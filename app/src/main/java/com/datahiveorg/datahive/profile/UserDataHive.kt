package com.datahiveorg.datahive.profile

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDataHive : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE : UserDataHive? = null

        fun getDataBase(context: Context) : UserDataHive {

            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance

            }
            synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    UserDataHive::class.java,
                    "user_table"
                ).build()
                INSTANCE= db
                return db

            }

        }
    }
}