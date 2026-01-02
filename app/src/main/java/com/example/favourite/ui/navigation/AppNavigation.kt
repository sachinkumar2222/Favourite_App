package com.example.favourite.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.favourite.ui.screens.HomeScreen
import com.example.favourite.ui.screens.AboutScreen
import com.example.favourite.ui.screens.PersonScreen
import com.example.favourite.ui.screens.FavouriteScreen
import com.example.favourite.ui.screens.SearchScreen
import com.example.favourite.ui.screens.GenreScreen
import com.example.favourite.viewmodel.MovieViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    movieViewModel: MovieViewModel
) {
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
                navArgument("type") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "movie"
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            AboutScreen(
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
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            PersonScreen(
                personId = id,
                viewModel = movieViewModel,
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") }
            )
        }

        composable("favourite") {
            FavouriteScreen(
                viewModel = movieViewModel,
                onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") },
                onHomeClick = { navController.navigate("home") }
            )
        }

        composable("search") {
            SearchScreen(
                viewModel = movieViewModel,
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") }
            )
        }

        composable(
            "genre/{id}/{name}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "28"
            val name = backStackEntry.arguments?.getString("name") ?: "Genre"
            GenreScreen(
                genreId = id,
                genreName = name,
                viewModel = movieViewModel,
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId -> navController.navigate("about/movie/$movieId") }
            )
        }
    }
}
