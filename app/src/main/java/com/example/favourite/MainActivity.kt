package com.example.favourite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.favourite.ui.screens.HomeScreen
import com.example.favourite.ui.theme.FavouriteTheme
import com.example.favourite.viewmodel.MovieViewModel
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            FavouriteTheme {
                val navController = rememberNavController()
                val movieViewModel: MovieViewModel = viewModel()
                
                NavHost(
                    navController = navController, 
                    startDestination = "home",
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
                ) {
                    composable(
                        "home",
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { fadeOut() }
                    ) {
                        HomeScreen(
                            viewModel = movieViewModel,
                            onMovieClick = { movieId ->
                                navController.navigate("about/movie/$movieId")
                            },
                            onPersonClick = { personId ->
                                navController.navigate("person/$personId")
                            },
                            onSearchClick = {
                                navController.navigate("search")
                            },
                            onFavouriteClick = {
                                navController.navigate("favourite") 
                            }
                        )
                    }
                    
                    composable(
                        "about/{type}/{id}",
                        arguments = listOf(
                            androidx.navigation.navArgument("type") { type = androidx.navigation.NavType.StringType },
                            androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val type = backStackEntry.arguments?.getString("type") ?: "movie"
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        com.example.favourite.ui.screens.AboutScreen(
                            type = type,
                            id = id,
                            viewModel = movieViewModel,
                            onBack = { navController.popBackStack() },
                            onPersonClick = { personId -> navController.navigate("person/$personId") },
                            onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") },
                            onGenreClick = { genreId, genreName -> navController.navigate("genre/$genreId/$genreName") }
                        )
                    }
                    
                    composable(
                        "person/{id}",
                        arguments = listOf(
                            androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        com.example.favourite.ui.screens.PersonScreen(
                            personId = id,
                            viewModel = movieViewModel,
                            onBack = { navController.popBackStack() },
                            onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") }
                        )
                    }
                    
                    composable("favourite") {
                         com.example.favourite.ui.screens.FavouriteScreen(
                             viewModel = movieViewModel,
                             onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") },
                             onHomeClick = { navController.navigate("home") }
                         )
                    }

                    composable("search") {
                        com.example.favourite.ui.screens.SearchScreen(
                            viewModel = movieViewModel,
                            onBack = { navController.popBackStack() },
                            onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") }
                        )
                    }

                    composable(
                        "genre/{id}/{name}",
                        arguments = listOf(
                            androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.StringType },
                            androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id") ?: "28"
                        val name = backStackEntry.arguments?.getString("name") ?: "Genre"
                        com.example.favourite.ui.screens.GenreScreen(
                            genreId = id,
                            genreName = name,
                            viewModel = movieViewModel,
                            onBack = { navController.popBackStack() },
                            onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") }
                        )
                    }
                }
            }
        }
    }
}