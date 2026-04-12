package com.example.quotes_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quotes_app.domain.Recipe

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
