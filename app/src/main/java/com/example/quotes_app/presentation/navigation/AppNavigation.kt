package com.example.quotes_app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quotes_app.domain.Recipe
import com.example.quotes_app.presentation.AddRecipeScreen
import com.example.quotes_app.presentation.AddRecipeViewModel
import com.example.quotes_app.presentation.DetailScreen
import com.example.quotes_app.presentation.DetailViewModel
import com.example.quotes_app.presentation.FavoriteScreen
import com.example.quotes_app.presentation.FavoriteViewModel
import com.example.quotes_app.presentation.HomeScreen
import com.example.quotes_app.presentation.HomeViewModel
import com.example.quotes_app.presentation.OwnRecipeScreen
import com.example.quotes_app.presentation.OwnRecipeViewModel
import com.example.quotes_app.presentation.RandomRecipeScreen
import com.example.quotes_app.presentation.RandomRecipeViewModel
import com.example.quotes_app.presentation.auth.AuthViewModel
import com.example.quotes_app.presentation.auth.LoginScreen
import com.example.quotes_app.presentation.auth.RegisterScreen
import com.example.quotes_app.presentation.order.CheckoutScreen
import com.example.quotes_app.presentation.order.OrderHistoryScreen
import com.example.quotes_app.presentation.order.OrderViewModel
import com.example.quotes_app.presentation.profile.EditProfileScreen
import com.example.quotes_app.presentation.profile.EditProfileViewModel
import com.example.quotes_app.presentation.profile.ProfileScreen
import com.example.quotes_app.presentation.profile.ProfileViewModel
import com.example.quotes_app.presentation.profile.SettingsScreen
import com.example.quotes_app.presentation.profile.SettingsViewModel
import com.example.quotes_app.presentation.splash.SplashScreen
import com.example.quotes_app.utils.ThemeManager

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Favorite : Screen("favorite")
    object OwnRecipe : Screen("own_recipe")
    object Orders : Screen("orders")
    object Profile : Screen("profile")
    object Detail : Screen("detail")
    object Checkout : Screen("checkout")
    object AddRecipe : Screen("add_recipe")
    object RandomRecipe : Screen("random_recipe")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(themeManager: ThemeManager) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        Screen.Home to Pair("Home", Icons.Default.Home),
        Screen.Favorite to Pair("Favorite", Icons.Default.Favorite),
        Screen.OwnRecipe to Pair("My Recipe", Icons.Default.Edit),
        Screen.Orders to Pair("Orders", Icons.Default.DateRange),
        Screen.Profile to Pair("Profile", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.first.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    bottomNavItems.forEach { (screen, info) ->
                        val (title, icon) = info
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = title) },
                            label = { 
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    softWrap = false
                                ) 
                            },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF9800),
                                selectedTextColor = Color(0xFFFF9800),
                                indicatorColor = Color(0xFFFFE0B2)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        // For now we'll route to login, but could check auth session here
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                val viewModel = hiltViewModel<AuthViewModel>()
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                val viewModel = hiltViewModel<AuthViewModel>()
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {
                        navController.navigateUp()
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable(Screen.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    themeManager = themeManager,
                    onNavigateToDetail = { recipe ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("recipe", recipe)
                        navController.navigate(Screen.Detail.route)
                    },
                    onNavigateToSurprise = {
                        navController.navigate(Screen.RandomRecipe.route)
                    }
                )
            }

            composable(Screen.Favorite.route) {
                val viewModel = hiltViewModel<FavoriteViewModel>()
                FavoriteScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { recipe ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("recipe", recipe)
                        navController.navigate(Screen.Detail.route)
                    }
                )
            }

            composable(Screen.OwnRecipe.route) {
                val viewModel = hiltViewModel<OwnRecipeViewModel>()
                OwnRecipeScreen(
                    viewModel = viewModel,
                    onNavigateToAddRecipe = {
                        navController.navigate(Screen.AddRecipe.route)
                    },
                    onNavigateToDetail = { recipe ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("recipe", recipe)
                        navController.navigate(Screen.Detail.route)
                    }
                )
            }

            composable(Screen.Orders.route) {
                val viewModel = hiltViewModel<OrderViewModel>()
                OrderHistoryScreen(viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                val viewModel = hiltViewModel<ProfileViewModel>()
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Detail.route) {
                val recipe = navController.previousBackStackEntry?.savedStateHandle?.get<Recipe>("recipe")
                val viewModel = hiltViewModel<DetailViewModel>()
                
                if (recipe != null) {
                    DetailScreen(
                        recipe = recipe,
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() },
                        onOrderClick = { selectedRecipe ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("recipe", selectedRecipe)
                            navController.navigate(Screen.Checkout.route)
                        }
                    )
                }
            }

            composable(Screen.Checkout.route) {
                val recipe = navController.previousBackStackEntry?.savedStateHandle?.get<Recipe>("recipe")
                val viewModel = hiltViewModel<OrderViewModel>()

                if (recipe != null) {
                    CheckoutScreen(
                        recipe = recipe,
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() },
                        onCheckoutSuccess = {
                            navController.navigate(Screen.Orders.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }
            }

            composable(Screen.AddRecipe.route) {
                val viewModel = hiltViewModel<AddRecipeViewModel>()
                AddRecipeScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.RandomRecipe.route) {
                val viewModel = hiltViewModel<RandomRecipeViewModel>()
                RandomRecipeScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { recipe ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("recipe", recipe)
                        navController.navigate(Screen.Detail.route)
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                val viewModel = hiltViewModel<EditProfileViewModel>()
                EditProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.Settings.route) {
                val viewModel = hiltViewModel<SettingsViewModel>()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
