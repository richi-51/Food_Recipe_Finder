package com.example.quotes_app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quotes_app.domain.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipe: Recipe,
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit,
    onOrderClick: (Recipe) -> Unit
) {
    val isFavorite by viewModel.isFavorite(recipe.idMeal).collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Recipe Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleFavorite(recipe, isFavorite) },
                containerColor = if (isFavorite) Color(0xFFFFD700) else Color.LightGray,
                modifier = Modifier.testTag("favoriteFab")
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.White else Color.DarkGray
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = recipe.strMealThumb,
                contentDescription = recipe.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.strMeal,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val categoryText = "${recipe.strCategory ?: ""} - ${recipe.strArea ?: ""}"
                if (categoryText.isNotBlank() && categoryText != " - ") {
                    Text(
                        text = categoryText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.strInstructions ?: "No instructions available.",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                androidx.compose.material3.Button(
                    onClick = { onOrderClick(recipe) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("orderIngredientsButton")
                ) {
                    Text("Order Ingredients", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(80.dp)) // space for FAB
            }
        }
    }
}
