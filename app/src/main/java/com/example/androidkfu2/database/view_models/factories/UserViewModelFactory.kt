package com.example.androidkfu2.database.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.androidkfu2.database.daos.UserDao
import com.example.androidkfu2.database.view_models.UserViewModel
import java.lang.IllegalArgumentException

class UserViewModelFactory(private val dao: UserDao): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java)){
            return UserViewModel(dao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}