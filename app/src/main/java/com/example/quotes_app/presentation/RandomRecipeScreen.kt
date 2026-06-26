package com.example.quotes_app.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomRecipeScreen(
    viewModel: RandomRecipeViewModel,
    onNavigateToDetail: (Recipe) -> Unit
) {
    val randomRecipeResource by viewModel.randomRecipe.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Surprise Me!") },
                actions = {
                    IconButton(onClick = { viewModel.getRandomRecipe() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Get another random recipe")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val resource = randomRecipeResource) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                is Resource.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = resource.message ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.getRandomRecipe() }) {
                            Text("Try Again")
                        }
                    }
                }
                is Resource.Success -> {
                    val recipe = resource.data
                    if (recipe != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            RecipeItem(recipe = recipe, onClick = { onNavigateToDetail(recipe) })
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.getRandomRecipe() }) {
                                Text("Not this one? Roll again!")
                            }
                        }
                    }
                }
            }
        }
    }
}
