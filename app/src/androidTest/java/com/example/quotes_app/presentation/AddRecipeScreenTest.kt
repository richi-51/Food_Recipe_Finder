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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class AddRecipeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addRecipeScreen_shouldDisplayAllFieldsAndSaveButton() {
        val repository = RecipeRepository(FakeAddApi(), FakeAddRecipeDao(), FakeAddOwnDao())
        val viewModel = AddRecipeViewModel(repository)

        composeTestRule.setContent {
            AddRecipeScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag("recipeNameField").assertExists()
        composeTestRule.onNodeWithTag("categoryField").assertExists()
        composeTestRule.onNodeWithTag("instructionsField").assertExists()
        composeTestRule.onNodeWithTag("saveRecipeButton").assertExists()
    }

    @Test
    fun addRecipeScreen_fillingForm_enablesSaveButton() {
        val repository = RecipeRepository(FakeAddApi(), FakeAddRecipeDao(), FakeAddOwnDao())
        val viewModel = AddRecipeViewModel(repository)

        composeTestRule.setContent {
            AddRecipeScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithTag("recipeNameField").performTextInput("Soto Ayam")
        composeTestRule.onNodeWithTag("categoryField").performTextInput("Soup")
        composeTestRule.onNodeWithTag("instructionsField").performTextInput("Boil chicken")

        composeTestRule.onNodeWithTag("saveRecipeButton").assertIsEnabled()
    }
}

class FakeAddApi : RecipeApi {
    override suspend fun searchRecipes(query: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRecipeById(id: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRandomRecipe() = Response.success(RecipeResponse(emptyList()))
}

class FakeAddRecipeDao : RecipeDao {
    override fun getAllFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
    override fun getFavoritesPaginated(limit: Int): Flow<List<Recipe>> = flowOf(emptyList())
    override fun isFavorite(id: String): Flow<Boolean> = flowOf(false)
    override suspend fun insertFavorite(recipe: Recipe) {}
    override suspend fun deleteFavorite(recipe: Recipe) {}
}

class FakeAddOwnDao : OwnRecipeDao {
    override fun getAllOwnRecipes(): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override fun getOwnRecipesPaginated(limit: Int): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override suspend fun insertOwnRecipe(recipe: OwnRecipe) {}
    override suspend fun deleteOwnRecipe(recipe: OwnRecipe) {}
}
