package com.example.favourite.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Container Style
    val containerShape = RoundedCornerShape(32.dp)
    val containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)
    val borderColor = Color.White.copy(alpha = 0.1f)

    Box(
        modifier = modifier
            .height(64.dp)
            .shadow(16.dp, containerShape, spotColor = Color.Black.copy(alpha = 0.4f))
            .background(containerColor, containerShape)
            .border(1.dp, borderColor, containerShape)
            // Adaptive width based on items, but here fixed or wrapped is fine. 
            // Let's use flexible width for the floating look.
            .padding(horizontal = 8.dp) 
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavbarPill(
                icon = Icons.Filled.Home,
                label = "Home",
                isActive = currentRoute == "home",
                onClick = onHomeClick
            )

            NavbarPill(
                icon = if (currentRoute == "favourite") Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                label = "Saved", // Using "Saved" to be shorter/match typical shorter labels if desired, or keep Favorites
                isActive = currentRoute == "favourite",
                onClick = onFavouriteClick
            )
        }
    }
}

@Composable
fun NavbarPill(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    // Animate width
    // Active: Icon(24) + Padding(16) + Text (~40-60) -> approx 100-120dp
    // Inactive: Icon(24) + Padding(24) -> approx 48-50dp
    // We'll rely on Row measuring with AnimatedVisibility for text, but adding a specific background shape animation looks better.
    
    val backgroundColor = if (isActive) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF8F00FF), // Neon Purple
                Color(0xFF651FFF)  // Deep Blue-Violet
            )
        )
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }
    
    val contentColor = if (isActive) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(CircleShape) // Fully rounded ends
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp), // Comfortable tap area
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            AnimatedVisibility(
                visible = isActive,
                enter = fadeIn() + androidx.compose.animation.expandHorizontally(),
                exit = fadeOut() + androidx.compose.animation.shrinkHorizontally()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
