package com.qbs.platereader.database

import android.content.ContentValues
import android.content.Context
import com.qbs.platereader.models.UserCheckIn

open class UserCheckInDaoImpl(context: Context) : UserCheckInDao {
    val context: Context
    val databaseHelper: DatabaseHelper

    init {
        this.context = context
        this.databaseHelper = DatabaseHelper(this.context)
    }


    override fun getUsersList(): List<UserCheckIn>? {
        return null
    }

    override fun saveCustomer(usercheckin: UserCheckIn): Boolean {
        val db = databaseHelper.getWritableDatabase()
        val cv = ContentValues()
        cv.put(UserCheckIn.COLUMN_NAME, usercheckin.userName)
        cv.put(UserCheckIn.PLATE_NUM, usercheckin.plateNumber)
        val success = db.insert(UserCheckIn.TABLE_USER_CHECKIN, null, cv).toInt()
        return success > -1

    }

    override fun getUserByPlate(platNo: String): UserCheckIn? {
        val select = UserCheckIn.PLATE_NUM + " = ?"
        val db = databaseHelper.getReadableDatabase()
        val cursor = db.query(UserCheckIn.TABLE_USER_CHECKIN, null, select, arrayOf(platNo), null, null, null)
        val c = UserCheckIn()
        while (cursor.moveToNext()){
            c.id = cursor.getInt(cursor.getColumnIndex(UserCheckIn.COLUMN_ID))
            c.userName = cursor.getString(cursor.getColumnIndex(UserCheckIn.COLUMN_NAME))
            c.plateNumber = cursor.getString(cursor.getColumnIndex(UserCheckIn.PLATE_NUM))
            c.timestamp = cursor.getString(cursor.getColumnIndex(UserCheckIn.COLUMN_TIMESTAMP))
        }
        cursor.close()
        db.close()
        return c
    }

}