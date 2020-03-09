package com.qbs.platereader

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.qbs.platereader.database.UserCheckInDao
import com.qbs.platereader.database.UserCheckInDaoImpl
import com.qbs.platereader.database.UserDao
import com.qbs.platereader.database.UserDaoImpl
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    companion object{
        val EXTRA_TEXT_DATA = "extra_text_data"
        val EXTRA_PIC_URI = "extra_pic_uri"
    }
    data class LabelMap(val lName: String, val lValue: String);
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var dataList = arrayListOf<LabelMap>()

    private var userDao: UserDao?= null
    private var userCheckDao: UserCheckInDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        userDao = UserDaoImpl(this)
        userCheckDao = UserCheckInDaoImpl(this)

        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewAdapter = MyAdapter(dataList)
        createDataset(intent.getStringExtra(EXTRA_TEXT_DATA))
        recyclerView = findViewById(R.id.my_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter

        if (!intent.getStringExtra(EXTRA_PIC_URI).isNullOrEmpty()){
            var ois: InputStream? = null
            try {
                ois = getContentResolver().openInputStream(Uri.parse(intent.getStringExtra(EXTRA_PIC_URI)))
                val bitmap = BitmapFactory.decodeStream(ois)
                imageView.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (ois != null) {
                    try {
                        ois.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        }


    }

    private fun createDataset(stringExtra: String) {

        val user = userDao?.getUserByPlate(stringExtra)
        val uc = userCheckDao?.getUserByPlate(stringExtra)
        dataList.add(LabelMap("Name",user?.userName!!))
        dataList.add(LabelMap("Plate Number ",user?.plateNumber!!))
        dataList.add(LabelMap("Apartment/Flat No.",user.flatNum!!))
        dataList.add(LabelMap("Society/Area",user.aprtNum!!))
        dataList.add(LabelMap("Mobile",user.mobNum!!))
        if (uc != null) {
            dataList.add(LabelMap("Punch time", uc.timestamp!!))
        }
    }
}