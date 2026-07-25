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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeFavoriteRecipeDao
    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setup() {
        fakeDao = FakeFavoriteRecipeDao()
        repository = RecipeRepository(FakeFavoriteRecipeApi(), fakeDao, FakeFavoriteOwnRecipeDao())
        viewModel = FavoriteViewModel(repository)
    }

    @Test
    fun favorites_returnsPaginatedFavoriteList() = runTest {
        fakeDao.favoritesList.addAll((1..15).map {
            Recipe("$it", "Recipe $it", "Category", "Area", "Inst", null)
        })

        val list = viewModel.favorites.first()
        assertEquals(10, list.size)
    }

    @Test
    fun loadMore_increasesLimitAndFetchesMore() = runTest {
        fakeDao.favoritesList.addAll((1..25).map {
            Recipe("$it", "Recipe $it", "Category", "Area", "Inst", null)
        })

        viewModel.loadMore()

        val list = viewModel.favorites.first()
        assertEquals(20, list.size)
    }
}

class FakeFavoriteRecipeApi : RecipeApi {
    override suspend fun searchRecipes(query: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRecipeById(id: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRandomRecipe() = Response.success(RecipeResponse(emptyList()))
}

class FakeFavoriteRecipeDao : RecipeDao {
    val favoritesList = mutableListOf<Recipe>()

    override fun getAllFavorites(): Flow<List<Recipe>> = flowOf(favoritesList)
    override fun getFavoritesPaginated(limit: Int): Flow<List<Recipe>> = flowOf(favoritesList.take(limit))
    override fun isFavorite(id: String): Flow<Boolean> = flowOf(favoritesList.any { it.idMeal == id })
    override suspend fun insertFavorite(recipe: Recipe) { favoritesList.add(recipe) }
    override suspend fun deleteFavorite(recipe: Recipe) { favoritesList.removeAll { it.idMeal == recipe.idMeal } }
}

class FakeFavoriteOwnRecipeDao : OwnRecipeDao {
    override fun getAllOwnRecipes(): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override fun getOwnRecipesPaginated(limit: Int): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override suspend fun insertOwnRecipe(recipe: OwnRecipe) {}
    override suspend fun deleteOwnRecipe(recipe: OwnRecipe) {}
}
