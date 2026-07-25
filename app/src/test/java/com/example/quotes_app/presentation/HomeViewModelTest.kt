package com.example.quotes_app.presentation

import com.example.quotes_app.data.api.RecipeApi
import com.example.quotes_app.data.api.RecipeResponse
import com.example.quotes_app.data.local.OwnRecipeDao
import com.example.quotes_app.data.local.RecipeDao
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.RecipeRepository
import com.example.quotes_app.util.MainDispatcherRule
import com.example.quotes_app.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeApi: FakeRecipeApiForHome
    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        fakeApi = FakeRecipeApiForHome()
        repository = RecipeRepository(fakeApi, FakeRecipeDaoForHome(), FakeOwnRecipeDaoForHome())
    }

    @Test
    fun searchRecipes_withValidResults_returnsSuccess() = runTest {
        val sampleRecipes = listOf(
            Recipe("1", "Chicken Curry", "Chicken", "Asian", "Cook chicken", null),
            Recipe("2", "Beef Noodle", "Beef", "Asian", "Cook noodle", null)
        )
        fakeApi.recipesToReturn = sampleRecipes

        viewModel = HomeViewModel(repository)

        val state = viewModel.recipes.value
        assertTrue(state is Resource.Success)
        assertEquals(2, (state as Resource.Success).data?.size)
        assertEquals("Chicken Curry", state.data?.first()?.strMeal)
    }

    @Test
    fun searchRecipes_withEmptyResults_returnsError() = runTest {
        fakeApi.recipesToReturn = emptyList()

        viewModel = HomeViewModel(repository)

        val state = viewModel.recipes.value
        assertTrue(state is Resource.Error)
        assertEquals("No recipes found", (state as Resource.Error).message)
    }

    @Test
    fun loadMore_withPagination_loadsNextPage() = runTest {
        val manyRecipes = (1..15).map {
            Recipe("$it", "Recipe $it", "Category", "Area", "Inst", null)
        }
        fakeApi.recipesToReturn = manyRecipes

        viewModel = HomeViewModel(repository)

        val firstState = viewModel.recipes.value as Resource.Success
        assertEquals(10, firstState.data?.size)
        assertTrue(viewModel.hasNextPage.value)

        viewModel.loadMore()

        val secondState = viewModel.recipes.value as Resource.Success
        assertEquals(15, secondState.data?.size)
        assertFalse(viewModel.hasNextPage.value)
    }
}

class FakeRecipeApiForHome : RecipeApi {
    var recipesToReturn: List<Recipe> = emptyList()

    override suspend fun searchRecipes(query: String): Response<RecipeResponse> {
        return Response.success(RecipeResponse(recipesToReturn))
    }

    override suspend fun getRecipeById(id: String): Response<RecipeResponse> {
        return Response.success(RecipeResponse(recipesToReturn))
    }

    override suspend fun getRandomRecipe(): Response<RecipeResponse> {
        return Response.success(RecipeResponse(recipesToReturn))
    }
}

class FakeRecipeDaoForHome : RecipeDao {
    override fun getAllFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
    override fun getFavoritesPaginated(limit: Int): Flow<List<Recipe>> = flowOf(emptyList())
    override fun isFavorite(id: String): Flow<Boolean> = flowOf(false)
    override suspend fun insertFavorite(recipe: Recipe) {}
    override suspend fun deleteFavorite(recipe: Recipe) {}
}

class FakeOwnRecipeDaoForHome : OwnRecipeDao {
    override fun getAllOwnRecipes(): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override fun getOwnRecipesPaginated(limit: Int): Flow<List<OwnRecipe>> = flowOf(emptyList())
    override suspend fun insertOwnRecipe(recipe: OwnRecipe) {}
    override suspend fun deleteOwnRecipe(recipe: OwnRecipe) {}
}
