package com.example.quotes_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    fun isFavorite(id: String): StateFlow<Boolean> = repository.isFavorite(id)
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun toggleFavorite(recipe: Recipe, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                repository.deleteFavorite(recipe)
            } else {
                repository.insertFavorite(recipe)
            }
        }
    }
}
