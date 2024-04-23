package com.example.androidkfu2.database.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidkfu2.database.daos.UserDao
import com.example.androidkfu2.database.entities.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UserViewModel(val dao: UserDao): ViewModel() {

//    var newUserLogin = ""
//    var newUserPassword = ""

    val users = dao.getAll()

    fun addUser(newUserLogin: String, newUserPassword: String){
        viewModelScope.launch {
            val user = User()

            user.userLogin = newUserLogin
            user.password = newUserPassword
            dao.insert(user)
        }
    }


    suspend fun getUser(userLogin: String): User? {
        val res = viewModelScope.async {
            dao.get(userLogin)
        }

        return res.await()

    }


}