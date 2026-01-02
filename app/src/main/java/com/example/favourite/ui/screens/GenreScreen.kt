package com.example.favourite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.itemsIndexed
import kotlinx.coroutines.launch
import com.example.favourite.ui.components.MovieCard
import com.example.favourite.viewmodel.MovieViewModel

@Composable
fun GenreScreen(
    genreId: String,
    genreName: String,
    viewModel: MovieViewModel,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    LaunchedEffect(genreId) {
        viewModel.fetchGenreMovies(genreId)
    }

    val movies = viewModel.genreMovies

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2A2A2A), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Text(
                text = "$genreName Movies",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        if (viewModel.isLoading && movies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Red)
            }
        } else {
             LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 itemsIndexed(movies) { index, movie ->
                    val alpha = androidx.compose.runtime.remember(movie.id) { Animatable(0f) }
                    val scale = androidx.compose.runtime.remember(movie.id) { Animatable(0.5f) }

                    LaunchedEffect(key1 = movie.id) {
                        val delay = (index % 12) * 50L 
                        kotlinx.coroutines.delay(delay)
                        
                        launch {
                            alpha.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 300)
                            )
                        }
                        
                        launch {
                            scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.graphicsLayer {
                            this.alpha = alpha.value
                            this.scaleX = scale.value
                            this.scaleY = scale.value
                        }
                    ) {
                        MovieCard(
                            movie = movie,
                            isFavorite = viewModel.isFavorite(movie.id),
                            onToggleFavorite = { viewModel.toggleFavorite(movie) },
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                 }
            }
        }
    }
}
