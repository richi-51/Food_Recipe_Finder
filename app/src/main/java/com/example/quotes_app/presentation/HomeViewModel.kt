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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<Resource<List<Recipe>>>(Resource.Loading())
    val recipes: StateFlow<Resource<List<Recipe>>> = _recipes.asStateFlow()

    private val _hasNextPage = MutableStateFlow(false)
    val hasNextPage: StateFlow<Boolean> = _hasNextPage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private var allRecipes: List<Recipe> = emptyList()
    private var currentPage = 1
    private val pageSize = 10

    init {
        observeSearch()
    }

    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                fetchRecipes(query)
            }
        }
    }

    fun searchRecipes(query: String) {
        _searchQuery.value = query
    }

    private suspend fun fetchRecipes(query: String) {
        _recipes.value = Resource.Loading()
        _hasNextPage.value = false
        try {
            val response = repository.searchRecipes(query)
            if (response.isSuccessful) {
                val remoteRecipes = response.body()?.meals ?: emptyList()
                if (remoteRecipes.isEmpty()) {
                    allRecipes = emptyList()
                    _recipes.value = Resource.Error("No recipes found")
                } else {
                    allRecipes = remoteRecipes
                    currentPage = 1
                    updatePaginatedList()
                }
            } else {
                _recipes.value = Resource.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            _recipes.value = Resource.Error("Failed to fetch data: ${e.localizedMessage}")
        }
    }

    fun loadMore() {
        if (_hasNextPage.value) {
            currentPage++
            updatePaginatedList()
        }
    }

    private fun updatePaginatedList() {
        val endIndex = (currentPage * pageSize).coerceAtMost(allRecipes.size)
        val paginatedList = allRecipes.subList(0, endIndex)
        _recipes.value = Resource.Success(paginatedList.toList())
        _hasNextPage.value = endIndex < allRecipes.size
    }
}
