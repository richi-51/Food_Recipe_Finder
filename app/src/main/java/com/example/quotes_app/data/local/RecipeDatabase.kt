package com.example.quotes_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.Order
import com.example.quotes_app.domain.User

@Database(entities = [Recipe::class, OwnRecipe::class, Order::class, User::class], version = 4, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun ownRecipeDao(): OwnRecipeDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
}
