package com.example.quotes_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.RecipeRepository
import com.example.quotes_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<Resource<List<Recipe>>>(Resource.Loading())
    val recipes: StateFlow<Resource<List<Recipe>>> = _recipes.asStateFlow()

    init {
        searchRecipes("") // default search or let it be empty
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _recipes.value = Resource.Loading()
            try {
                val response = repository.searchRecipes(query)
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    if (meals.isNullOrEmpty()) {
                        _recipes.value = Resource.Error("No recipes found")
                    } else {
                        _recipes.value = Resource.Success(meals)
                    }
                } else {
                    _recipes.value = Resource.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _recipes.value = Resource.Error("Failed to fetch data: ${e.localizedMessage}")
            }
        }
    }
}
