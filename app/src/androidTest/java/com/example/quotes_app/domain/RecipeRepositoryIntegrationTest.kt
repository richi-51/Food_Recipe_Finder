package com.example.quotes_app.domain

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quotes_app.data.api.RecipeApi
import com.example.quotes_app.data.api.RecipeResponse
import com.example.quotes_app.data.local.RecipeDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class RecipeRepositoryIntegrationTest {

    private lateinit var database: RecipeDatabase
    private lateinit var repository: RecipeRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RecipeDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RecipeRepository(
            api = FakeRecipeApi(),
            dao = database.recipeDao(),
            ownRecipeDao = database.ownRecipeDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun repository_favoriteAndOwnRecipe_localOperationsWork() = runBlocking {
        val recipe = Recipe(
            idMeal = "meal_42",
            strMeal = "Mie Goreng",
            strCategory = "Main Course",
            strArea = "Indonesia",
            strInstructions = "Cook noodles",
            strMealThumb = "https://example.com/mie.jpg"
        )
        val ownRecipe = OwnRecipe(
            id = "own_42",
            title = "Ayam Bakar",
            category = "Grill",
            instructions = "Grill chicken",
            area = "Local",
            imagePath = null
        )

        repository.insertFavorite(recipe)
        repository.insertOwnRecipe(ownRecipe)

        val favorites = repository.getAllFavorites().first()
        val ownRecipes = repository.getAllOwnRecipes().first()
        val isFavorite = repository.isFavorite(recipe.idMeal).first()

        assertEquals(1, favorites.size)
        assertEquals("Mie Goreng", favorites.first().strMeal)
        assertEquals(1, ownRecipes.size)
        assertEquals("Ayam Bakar", ownRecipes.first().title)
        assertTrue(isFavorite)

        repository.deleteFavorite(recipe)
        repository.deleteOwnRecipe(ownRecipe)

        assertTrue(repository.getAllFavorites().first().isEmpty())
        assertTrue(repository.getAllOwnRecipes().first().isEmpty())
    }
}

private class FakeRecipeApi : RecipeApi {
    override suspend fun searchRecipes(query: String): Response<RecipeResponse> {
        return Response.success(RecipeResponse(emptyList()))
    }

    override suspend fun getRecipeById(id: String): Response<RecipeResponse> {
        return Response.success(RecipeResponse(emptyList()))
    }

    override suspend fun getRandomRecipe(): Response<RecipeResponse> {
        return Response.success(RecipeResponse(emptyList()))
    }
}
