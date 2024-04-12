package com.example.androidkfu2.database.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidkfu2.database.daos.UserDao
import com.example.androidkfu2.database.entities.User
import kotlinx.coroutines.launch

class UserViewModel(val dao: UserDao): ViewModel() {



    fun addUser(newUserLogin:String, newUserPassword:String){
        viewModelScope.launch {
            val user = User()

            user.userLogin = newUserLogin
            user.password = newUserPassword
            println(newUserLogin)
            println(newUserPassword)
            dao.insert(user)
        }

    }


}