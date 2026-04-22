package com.example.quotes_app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quotes_app.domain.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM favorite_recipes")
    fun getAllFavorites(): Flow<List<Recipe>>

    @Query("SELECT * FROM favorite_recipes LIMIT :limit")
    fun getFavoritesPaginated(limit: Int): Flow<List<Recipe>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipes WHERE idMeal = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(recipe: Recipe)

    @Delete
    suspend fun deleteFavorite(recipe: Recipe)
}
