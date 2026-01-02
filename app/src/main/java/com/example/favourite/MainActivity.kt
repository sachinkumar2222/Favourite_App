package com.example.favourite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.favourite.ui.navigation.AppNavigation
import com.example.favourite.ui.theme.FavouriteTheme
import com.example.favourite.viewmodel.MovieViewModel

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
                
                AppNavigation(navController = navController, movieViewModel = movieViewModel)
            }
        }
    }
}