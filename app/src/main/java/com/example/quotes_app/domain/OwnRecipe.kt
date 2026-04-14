package com.example.quotes_app.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "own_recipes")
data class OwnRecipe(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val instructions: String,
    val area: String? = "Local",
    val imagePath: String? = null
) : Parcelable
