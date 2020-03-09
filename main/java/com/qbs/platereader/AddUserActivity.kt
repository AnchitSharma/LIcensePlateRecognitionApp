package com.qbs.platereader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.qbs.platereader.database.UserDao
import com.qbs.platereader.database.UserDaoImpl
import com.qbs.platereader.models.UserModel
import kotlinx.android.synthetic.main.activity_add_user.*

class AddUserActivity : AppCompatActivity() {

    private var userDao: UserDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)
        userDao = UserDaoImpl(this)
        save_user?.setOnClickListener {
            // get user object here
            if (unameEditText.text.toString().isEmpty()){
                unameEditTextLay.error = "Please Enter name"
                return@setOnClickListener
            }
            if (vehicle_no.text.toString().isEmpty()){
                vehicle_noLay.error = "Please enter vehicle number"
                return@setOnClickListener
            }
            if (aprt_no.text.toString().isEmpty()){
                aprt_noLay.error = "Please enter Apartment or Flat number"
                return@setOnClickListener
            }
            if (mob_no.text.toString().isEmpty()){
                mob_noLay.error = "Please enter Mobile Number"
                return@setOnClickListener
            }


            val user = UserModel()
            user.userName = unameEditText.text.toString()
            user.plateNumber = vehicle_no.text.toString()
            user.flatNum = aprt_no.text.toString()
            user.mobNum = mob_no.text.toString()
            user.aprtNum = area_so_no?.text?.toString()
            userDao?.saveCustomer(user)

            unameEditText.text = null
            vehicle_no.text = null
            aprt_no.text = null
            area_so_no?.text = null
            mob_no.text = null

        }
    }
}
