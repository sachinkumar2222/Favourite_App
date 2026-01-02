package com.example.favourite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.favourite.data.Movie
import com.example.favourite.ui.components.MovieCard
import com.example.favourite.ui.components.bounceClick
import com.example.favourite.ui.components.shimmerEffect
import com.example.favourite.viewmodel.MovieViewModel
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    viewModel: MovieViewModel,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var movieToDelete by remember { mutableStateOf<Movie?>(null) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Debounce search
    LaunchedEffect(query) {
        if (query.length > 2) {
            delay(500)
            viewModel.search(query)
        }
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .statusBarsPadding()
    ) {
        // --- Search Bar ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .bounceClick(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(25.dp))
                    .padding(horizontal = 16.dp)
            ) {
                 Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxSize()
                 ) {
                     BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        cursorBrush = SolidColor(Color(0xFFB980FC)), // Purple cursor
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            viewModel.search(query)
                            focusManager.clearFocus()
                        }),
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text("Search movies...", color = Color.Gray, fontSize = 16.sp)
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                    )
                    
                    if (query.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { query = "" }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                 }
            }
        }

        // --- Content ---
        if (viewModel.isLoading) {
             LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(6) {
                    Box(
                        modifier = Modifier
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerEffect()
                    )
                }
            }
        } else if (query.isEmpty()) {
            // History
            if (viewModel.searchHistory.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                ) {
                    item {
                         Text(
                             text = "Recent Searches",
                             style = MaterialTheme.typography.titleMedium,
                             color = Color.White,
                             modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                         )
                    }
                    items(viewModel.searchHistory) { movie ->
                        HistoryItem(movie = movie, onClick = {
                            viewModel.addToHistory(movie)
                            onMovieClick(movie.id)
                        }, onLongClick = { movieToDelete = movie })
                    }
                }
            } else {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = null, 
                            tint = Color.DarkGray, 
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Find your favorite movies", color = Color.Gray)
                    }
                }
            }
        } else if (viewModel.searchResults.isEmpty()) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results found", color = Color.Gray)
            }
        } else {
            // Grid Results
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.searchResults) { movie ->
                    MovieCard(
                        movie = movie,
                        isFavorite = viewModel.isFavorite(movie.id),
                        onToggleFavorite = { viewModel.toggleFavorite(movie) },
                        onClick = {
                            viewModel.addToHistory(movie)
                            onMovieClick(movie.id)
                        }
                    )
                }
            }
        }
    }

    if (movieToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { movieToDelete = null },
            title = { Text("Remove from history?") },
            text = { Text("Remove '${movieToDelete?.title ?: movieToDelete?.name}'?") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    movieToDelete?.let { viewModel.removeFromHistory(it) }
                    movieToDelete = null
                }) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { movieToDelete = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.Gray
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItem(movie: Movie, onClick: () -> Unit, onLongClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = "History",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        AsyncImage(
             model = "https://image.tmdb.org/t/p/w200${movie.posterPath}", 
             contentDescription = null,
             contentScale = ContentScale.Crop,
             modifier = Modifier
                 .width(50.dp)
                 .height(75.dp)
                 .clip(RoundedCornerShape(8.dp))
                 .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title ?: movie.name ?: "Unknown",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                maxLines = 1
            )
            Text(
                text = (movie.releaseDate ?: movie.firstAirDate)?.take(4) ?: "Unknown Year",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
