package com.example.quotes_app.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quotes_app.data.api.RecipeApi
import com.example.quotes_app.data.api.RecipeResponse
import com.example.quotes_app.data.local.OwnRecipeDao
import com.example.quotes_app.data.local.RecipeDao
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailScreen_shouldDisplayRecipeTitleAndButtons() {
        val recipe = Recipe("101", "Chicken Noodle Soup", "Soup", "Asian", "Boil chicken and noodles", "https://example.com/thumb.jpg")
        val repository = RecipeRepository(FakeDetailApi(), FakeDetailRecipeDao(), FakeDetailOwnDao())
        val viewModel = DetailViewModel(repository)

        composeTestRule.setContent {
            DetailScreen(
                recipe = recipe,
                viewModel = viewModel,
                onNavigateBack = {},
                onOrderClick = {}
            )
        }

        composeTestRule.onNodeWithText("Chicken Noodle Soup").assertExists()
        composeTestRule.onNodeWithTag("favoriteFab").assertExists()
        composeTestRule.onNodeWithTag("orderIngredientsButton").assertExists()
    }

    @Test
    fun detailScreen_orderIngredientsButton_triggersCallback() {
        var orderClicked = false
        val recipe = Recipe("102", "Spaghetti Carbonara", "Pasta", "Italian", "Cook pasta", null)
        val repository = RecipeRepository(FakeDetailApi(), FakeDetailRecipeDao(), FakeDetailOwnDao())
        val viewModel = DetailViewModel(repository)

        composeTestRule.setContent {
            DetailScreen(
                recipe = recipe,
                viewModel = viewModel,
                onNavigateBack = {},
                onOrderClick = { orderClicked = true }
            )
        }

        composeTestRule.onNodeWithTag("orderIngredientsButton").performClick()
        assertTrue(orderClicked)
    }
}

class FakeDetailApi : RecipeApi {
    override suspend fun searchRecipes(query: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRecipeById(id: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRandomRecipe() = Response.success(RecipeResponse(emptyList()))
}

class FakeDetailRecipeDao : RecipeDao {
    override fun getAllFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
    override fun getFavoritesPaginated(limit: Int): Flow<List<Recipe>> = flowOf(emptyList())
    override fun isFavorite(id: String): Flow<Boolean> = flowOf(false)
    override suspend fun insertFavorite(recipe: Recipe) {}
    override suspend fun deleteFavorite(recipe: Recipe) {}
}

class FakeDetailOwnDao : OwnRecipeDao {
    override fun getAllOwnRecipes(): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override fun getOwnRecipesPaginated(limit: Int): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override suspend fun insertOwnRecipe(recipe: OwnRecipe) {}
    override suspend fun deleteOwnRecipe(recipe: OwnRecipe) {}
}
