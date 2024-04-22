package com.example.androidkfu2.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidkfu2.database.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)



    @Query("SELECT * FROM users_table WHERE userLogin = :login")
    fun get(login: String) : LiveData<User>

    @Query("SELECT * FROM users_table")
    fun getAll(): LiveData<List<User>>
}