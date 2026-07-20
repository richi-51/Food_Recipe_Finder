package com.example.quotes_app.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quotes_app.domain.Order
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeDatabaseIntegrationTest {

    private lateinit var database: RecipeDatabase
    private lateinit var recipeDao: RecipeDao
    private lateinit var ownRecipeDao: OwnRecipeDao
    private lateinit var orderDao: OrderDao
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RecipeDatabase::class.java
        ).allowMainThreadQueries().build()

        recipeDao = database.recipeDao()
        ownRecipeDao = database.ownRecipeDao()
        orderDao = database.orderDao()
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun recipeDao_insertAndDeleteFavorite_updatesFavoriteState() = runBlocking {
        val recipe = Recipe(
            idMeal = "meal_1",
            strMeal = "Nasi Goreng",
            strCategory = "Main Course",
            strArea = "Indonesia",
            strInstructions = "Cook and serve",
            strMealThumb = "https://example.com/nasi-goreng.jpg"
        )

        recipeDao.insertFavorite(recipe)

        val favorites = recipeDao.getAllFavorites().first()
        val isFavorite = recipeDao.isFavorite(recipe.idMeal).first()

        assertEquals(1, favorites.size)
        assertEquals(recipe.idMeal, favorites.first().idMeal)
        assertTrue(isFavorite)

        recipeDao.deleteFavorite(recipe)

        val isFavoriteAfterDelete = recipeDao.isFavorite(recipe.idMeal).first()
        assertFalse(isFavoriteAfterDelete)
    }

    @Test
    fun ownRecipeDao_insertAndDeleteOwnRecipe_updatesList() = runBlocking {
        val ownRecipe = OwnRecipe(
            id = "own_1",
            title = "Soto Ayam",
            category = "Soup",
            instructions = "Boil chicken and spices",
            area = "Local",
            imagePath = null
        )

        ownRecipeDao.insertOwnRecipe(ownRecipe)

        val recipesAfterInsert = ownRecipeDao.getAllOwnRecipes().first()
        assertEquals(1, recipesAfterInsert.size)
        assertEquals("Soto Ayam", recipesAfterInsert.first().title)

        ownRecipeDao.deleteOwnRecipe(ownRecipe)

        val recipesAfterDelete = ownRecipeDao.getAllOwnRecipes().first()
        assertTrue(recipesAfterDelete.isEmpty())
    }

    @Test
    fun orderDao_getAllOrders_returnsSortedByDateDescending() = runBlocking {
        val olderOrder = Order(
            recipeId = "r_1",
            recipeName = "Bakso",
            recipeThumb = "https://example.com/bakso.jpg",
            price = 12.0,
            address = "Address 1",
            orderDate = 1_700_000_000_000
        )
        val newerOrder = Order(
            recipeId = "r_2",
            recipeName = "Rendang",
            recipeThumb = "https://example.com/rendang.jpg",
            price = 15.0,
            address = "Address 2",
            orderDate = 1_800_000_000_000
        )

        orderDao.insertOrder(olderOrder)
        orderDao.insertOrder(newerOrder)

        val orders = orderDao.getAllOrders().first()
        assertEquals(2, orders.size)
        assertEquals("Rendang", orders.first().recipeName)
        assertEquals("Bakso", orders.last().recipeName)
    }

    @Test
    fun userDao_insertGetAndUpdateUser_worksAsExpected() = runBlocking {
        val user = User(
            username = "johndoe",
            name = "John Doe",
            passwordHash = "secret"
        )

        userDao.insertUser(user)
        val inserted = userDao.getUserByUsername("johndoe")

        assertNotNull(inserted)
        assertEquals("John Doe", inserted?.name)

        val updatedUser = inserted!!.copy(name = "John Updated")
        userDao.updateUser(updatedUser)

        val fetchedAfterUpdate = userDao.getUserByUsername("johndoe")
        assertEquals("John Updated", fetchedAfterUpdate?.name)
    }
}
