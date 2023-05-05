package com.example.datahive.profile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE )
    suspend fun addUserToRoomDB (user : User)

    @Query("SELECT * FROM USER_TABLE")
    fun fetchUserName (): LiveData<List<User>>
}