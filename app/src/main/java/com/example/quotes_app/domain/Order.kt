package com.example.quotes_app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recipeId: String,
    val recipeName: String,
    val recipeThumb: String,
    val price: Double,
    val address: String,
    val orderDate: Long = System.currentTimeMillis()
)
