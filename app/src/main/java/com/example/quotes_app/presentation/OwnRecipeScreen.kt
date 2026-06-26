package com.example.quotes_app.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.quotes_app.domain.OwnRecipe
import com.example.quotes_app.domain.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnRecipeScreen(
    viewModel: OwnRecipeViewModel,
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToDetail: (Recipe) -> Unit
) {
    val ownRecipes by viewModel.ownRecipes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Recipes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddRecipe) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.searchRecipes(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search your recipes...") },
                shape = RoundedCornerShape(24.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(ownRecipes) { ownRecipe ->
                    OwnRecipeItem(
                        ownRecipe = ownRecipe,
                        onClick = {
                            val recipe = Recipe(
                                idMeal = ownRecipe.id,
                                strMeal = ownRecipe.title,
                                strCategory = ownRecipe.category,
                                strArea = ownRecipe.area,
                                strInstructions = ownRecipe.instructions,
                                strMealThumb = ownRecipe.imagePath
                            )
                            onNavigateToDetail(recipe)
                        },
                        onDeleteClick = {
                            viewModel.deleteRecipe(ownRecipe)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OwnRecipeItem(ownRecipe: OwnRecipe, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ownRecipe.imagePath,
                contentDescription = ownRecipe.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ownRecipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ownRecipe.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
