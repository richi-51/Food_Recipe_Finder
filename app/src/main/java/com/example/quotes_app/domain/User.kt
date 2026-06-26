package com.example.quotes_app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val username: String,
    val name: String,
    val passwordHash: String // We will just store plain text or simple hash for dummy
)
