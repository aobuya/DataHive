package com.example.datahive.profile

import androidx.lifecycle.LiveData

class UserRepo(private val userDao: UserDao) {

    val readAllData: LiveData<List<User>> = userDao.fetchUserData()

    suspend fun addUser(user : User){
        userDao.addUserToRoomDB(user)
    }
}