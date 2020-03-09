package com.qbs.platereader.models

class UserCheckIn {
    companion object {
        val TABLE_USER_CHECKIN = "user_checkin"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "user_name"
        val PLATE_NUM = "plate_number"
        val COLUMN_TIMESTAMP = "timestamp"

        // user name + user vehicle name + user entry time
        val CREATE_TABLE_USER_CHECKIN = "CREATE TABLE " + TABLE_USER_CHECKIN + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," + PLATE_NUM + " TEXT," + COLUMN_TIMESTAMP + " date DEFAULT (datetime('now', 'localtime')))"
    }

    var id: Int = 0
    var userName: String? = null
    var plateNumber: String? = null
    var timestamp: String? = null

    override fun toString(): String {
        return "UserCheckIn(id=$id, userName=$userName, plateNumber=$plateNumber, timestamp=$timestamp)"
    }


}