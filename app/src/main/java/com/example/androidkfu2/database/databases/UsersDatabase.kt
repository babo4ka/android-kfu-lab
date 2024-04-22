package com.example.androidkfu2.database.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidkfu2.database.daos.UserDao
import com.example.androidkfu2.database.entities.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UsersDatabase: RoomDatabase() {

    abstract val userDao: UserDao

    companion object{
        @Volatile
        private var INSTANCE: UsersDatabase? = null

        fun getInstance(context: Context): UsersDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context,
                        UsersDatabase::class.java,
                        "users_database"
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}