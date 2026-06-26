package com.example.quotes_app.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.domain.Order
import com.example.quotes_app.domain.OrderRepository
import com.example.quotes_app.domain.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    val orders: kotlinx.coroutines.flow.Flow<List<Order>> = repository.getAllOrders()

    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState: StateFlow<OrderState> = _orderState.asStateFlow()

    fun placeOrder(recipe: Recipe, address: String) {
        if (address.isBlank()) {
            _orderState.value = OrderState.Error("Address cannot be empty")
            return
        }

        _orderState.value = OrderState.Loading
        viewModelScope.launch {
            try {
                // Mock price between $10 and $30 based on name length
                val mockPrice = 10.0 + (recipe.strMeal.length % 20)
                
                // Simulate network delay
                kotlinx.coroutines.delay(1000)
                
                repository.placeOrder(
                    recipeId = recipe.idMeal,
                    recipeName = recipe.strMeal,
                    recipeThumb = recipe.strMealThumb ?: "",
                    price = mockPrice,
                    address = address
                )
                
                _orderState.value = OrderState.Success
            } catch (e: Exception) {
                _orderState.value = OrderState.Error(e.localizedMessage ?: "Failed to place order")
            }
        }
    }

    fun resetState() {
        _orderState.value = OrderState.Idle
    }
}

sealed class OrderState {
    object Idle : OrderState()
    object Loading : OrderState()
    object Success : OrderState()
    data class Error(val message: String) : OrderState()
}
