package com.example.androidkfu2.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.androidkfu2.database.entities.User

@Dao
interface UserDao {

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

//    @Query("SELECT * FROM users WHERE userLogin = :login")
//    fun get(login: String) : LiveData<User>
//
//    @Query("SELECT * FROM users")
//    fun getAll(): List<LiveData<User>>
}