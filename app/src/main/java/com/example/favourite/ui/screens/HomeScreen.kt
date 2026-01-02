package com.example.favourite.ui.screens

import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.favourite.ui.components.CategoryChips
import com.example.favourite.ui.components.HeroSlider
import com.example.favourite.ui.components.MovieCard
import com.example.favourite.viewmodel.MovieViewModel
import com.example.favourite.ui.components.BottomNavigationBar

import com.example.favourite.ui.components.shimmerEffect

@Composable
fun HomeScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit,
    onPersonClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onFavouriteClick: () -> Unit // Added callback
) {
    val categories = listOf(
        "Trending" to "trending",
        "Action" to "28",
        "Romance" to "10749",
        "Animation" to "16",
        "Horror" to "27",
        "Sci-Fi" to "878",
        "Drama" to "18"
    )

    var selectedCategory by remember { mutableStateOf("Trending") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
    ) {
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Red)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 8.dp, end = 8.dp), // Reduced top padding since header is now scrollable
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ... (Content unrelated to change, but kept to match structure if needed, focusing on BottomNav)
                // 1. Top Bar (Scrollable)
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(start = 8.dp, end = 8.dp)
                            .height(60.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sachin.",
                            color = Color(0xFFB980FC), // Purple logo color
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )
                        
                        // Search Button/Box
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // 2. Hero Slider as header
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Column {
                        if (selectedCategory == "Trending") {
                            HeroSlider(
                                movies = viewModel.heroMovies, 
                                category = selectedCategory,
                                onMovieClick = onMovieClick
                            )
                        } else {
                            val sliderMovies = if (selectedCategory == "Trending") viewModel.heroMovies else viewModel.apiData.take(5)
                            HeroSlider(
                                movies = sliderMovies,
                                category = selectedCategory,
                                onMovieClick = onMovieClick
                            )
                        }
                        LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                item {
                    Chip(text = "Trending", isSelected = selectedCategory == "Trending", onClick = {
                        selectedCategory = "Trending"
                        viewModel.fetchData(reset = true)
                    })
                }
                items(categories.filter { it.first != "Trending" }) { category ->
                    Chip(text = category.first, isSelected = selectedCategory == category.first, onClick = {
                        selectedCategory = category.first
                        viewModel.fetchByGenre(category.second, reset = true)
                    })
                }
            }
                        
                        // Trending Movies Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(24.dp)
                                    .background(Color(0xFFB980FC)) // Purple accent from image
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$selectedCategory Movies",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                items(
                    count = viewModel.apiData.size,
                    key = { index -> viewModel.apiData[index].id }
                ) { index ->
                    val movie = viewModel.apiData[index]
                    
                    // Animation State
                    val alpha = remember { androidx.compose.animation.core.Animatable(0f) }
                    val scale = remember { androidx.compose.animation.core.Animatable(0.5f) }

                    LaunchedEffect(key1 = movie.id) {
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

                    // Trigger load more when reaching the end
                    if (index == viewModel.apiData.lastIndex) {
                        LaunchedEffect(Unit) {
                            viewModel.loadMore()
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
                            isFavorite = viewModel.isFavorite(movie.id),
                            onToggleFavorite = { viewModel.toggleFavorite(movie) },
                            onClick = {
                                if (movie.mediaType == "person") {
                                    onPersonClick(movie.id)
                                } else {
                                    onMovieClick(movie.id)
                                }
                            }
                        )
                    }
                }
                
                if (viewModel.isPaginating) {
                    items(6) {
                        ShimmerMovieCard()
                    }
                }
            }
        }

        // --- Bottom Navigation Bar ---
        BottomNavigationBar(
            currentRoute = "home",
            onHomeClick = { /* Already on home */ },
            onFavouriteClick = onFavouriteClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
        )
    }
}

@Composable
fun ShimmerMovieCard() {
    Column {
        Box(
            modifier = Modifier
                .height(240.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.height(20.dp).width(120.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.height(16.dp).width(80.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
    }
}

@Composable
fun Chip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFF303030) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Color(0xFF303030),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
