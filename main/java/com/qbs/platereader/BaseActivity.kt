package com.qbs.platereader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import com.qbs.platereader.camera.CameraSource
import com.qbs.platereader.camera.CameraSourcePreview
import com.qbs.platereader.database.UserCheckInDao
import com.qbs.platereader.database.UserCheckInDaoImpl
import com.qbs.platereader.database.UserDao
import com.qbs.platereader.database.UserDaoImpl
import com.qbs.platereader.models.UserCheckIn
import com.qbs.platereader.models.UserModel
import com.qbs.platereader.others.GraphicOverlay
import com.qbs.platereader.text_detection.TextRecognitionProcessor
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnTextProcessing {
    override fun getProcessedText(text: String) {
        print("getProcessedText: $text")
        println("tts?.isSpeaking()"+ tts?.isSpeaking())
        println("isSpeaking $isSpeaking")
        if (!tts!!.isSpeaking()) {
            print("isSpeaking: $isSpeaking")
            val list = text.split("\n")
            // get the list of plate numbers
            // traverse and get last four digits of each plate number

            list.forEach { s ->
                var str = Utils.removeWhiteSpace(s)
//                if (str.length<=4){
//
//                }else{
//                    str = str.substring(str.length - 4)
//                }

                val md = getMatchPlateNumber(str)
                if (md != null){
                    plate_number = md.plateNumber!!
                    val note = "${md.userName?.toLowerCase()} Flat Number ${md.flatNum} ${md.aprtNum?.toLowerCase()}"
                    tts?.speak(
                        note,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
                isSpeaking = false
            }
        }

    }

    private fun getMatchPlateNumber(string: String): UserModel?{
        if (!string.isEmpty()){
            val list = userDao?.getUsersList()
            println("getMatchPlateNumber")
            println(list)
            var i:UserModel? = null
            list?.forEach {user->
                if (!user.plateNumber.isNullOrEmpty()) {
                    if (user.plateNumber.equals(string, true)){
                        i = user
                    }
                }
            }
            return i
        }
        return null
    }


    /*
    * DL 7CQ 1939 -> Anchit Sharma -> Flat Number 303, Quantafic Business Solutions
    * 3SAM123
    * YOURPL8*/
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var picture: Button? = null
    private val TAG = BaseActivity::class.java.simpleName.trim { it <= ' ' }
    private var tts: TextToSpeech? = null
    private var textrecognitionprocessor: TextRecognitionProcessor? = null
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    private var stopTTS = true
    private var isSpeaking = true
    val REQUEST_CAM_PERMISSION = 1001
    private val REQUEST_TAKE_PHOTO = 1
    private var mCurrentPhotoPath: String? = null
    private var plate_number: String = ""
    private var userDao: UserDao? = null
    private var userCheckDao: UserCheckInDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        userDao = UserDaoImpl(this)
        userCheckDao = UserCheckInDaoImpl(this)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.setLanguage(Locale.UK)
            }
        })


        preview = findViewById(R.id.camera_source_preview) as CameraSourcePreview
        if (preview == null) {
            Log.d(TAG, "Preview is null")
        }
        graphicOverlay = findViewById(R.id.graphics_overlay) as GraphicOverlay
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }
        picture = findViewById(R.id.picture)
        textrecognitionprocessor = TextRecognitionProcessor(this)
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            createCameraSource()
            startCameraSource()
        }

        picture?.setOnClickListener {
            if (checkPermission()) takePhoto()
        }
    }


    private fun checkPermission(): Boolean {
        val res1 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val res2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (res1 && res2) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CAM_PERMISSION
            )
            return false
        }
    }

    private fun takePhoto() {
        dispatchToTakePhotoIntent()
    }
    private fun dispatchToTakePhotoIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the file where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this@BaseActivity, "com.qbs.platereader.provider", photoFile))//Uri.fromFile(photoFile)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun createImageFile(): File? {
        // create an image file name
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timestamp}_"
        val storageDir = "${Environment.getExternalStorageDirectory()}/MeterReadApp"
        val dir = File(storageDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val image = File("${storageDir}/${imageFileName}.jpg")
        mCurrentPhotoPath = image.absolutePath
        return image
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_TAKE_PHOTO) && resultCode == Activity.RESULT_OK) {
            println("[INFO] "+data?.data)
            println("[INFO] "+mCurrentPhotoPath)
            var uri = data?.data;
            if (null == uri) {
                println("[INFO] "+mCurrentPhotoPath)
                uri = Uri.fromFile(File(mCurrentPhotoPath))
                // send this uri plus plate number to next activity

            }

            val intent = Intent(this@BaseActivity, ProfileActivity::class.java)
            print("[INFO] $plate_number")
            intent.putExtra(ProfileActivity.EXTRA_TEXT_DATA, Utils.removeWhiteSpace(plate_number))
            intent.putExtra(ProfileActivity.EXTRA_PIC_URI, uri.toString())
            startActivity(intent)
            val user = UserCheckIn()
            user.plateNumber = Utils.removeWhiteSpace(plate_number)
            userCheckDao?.saveCustomer(user)
            finish()

        }
    }
    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        startCameraSource()
        handlerThread = HandlerThread("cameraActivity")
        handlerThread?.start()
        handler = Handler(handlerThread?.getLooper())
        handler?.post {
            while (stopTTS) {
                try {
                    Thread.sleep(5000)
                    isSpeaking = true
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /** Stops the camera.  */
    override fun onPause() {
        super.onPause()
        preview?.stop()
        handlerThread?.quitSafely()
        stopTTS = false
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (textrecognitionprocessor != null) {
            textrecognitionprocessor = null
        }
        if (cameraSource != null) {
            cameraSource?.release()
        }
        stopTTS = false
    }

    private fun createCameraSource() {

        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay)
            cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
        }

        cameraSource?.setMachineLearningFrameProcessor(textrecognitionprocessor)
    }


    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createCameraSource()
                startCameraSource()
            }
        }
    }


    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                preview?.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
            }

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // add user here
                val intent = Intent(this@BaseActivity, AddUserActivity::class.java)
                startActivity(intent)
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }




}
