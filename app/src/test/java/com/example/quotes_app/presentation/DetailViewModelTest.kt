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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeRecipeDaoForDetail
    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        fakeDao = FakeRecipeDaoForDetail()
        repository = RecipeRepository(FakeRecipeApiForDetail(), fakeDao, FakeOwnRecipeDaoForDetail())
        viewModel = DetailViewModel(repository)
    }

    @Test
    fun isFavorite_returnsCorrectFavoriteStatus() = runTest {
        val sampleRecipe = Recipe("101", "Pasta", "Italian", "Main", "Cook pasta", null)
        fakeDao.insertFavorite(sampleRecipe)

        val isFav = viewModel.isFavorite("101").first()
        assertTrue(isFav)

        val isOtherFav = viewModel.isFavorite("999").first()
        assertFalse(isOtherFav)
    }

    @Test
    fun toggleFavorite_removesWhenCurrentlyFavorite() = runTest {
        val sampleRecipe = Recipe("102", "Burger", "Fast Food", "American", "Grill patty", null)
        fakeDao.insertFavorite(sampleRecipe)

        viewModel.toggleFavorite(sampleRecipe, isCurrentlyFavorite = true)

        val favorites = fakeDao.favoritesMap.values
        assertFalse(favorites.contains(sampleRecipe))
    }

    @Test
    fun toggleFavorite_insertsWhenNotCurrentlyFavorite() = runTest {
        val sampleRecipe = Recipe("103", "Pizza", "Fast Food", "Italian", "Bake pizza", null)

        viewModel.toggleFavorite(sampleRecipe, isCurrentlyFavorite = false)

        val favorites = fakeDao.favoritesMap.values
        assertTrue(favorites.contains(sampleRecipe))
    }
}

class FakeRecipeApiForDetail : RecipeApi {
    override suspend fun searchRecipes(query: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRecipeById(id: String) = Response.success(RecipeResponse(emptyList()))
    override suspend fun getRandomRecipe() = Response.success(RecipeResponse(emptyList()))
}

class FakeRecipeDaoForDetail : RecipeDao {
    val favoritesMap = mutableMapOf<String, Recipe>()

    override fun getAllFavorites(): Flow<List<Recipe>> = flowOf(favoritesMap.values.toList())
    override fun getFavoritesPaginated(limit: Int): Flow<List<Recipe>> = flowOf(favoritesMap.values.take(limit))
    override fun isFavorite(id: String): Flow<Boolean> = flowOf(favoritesMap.containsKey(id))

    override suspend fun insertFavorite(recipe: Recipe) {
        favoritesMap[recipe.idMeal] = recipe
    }

    override suspend fun deleteFavorite(recipe: Recipe) {
        favoritesMap.remove(recipe.idMeal)
    }
}

class FakeOwnRecipeDaoForDetail : OwnRecipeDao {
    override fun getAllOwnRecipes(): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override fun getOwnRecipesPaginated(limit: Int): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override suspend fun insertOwnRecipe(recipe: OwnRecipe) {}
    override suspend fun deleteOwnRecipe(recipe: OwnRecipe) {}
}
