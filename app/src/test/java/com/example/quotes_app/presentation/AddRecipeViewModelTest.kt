package com.example.quotes_app.presentation

import com.example.quotes_app.data.api.RecipeApi
import com.example.quotes_app.data.api.RecipeResponse
import com.example.quotes_app.data.local.OwnRecipeDao
import com.example.quotes_app.data.local.RecipeDao
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.RecipeRepository
import com.example.quotes_app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AddRecipeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeOwnRecipeDao: FakeOwnRecipeDaoForAdd
    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: AddRecipeViewModel

    @Before
    fun setup() {
        fakeOwnRecipeDao = FakeOwnRecipeDaoForAdd()
        repository = RecipeRepository(FakeRecipeApiForAdd(), FakeRecipeDaoForAdd(), fakeOwnRecipeDao)
        viewModel = AddRecipeViewModel(repository)
    }

    @Test
    fun saveRecipe_withNewRecipe_insertsOwnRecipe() = runTest {
        viewModel.saveRecipe("Nasi Uduk", "Main", "Cook with coconut milk", null)

        assertEquals(1, fakeOwnRecipeDao.ownRecipes.size)
        val saved = fakeOwnRecipeDao.ownRecipes.first()
        assertEquals("Nasi Uduk", saved.title)
        assertEquals("Main", saved.category)
    }

    @Test
    fun saveRecipe_withExistingId_updatesOwnRecipe() = runTest {
        val existingId = "existing_123"
        viewModel.setEditingId(existingId)
        viewModel.saveRecipe("Updated Nasi Uduk", "Main", "Updated recipe", "path/to/img")

        assertEquals(1, fakeOwnRecipeDao.ownRecipes.size)
        val saved = fakeOwnRecipeDao.ownRecipes.first()
        assertEquals(existingId, saved.id)
        assertEquals("Updated Nasi Uduk", saved.title)
        assertEquals("path/to/img", saved.imagePath)
    }
}

class FakeRecipeApiForAdd : RecipeApi {
    override suspend fun searchRecipes(query: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRecipeById(id: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRandomRecipe() = Response.success(RecipeResponse(emptyList()))
}

class FakeRecipeDaoForAdd : RecipeDao {
    override fun getAllFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
    override fun getFavoritesPaginated(limit: Int): Flow<List<Recipe>> = flowOf(emptyList())
    override fun isFavorite(id: String): Flow<Boolean> = flowOf(false)
    override suspend fun insertFavorite(recipe: Recipe) {}
    override suspend fun deleteFavorite(recipe: Recipe) {}
}

class FakeOwnRecipeDaoForAdd : OwnRecipeDao {
    val ownRecipes = mutableListOf<OwnRecipe>()

    override fun getAllOwnRecipes(): Flow<List<OwnRecipe>> = flowOf(ownRecipes)
    override fun getOwnRecipesPaginated(limit: Int): Flow<List<OwnRecipe>> = flowOf(ownRecipes.take(limit))

    override suspend fun insertOwnRecipe(recipe: OwnRecipe) {
        ownRecipes.removeAll { it.id == recipe.id }
        ownRecipes.add(recipe)
    }

    override suspend fun deleteOwnRecipe(recipe: OwnRecipe) {
        ownRecipes.removeAll { it.id == recipe.id }
    }
}
