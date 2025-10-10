package com.example.monefy.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.monefy.presentation.constants.ADD_CATEGORY_ID

// Карточка категории
@Composable
fun CategoryCard(
    categoryName: String,
    categoryId: Int,
    categoryColor: Color,
    selectedCategoryId: Int,
    changeSelectedCategory: (Int) -> Unit,
    onAddCategoryScreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .size(150.dp)
            .padding(4.dp)
            .clickable {
                // Если id = -1, то это карточка создания категории, иначе просто выбираем категорию
                if (categoryId == ADD_CATEGORY_ID) onAddCategoryScreenClick()
                else changeSelectedCategory(categoryId)
            }
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                // Белая категория - выбранная категория
                color = if (selectedCategoryId == categoryId) MaterialTheme.colorScheme.onSurface else Color.Transparent,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Рисуем категорию создания
            if (categoryId != ADD_CATEGORY_ID) {
                CircleCategoryColor(
                    colorLong = categoryColor.toColorLong(),
                    radius = 10f,
                    center = 5f,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = categoryName,
                textAlign = TextAlign.Center,
                style = if (categoryId == ADD_CATEGORY_ID) MaterialTheme.typography.displaySmall
                else MaterialTheme.typography.titleMedium
            )
        }
    }
}