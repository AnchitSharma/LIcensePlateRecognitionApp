package com.qbs.platereader.database

import android.content.ContentValues
import android.content.Context
import com.qbs.platereader.models.UserModel

open class UserDaoImpl(context: Context): UserDao{

    private val context: Context
    private val databaseHelper: DatabaseHelper

    init {
        this.context = context
        this.databaseHelper = DatabaseHelper(context)
    }


    override fun getUsersList(): List<UserModel> {
        val users = arrayListOf<UserModel>()
        val db = databaseHelper.getReadableDatabase()
        val query = "SELECT * FROM ${UserModel.TABLE_USER_ENTRY}"
        val cursor = db.rawQuery(query, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val c = UserModel()
                c.id = cursor.getInt(cursor.getColumnIndex(UserModel.COLUMN_ID))
                c.userName = cursor.getString(cursor.getColumnIndex(UserModel.COLUMN_NAME))
                c.plateNumber = cursor.getString(cursor.getColumnIndex(UserModel.PLATE_NUM))
                c.timestamp = cursor.getString(cursor.getColumnIndex(UserModel.COLUMN_TIMESTAMP))
                c.mobNum = cursor.getString(cursor.getColumnIndex(UserModel.MOB_NUM))
                c.flatNum = cursor.getString(cursor.getColumnIndex(UserModel.FLAT_NO))
                c.aprtNum = cursor.getString(cursor.getColumnIndex(UserModel.APRT_SOC_NAME))
                users.add(c)
            }
        }

        if (cursor != null) {
            cursor.close()
        }
        db.close()
        return users
    }

    override fun saveCustomer(userModel: UserModel): Boolean {
        val db = databaseHelper.getWritableDatabase()
        val cv = ContentValues()
        cv.put(UserModel.COLUMN_NAME, userModel.userName)
        cv.put(UserModel.PLATE_NUM, userModel.plateNumber)
        cv.put(UserModel.MOB_NUM, userModel.mobNum)
        cv.put(UserModel.FLAT_NO, userModel.flatNum)
        cv.put(UserModel.APRT_SOC_NAME, userModel.aprtNum)
        val success = db.insert(UserModel.TABLE_USER_ENTRY, null, cv).toInt()
        return success > -1
    }

    override fun getUserByPlate(platNo: String): UserModel {
        val select = UserModel.PLATE_NUM + " = ?"
        val db = databaseHelper.getReadableDatabase()
        val cursor = db.query(UserModel.TABLE_USER_ENTRY, null, select, arrayOf(platNo), null, null, null)
        val c = UserModel()
        while (cursor.moveToNext()){
            c.id = cursor.getInt(cursor.getColumnIndex(UserModel.COLUMN_ID))
            c.userName = cursor.getString(cursor.getColumnIndex(UserModel.COLUMN_NAME))
            c.plateNumber = cursor.getString(cursor.getColumnIndex(UserModel.PLATE_NUM))
            c.mobNum = cursor.getString(cursor.getColumnIndex(UserModel.MOB_NUM))
            c.flatNum = cursor.getString(cursor.getColumnIndex(UserModel.FLAT_NO))
            c.aprtNum = cursor.getString(cursor.getColumnIndex(UserModel.APRT_SOC_NAME))
            c.timestamp = cursor.getString(cursor.getColumnIndex(UserModel.COLUMN_TIMESTAMP))
        }
        cursor.close()
        db.close()
        return c
    }

}