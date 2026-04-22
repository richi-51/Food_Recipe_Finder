package com.example.quotes_app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private var editingId: String? = null

    fun setEditingId(id: String) {
        editingId = id
    }

    fun saveRecipe(name: String, category: String, instructions: String, imagePath: String?) {
        viewModelScope.launch {
            val recipe = OwnRecipe(
                id = editingId ?: UUID.randomUUID().toString(),
                title = name,
                category = category,
                instructions = instructions,
                area = "Local",
                imagePath = imagePath
            )
            repository.insertOwnRecipe(recipe)
        }
    }
}
