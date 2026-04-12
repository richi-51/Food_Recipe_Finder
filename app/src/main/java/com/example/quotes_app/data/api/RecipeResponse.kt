package com.example.quotes_app.data.api

import com.example.quotes_app.domain.Recipe

data class RecipeResponse(
    val meals: List<Recipe>?
)
