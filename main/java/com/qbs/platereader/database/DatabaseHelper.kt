package com.qbs.platereader.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.qbs.platereader.models.UserCheckIn
import com.qbs.platereader.models.UserModel

// Database Version
private val DATABASE_VERSION = 1

// Database Name
private val DATABASE_NAME = "notes_db"

open class DatabaseHelper(context: Context):SQLiteOpenHelper(context,
    DATABASE_NAME,null,
    DATABASE_VERSION
){
    override fun onCreate(db: SQLiteDatabase) {
        print("[INFO] ${UserModel.CREATE_TABLE_USER}")
        print("[INFO] ${UserCheckIn.CREATE_TABLE_USER_CHECKIN}")
        db.execSQL(UserModel.CREATE_TABLE_USER)
        db.execSQL(UserCheckIn.CREATE_TABLE_USER_CHECKIN)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS "+ UserModel.TABLE_USER_ENTRY)
        db.execSQL("DROP TABLE IF EXISTS "+ UserCheckIn.TABLE_USER_CHECKIN)
        onCreate(db)
    }


}