package com.example.quotes_app.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {
    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): Response<RecipeResponse>
    
    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") id: String): Response<RecipeResponse>
}
