package com.qbs.platereader.database

import com.qbs.platereader.models.UserCheckIn
import com.qbs.platereader.models.UserModel

// user entry
// get user list

public interface UserDao{
    fun getUsersList():List<UserModel>
    fun saveCustomer(userModel: UserModel):Boolean
    fun getUserByPlate(platNo: String):UserModel
}


public interface UserCheckInDao{
    fun getUsersList():List<UserCheckIn>?
    fun saveCustomer(usercheckin: UserCheckIn):Boolean
    fun getUserByPlate(platNo: String):UserCheckIn?
}

