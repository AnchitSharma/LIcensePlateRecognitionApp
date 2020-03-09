package com.qbs.platereader.models

open class UserModel {
    companion object {
        val TABLE_USER_ENTRY = "user_entry"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "user_name"
        val PLATE_NUM = "plate_number"
        val MOB_NUM = "mob_num"
        val FLAT_NO = "flat_num"
        val APRT_SOC_NAME = "aprt_soc_name"
        val COLUMN_TIMESTAMP = "timestamp"

        val CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER_ENTRY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," +
                MOB_NUM + " TEXT," + FLAT_NO + " TEXT," + APRT_SOC_NAME + " TEXT," +
                PLATE_NUM + " TEXT," + COLUMN_TIMESTAMP + " date DEFAULT (datetime('now', 'localtime')))"

    }

    var id: Int = 0
    var userName: String? = null
    var plateNumber: String? = null
    var mobNum: String? = null
    var flatNum: String? = null
    var aprtNum: String? = null
    var timestamp: String? = null

    override fun toString(): String {
        return "UserModel(id=$id, userName=$userName, plateNumber=$plateNumber, timestamp=$timestamp)"
    }


}













