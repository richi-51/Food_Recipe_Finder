package com.example.quotes_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.domain.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _limit = MutableStateFlow(10)

    val favorites: StateFlow<List<Recipe>> = _limit
        .flatMapLatest { limit -> repository.getFavoritesPaginated(limit) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadMore() {
        _limit.value += 10
    }
}
