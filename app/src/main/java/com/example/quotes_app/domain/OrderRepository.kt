package com.example.quotes_app.domain

import com.example.quotes_app.data.local.OrderDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }

    suspend fun placeOrder(recipeId: String, recipeName: String, recipeThumb: String, price: Double, address: String) {
        val order = Order(
            recipeId = recipeId,
            recipeName = recipeName,
            recipeThumb = recipeThumb,
            price = price,
            address = address
        )
        orderDao.insertOrder(order)
    }
}
