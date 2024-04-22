package com.example.androidkfu2.database.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidkfu2.database.daos.UserDao
import com.example.androidkfu2.database.entities.User
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


    fun getUser(userLogin: String): User? {
//        val usrs = dao.getAll().value
//        println(usrs?.size)
//
//        if (usrs != null) {
//            for(u in usrs){
//                println(u.userLogin)
//            }
//        }

        val usr = dao.get(userLogin).value
        println(usr?.userLogin)
        return usr
    }


}