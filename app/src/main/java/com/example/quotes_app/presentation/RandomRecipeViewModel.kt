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
class RandomRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _randomRecipe = MutableStateFlow<Resource<Recipe>>(Resource.Loading())
    val randomRecipe: StateFlow<Resource<Recipe>> = _randomRecipe.asStateFlow()

    init {
        getRandomRecipe()
    }

    fun getRandomRecipe() {
        viewModelScope.launch {
            _randomRecipe.value = Resource.Loading()
            try {
                val response = repository.getRandomRecipe()
                if (response.isSuccessful) {
                    val recipe = response.body()?.meals?.firstOrNull()
                    if (recipe != null) {
                        _randomRecipe.value = Resource.Success(recipe)
                    } else {
                        _randomRecipe.value = Resource.Error("No recipe found")
                    }
                } else {
                    _randomRecipe.value = Resource.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _randomRecipe.value = Resource.Error("Failed to fetch data: ${e.localizedMessage}")
            }
        }
    }
}
