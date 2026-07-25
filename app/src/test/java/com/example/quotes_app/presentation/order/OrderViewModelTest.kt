package com.example.quotes_app.presentation.order

import com.example.quotes_app.data.local.OrderDao
import com.example.quotes_app.domain.Order
import com.example.quotes_app.domain.OrderRepository
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeOrderDao: FakeOrderDao
    private lateinit var repository: OrderRepository
    private lateinit var viewModel: OrderViewModel

    @Before
    fun setup() {
        fakeOrderDao = FakeOrderDao()
        repository = OrderRepository(fakeOrderDao)
        viewModel = OrderViewModel(repository)
    }

    @Test
    fun placeOrder_withEmptyAddress_showsError() = runTest {
        val recipe = Recipe("1", "Rendang", "Meat", "Indonesian", "Slow cook", "url")
        viewModel.placeOrder(recipe, "")
        testScheduler.advanceUntilIdle()

        val state = viewModel.orderState.value
        assertTrue(state is OrderState.Error)
        assertEquals("Address cannot be empty", (state as OrderState.Error).message)
    }

    @Test
    fun placeOrder_withValidAddress_placesOrderAndShowsSuccess() = runTest {
        val recipe = Recipe("1", "Rendang", "Meat", "Indonesian", "Slow cook", "url")
        viewModel.placeOrder(recipe, "Jl. Merdeka No. 45")
        testScheduler.advanceUntilIdle()

        val state = viewModel.orderState.value
        assertTrue(state is OrderState.Success)
        val orders = fakeOrderDao.ordersList
        assertEquals(1, orders.size)
        assertEquals("Rendang", orders.first().recipeName)
        assertEquals("Jl. Merdeka No. 45", orders.first().address)
    }

    @Test
    fun resetState_resetsOrderStateToIdle() = runTest {
        val recipe = Recipe("1", "Rendang", "Meat", "Indonesian", "Slow cook", "url")
        viewModel.placeOrder(recipe, "")
        testScheduler.advanceUntilIdle()

        viewModel.resetState()
        assertEquals(OrderState.Idle, viewModel.orderState.value)
    }
}

class FakeOrderDao : OrderDao {
    val ordersList = mutableListOf<Order>()

    override fun getAllOrders(): Flow<List<Order>> = flowOf(ordersList)

    override suspend fun insertOrder(order: Order) {
        ordersList.add(order)
    }
}
