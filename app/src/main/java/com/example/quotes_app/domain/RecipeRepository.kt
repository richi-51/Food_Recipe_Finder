package com.example.quotes_app.domain

import com.example.quotes_app.data.api.RecipeApi
import com.example.quotes_app.data.local.RecipeDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val api: RecipeApi,
    private val dao: RecipeDao
) {
    suspend fun searchRecipes(query: String) = api.searchRecipes(query)

    suspend fun getRecipeById(id: String) = api.getRecipeById(id)

    fun getAllFavorites(): Flow<List<Recipe>> = dao.getAllFavorites()

    fun isFavorite(id: String): Flow<Boolean> = dao.isFavorite(id)

    suspend fun insertFavorite(recipe: Recipe) {
        dao.insertFavorite(recipe)
    }

    suspend fun deleteFavorite(recipe: Recipe) {
        dao.deleteFavorite(recipe)
    }
}
