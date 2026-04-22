package com.example.quotes_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OwnRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _limit = MutableStateFlow(10)

    val ownRecipes: StateFlow<List<OwnRecipe>> = _limit
        .flatMapLatest { limit -> repository.getOwnRecipesPaginated(limit) }
        .combine(_searchQuery) { recipes, query ->
            if (query.isBlank()) {
                recipes
            } else {
                recipes.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.category.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun searchRecipes(query: String) {
        _searchQuery.value = query
    }

    fun deleteRecipe(recipe: OwnRecipe) {
        viewModelScope.launch {
            repository.deleteOwnRecipe(recipe)
        }
    }

    fun loadMore() {
        _limit.value += 10
    }
}
