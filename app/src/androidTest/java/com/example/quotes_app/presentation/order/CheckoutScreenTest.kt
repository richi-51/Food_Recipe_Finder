package com.example.quotes_app.presentation.order

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quotes_app.data.local.OrderDao
import com.example.quotes_app.domain.Order
import com.example.quotes_app.domain.OrderRepository
import com.example.quotes_app.domain.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckoutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkoutScreen_shouldDisplayAddressFieldAndPlaceOrderButton() {
        val recipe = Recipe("1", "Rendang", "Meat", "Indonesian", "Cook slow", null)
        val repository = OrderRepository(FakeCheckoutOrderDao())
        val viewModel = OrderViewModel(repository)

        composeTestRule.setContent {
            CheckoutScreen(
                recipe = recipe,
                viewModel = viewModel,
                onNavigateBack = {},
                onCheckoutSuccess = {}
            )
        }

        composeTestRule.onNodeWithText("Checkout").assertExists()
        composeTestRule.onNodeWithTag("addressField").assertExists()
        composeTestRule.onNodeWithTag("placeOrderButton").assertExists()
    }

    @Test
    fun checkoutScreen_enteringAddress_enablesPlaceOrderButton() {
        val recipe = Recipe("1", "Rendang", "Meat", "Indonesian", "Cook slow", null)
        val repository = OrderRepository(FakeCheckoutOrderDao())
        val viewModel = OrderViewModel(repository)

        composeTestRule.setContent {
            CheckoutScreen(
                recipe = recipe,
                viewModel = viewModel,
                onNavigateBack = {},
                onCheckoutSuccess = {}
            )
        }

        composeTestRule.onNodeWithTag("addressField").performTextInput("Jl. Sudirman No. 10")
        composeTestRule.onNodeWithTag("placeOrderButton").assertIsEnabled()
    }
}

class FakeCheckoutOrderDao : OrderDao {
    override fun getAllOrders(): Flow<List<Order>> = flowOf(emptyList())
    override suspend fun insertOrder(order: Order) {}
}
