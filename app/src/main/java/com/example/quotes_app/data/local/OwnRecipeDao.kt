package com.example.quotes_app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quotes_app.domain.OwnRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnRecipeDao {
    @Query("SELECT * FROM own_recipes")
    fun getAllOwnRecipes(): Flow<List<OwnRecipe>>

    @Query("SELECT * FROM own_recipes LIMIT :limit")
    fun getOwnRecipesPaginated(limit: Int): Flow<List<OwnRecipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnRecipe(recipe: OwnRecipe)

    @Delete
    suspend fun deleteOwnRecipe(recipe: OwnRecipe)
}
