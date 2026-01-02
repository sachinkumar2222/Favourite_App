package com.example.favourite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import com.example.favourite.ui.components.MovieCard
import com.example.favourite.viewmodel.MovieViewModel

import kotlinx.coroutines.launch

import com.example.favourite.ui.components.BottomNavigationBar

@Composable
fun FavouriteScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit,
    onHomeClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101010))
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            // Purple Accent Header
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(28.dp)
                        .background(Color(0xFFB980FC)) // Purple accent
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "My Favourites",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            val favorites = viewModel.favorites.value

            if (favorites.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Favourites Yet",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Mark movies as favorite to see them here",
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp), // Added padding for bottom nav
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        count = favorites.size,
                        key = { index -> favorites[index].id }
                    ) { index ->
                        val movie = favorites[index]

                        // Animation State
                        val alpha = androidx.compose.runtime.remember { androidx.compose.animation.core.Animatable(0f) }
                        val scale = androidx.compose.runtime.remember { androidx.compose.animation.core.Animatable(0.5f) }

                        androidx.compose.runtime.LaunchedEffect(key1 = movie.id) {
                            val delay = (index % 10) * 50L
                            kotlinx.coroutines.delay(delay)

                            launch {
                                alpha.animateTo(
                                    targetValue = 1f,
                                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
                                )
                            }

                            launch {
                                scale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = androidx.compose.animation.core.spring(
                                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    this.alpha = alpha.value
                                    this.scaleX = scale.value
                                    this.scaleY = scale.value
                                }
                        ) {
                            MovieCard(
                                movie = movie,
                                isFavorite = true,
                                onToggleFavorite = { viewModel.toggleFavorite(movie) },
                                onClick = { onMovieClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }

        BottomNavigationBar(
            currentRoute = "favourite",
            onHomeClick = onHomeClick,
            onFavouriteClick = { /* Already on favorites */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
