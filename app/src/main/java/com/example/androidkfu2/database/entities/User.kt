package com.example.androidkfu2.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class User(
    @PrimaryKey
    var userLogin: String = "",

    var password: String = ""

)