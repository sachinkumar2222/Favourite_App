package com.example.favourite.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryChips(
    categories: List<Pair<String, Any>>,
    selectedCategory: String,
    onCategorySelected: (String, Any) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
            .padding(start = 12.dp)
    ) {
        categories.forEach { (name, id) ->
            val isSelected = selectedCategory == name
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        color = if (isSelected) Color(0xFF303030) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else Color(0xFF303030),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onCategorySelected(name, id) }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
