package com.example.datahive.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<User>>
    private val repo: UserRepo

    init {
        val userDao = UserDataHive.getDataBase(application).userDao()
        repo = UserRepo(userDao)
        readAllData = repo.readAllData
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addUser(user)
        }
    }
}