package com.example.favourite.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import com.example.favourite.ui.components.MovieCard
import com.example.favourite.viewmodel.MovieViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonScreen(
    personId: Int,
    viewModel: MovieViewModel,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    LaunchedEffect(personId) {
        viewModel.fetchPersonDetails(personId)
    }

    val person = viewModel.personDetails
    var isBioExpanded by remember { mutableStateOf(false) }

    if (viewModel.isLoading || person == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF101010)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Red)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101010))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxSize(),
                // Add top padding for the fixed Back Button
                contentPadding = PaddingValues(top = 80.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Profile Info (Centered)
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile Image
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/h632${person.profilePath}",
                            contentDescription = person.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(180.dp)
                                .height(270.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.Gray)
                        )
                        
                        // Name
                        Text(
                            text = person.name,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Center
                        )
                        
                        // Born
                        if (!person.birthday.isNullOrEmpty()) {
                            InfoChip(text = "Born: ${person.birthday}")
                        }
                        
                        // Place of Birth
                        if (!person.placeOfBirth.isNullOrEmpty()) {
                             InfoChip(text = "Place of Birth: ${person.placeOfBirth}")
                        }
                        
                        // Gender & Known For (Row)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoChip(text = "Gender: ${if (person.gender == 1) "Female" else if (person.gender == 2) "Male" else "N/A"}")
                            InfoChip(text = "Known For: ${person.knownForDepartment ?: "Acting"}")
                        }
                        
                        // Also Known As
                         if (!person.alsoKnownAs.isNullOrEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Also Known As",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = person.alsoKnownAs.take(3).joinToString(", "),
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }

                        // Social Buttons
                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            if (!person.imdbId.isNullOrEmpty()) {
                                Button(
                                    onClick = { uriHandler.openUri("https://www.imdb.com/name/${person.imdbId}") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("IMDb", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            
                            if (!person.homepage.isNullOrEmpty()) {
                                Button(
                                    onClick = { uriHandler.openUri(person.homepage) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A)),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("Website", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Biography Section
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                             Text(
                                 text = "Biography", 
                                 fontSize = 20.sp, 
                                 fontWeight = FontWeight.Bold, 
                                 color = Color(0xFFB980FC),
                                 modifier = Modifier.padding(bottom = 8.dp)
                             )
                             
                             Column(modifier = Modifier.animateContentSize()) {
                                 Text(
                                    text = person.biography ?: "No biography available.",
                                    color = Color(0xFFCCCCCC),
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = if (isBioExpanded) Int.MAX_VALUE else 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if ((person.biography?.length ?: 0) > 200) {
                                    Text(
                                        text = if (isBioExpanded) "Read Less" else "Read More",
                                        color = Color(0xFFB980FC),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .clickable { isBioExpanded = !isBioExpanded }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Known For Header
                         Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(28.dp)
                                    .background(Color(0xFFB980FC))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Known For", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Credits Grid
                itemsIndexed(viewModel.personCredits) { index, movie ->
                    // Trigger load more
                    if (index == viewModel.personCredits.lastIndex) {
                        LaunchedEffect(Unit) {
                            viewModel.loadMorePersonCredits()
                        }
                    }

                    val alpha = remember(movie.id) { Animatable(0f) }
                    val scale = remember(movie.id) { Animatable(0.5f) }

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
            
            // --- Fixed Back Button (Top Left) ---
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color(0xFF2A2A2A), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                 Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

// Bottom Navigation Bar Removed
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val parts = text.split(": ", limit = 2)
        if (parts.size == 2) {
             Row {
                 Text(text = "${parts[0]}: ", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Normal)
                 Text(text = parts[1], color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
             }
        } else {
            Text(text = text, color = Color.White, fontSize = 14.sp)
        }
    }
}
