package com.example.favourite.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.example.favourite.ui.components.CastList
import com.example.favourite.ui.components.MovieCard
import com.example.favourite.viewmodel.MovieViewModel

@Composable
fun AboutScreen(
    type: String,
    id: Int,
    viewModel: MovieViewModel,
    onBack: () -> Unit,
    onPersonClick: (Int) -> Unit,
    onMovieClick: (Int) -> Unit,
    onGenreClick: (String, String) -> Unit
) {
    LaunchedEffect(id) {
        viewModel.fetchMovieDetails(type, id)
    }

    val movie = viewModel.selectedMovie
    var isCastExpanded by remember { mutableStateOf(false) }
    var isOverviewExpanded by remember { mutableStateOf(false) }

    if (viewModel.isLoading || movie == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF101010)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Red)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101010))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp) // Only bottom padding needed now
            ) {
                // 1. Hero Section (Image + Gradient + Main Info) - NOW SCROLLABLE
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                    ) {
                        // 1. Backdrop Image (Background)
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/original${movie.backdropPath ?: movie.posterPath}",
                            contentDescription = "Backdrop",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // 2. Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF101010).copy(alpha = 0.5f), // Slightly darker middle
                                            Color(0xFF101010).copy(alpha = 0.95f), // Darker bottom
                                            Color(0xFF101010)
                                        ),
                                        startY = 100f, // Start gradient earlier for better visibility
                                    )
                                )
                        )

                        // 3. Content: Poster + Info (Row Layout)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Poster Image (Card)
                            val posterAlpha = remember { Animatable(0f) }
                            val posterScale = remember { Animatable(0.5f) }
                            
                            LaunchedEffect(Unit) {
                                launch { posterAlpha.animateTo(1f, tween(300)) }
                                launch {
                                    posterScale.animateTo(
                                        1f,
                                        spring(
                                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                        )
                                    )
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                modifier = Modifier
                                    .width(140.dp) // Fixed width for poster
                                    .aspectRatio(2f / 3f) // Standard poster ratio
                                    .graphicsLayer {
                                        this.alpha = posterAlpha.value
                                        this.scaleX = posterScale.value
                                        this.scaleY = posterScale.value
                                    }
                            ) {
                                AsyncImage(
                                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}", // Use smaller size for poster
                                    contentDescription = "Poster",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Text Content (Title, Metadata, Genres)
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start // Left align text
                            ) {
                                // Title
                                Text(
                                    text = (movie.title ?: movie.name ?: "").uppercase(),
                                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 24.sp), // Slightly smaller to fit
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Start,
                                    lineHeight = 32.sp
                                )

                                // Tagline
                                if (!movie.tagline.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${movie.tagline}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.LightGray,
                                        textAlign = TextAlign.Start
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Metadata Row (Rating | Year | Runtime)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = String.format("%.1f", movie.voteAverage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    
                                    Text(text = " • ", color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                    
                                    Text(text = (movie.releaseDate ?: movie.firstAirDate)?.take(4) ?: "N/A", color = Color.White, fontSize = 14.sp)
                                    
                                    Text(text = " • ", color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                                    
                                    Text(text = formatRuntime(movie.runtime ?: 0), color = Color.White, fontSize = 14.sp)
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))

                                // Genres (Wrapped FlowRow would be better but simple Row with scroll or take(2) is safer for now)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    movie.genres?.take(2)?.forEach { genre ->
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .border(1.dp, Color.Gray, RoundedCornerShape(50))
                                                .clickable { onGenreClick(genre.id.toString(), genre.name) }
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = genre.name, color = Color.LightGray, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Details Section (Overview, Buttons, Cast)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Overview
                        Text(
                            text = movie.overview ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            maxLines = if (isOverviewExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clickable { isOverviewExpanded = !isOverviewExpanded }
                                .animateContentSize()
                        )
                        if (!isOverviewExpanded && (movie.overview?.length ?: 0) > 100) {
                             Text(
                                text = "Read More",
                                color = Color(0xFFBB86FC),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable { isOverviewExpanded = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Buttons Row
                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick = { 
                                    viewModel.trailerKey?.let { key ->
                                        uriHandler.openUri("https://www.youtube.com/watch?v=$key")
                                    }
                                },
                                enabled = viewModel.trailerKey != null, // Disable if no trailer
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    disabledContainerColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Watch Trailer", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Button(
                                onClick = {
                                    val movieForFav = com.example.favourite.data.Movie(
                                        id = movie.id,
                                        title = movie.title,
                                        name = movie.name,
                                        posterPath = movie.posterPath,
                                        backdropPath = movie.backdropPath,
                                        overview = movie.overview,
                                        voteAverage = movie.voteAverage,
                                        releaseDate = movie.releaseDate,
                                        firstAirDate = movie.firstAirDate,
                                        mediaType = type
                                    )
                                    viewModel.toggleFavorite(movieForFav)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = BorderStroke(1.dp, Color.Gray),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Icon(
                                    if (viewModel.isFavorite(movie.id)) Icons.Default.Favorite else Icons.Filled.Add, 
                                    contentDescription = null, 
                                    tint = if (viewModel.isFavorite(movie.id)) Color.Red else Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add to List", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 3. Cast & Production Expandable
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    // "Show Cast & Production Info" Expandable Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(1.dp, Color(0xFF2C2C2C), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF141414)) // Darker background
                            .animateContentSize()
                    ) {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCastExpanded = !isCastExpanded }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Show Cast & Production Info", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Icon(
                                if (isCastExpanded) Icons.Default.Remove else Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        if (isCastExpanded) {
                            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)) {
                                
                                // Info Fields (Matched to Reference)
                                InfoField("Status", movie.status ?: "Unknown")
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                InfoField("Release Date", movie.releaseDate ?: "Unknown")
                                Spacer(modifier = Modifier.height(12.dp))

                                InfoField("Original Language", movie.originalLanguage?.uppercase() ?: "EN")
                                Spacer(modifier = Modifier.height(12.dp))

                                // Keep Director and Country as they are useful info, but formatted same way
                                if (viewModel.director != null) {
                                    Text(text = "Director", color = Color.Gray, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { 
                                            viewModel.directorId?.let { id -> onPersonClick(id) }
                                        }
                                    ) {
                                        if (viewModel.directorProfilePath != null) {
                                            AsyncImage(
                                                model = "https://image.tmdb.org/t/p/w200${viewModel.directorProfilePath}",
                                                contentDescription = viewModel.director,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Gray)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text(
                                            text = viewModel.director!!, 
                                            color = Color.White, 
                                            fontWeight = FontWeight.Bold, 
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Cast List (Horizontal Scrollable Circles)
                                CastList(cast = viewModel.credits, onCastClick = onPersonClick)
                            }
                        }
                    }
                }

                // 4. Recommendation Header
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFD0BCFF)) // Purple tint
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recommended for you",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 5. Recommendations List (Scroll Driven Animation)
                val recommendations = viewModel.recommendations.take(20)
                val chunks = recommendations.chunked(2)

                items(chunks.size) { rowIndex ->
                    val rowItems = chunks[rowIndex]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp), // Added vertical padding for spacing
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEachIndexed { colIndex, item ->
                            // Reduced delay logic for smoother scroll entry
                            // The items are now lazy, so they enter composition as we scroll.
                            // We use a small stagger for the pair in the row.
                            
                            val alpha = remember(item.id) { Animatable(0f) }
                            val scale = remember(item.id) { Animatable(0.5f) }
                            
                            LaunchedEffect(item.id) {
                                val delay = colIndex * 50L // Simple stagger for the two items
                                kotlinx.coroutines.delay(delay)
                                launch {
                                    alpha.animateTo(1f, tween(300))
                                }
                                launch {
                                    scale.animateTo(
                                        1f,
                                        spring(
                                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer {
                                        this.alpha = alpha.value
                                        this.scaleX = scale.value
                                        this.scaleY = scale.value
                                    }
                            ) {
                                val isFav = viewModel.isFavorite(item.id)
                                MovieCard(
                                    movie = item,
                                    isFavorite = isFav,
                                    onToggleFavorite = { viewModel.toggleFavorite(item) },
                                    onClick = { onMovieClick(item.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        // Fill empty space if last row has only 1 item
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } // End LazyColumn           // Top Bar - Fixed transparent overlay
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String, onClick: (() -> Unit)? = null) {
    Column(modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

fun formatRuntime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return "${h}h ${m}min"
}
