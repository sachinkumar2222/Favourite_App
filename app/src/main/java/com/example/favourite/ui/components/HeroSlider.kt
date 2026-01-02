package com.example.favourite.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import coil.compose.SubcomposeAsyncImage
import com.example.favourite.data.Movie
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroSlider(movies: List<Movie>, category: String, onMovieClick: (Int) -> Unit) {
    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE }, initialPage = Int.MAX_VALUE / 2)

    // Auto-scroll
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            val nextPage = pagerState.currentPage + 1
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pager
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 60.dp),
            pageSpacing = (-30).dp,
            modifier = Modifier
                .height(420.dp)
                .fillMaxWidth()
        ) { page ->
            val index = page % movies.size
            val movie = movies[index]

            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val absOffset = pageOffset.absoluteValue

            // Transformations
            val scale = lerp(1f, 0.8f, absOffset.coerceIn(0f, 1f))
            val alpha = lerp(1f, 0.5f, absOffset.coerceIn(0f, 1f))
            val rotationY = lerp(0f, 10f, pageOffset.coerceIn(-1f, 1f))
            
            val density = LocalDensity.current.density

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(if (absOffset < 0.5) 1f else 0f)
                    .graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = alpha
                        this.rotationY = rotationY * if (pageOffset > 0) 1 else -1
                        cameraDistance = 12 * density
                    }
                    .clickable { onMovieClick(movie.id) }
            ) {
                // Card Container
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                ) {
                    SubcomposeAsyncImage(
                        model = "https://image.tmdb.org/t/p/original${movie.posterPath}",
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(modifier = Modifier.fillMaxSize().shimmerEffect())
                        }
                    )
                    
                    Box(
                         modifier = Modifier
                             .fillMaxSize()
                             .background(
                                 Brush.verticalGradient(
                                     colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                     startY = 0f,
                                     endY = Float.POSITIVE_INFINITY
                                 )
                             )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Movie Details
        val currentMovie = movies[pagerState.currentPage % movies.size]

        // 1. Title
        Text(
            text = currentMovie.title ?: currentMovie.name ?: "",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Category (Simple Text)
        Text(
            text = category,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Pagination
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dotCount = movies.take(5).size 
            val currentIndex = pagerState.currentPage % movies.size
            
            repeat(dotCount) { index ->
                val isSelected = index == (currentIndex % dotCount)
                val size = if (isSelected) 8.dp else 6.dp
                val color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f)
                
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(size)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

