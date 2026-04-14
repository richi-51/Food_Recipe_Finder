package com.example.quotes_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.OwnRecipe

@Database(entities = [Recipe::class, OwnRecipe::class], version = 2, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun ownRecipeDao(): OwnRecipeDao
}
