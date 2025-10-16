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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import com.example.monefy.presentation.constants.ADD_CATEGORY_ID
import com.example.monefy.presentation.theme.defaultDimensions

/**
 * Карточка отдельной категории в сетке.
 *
 * @param categoryName Название категории.
 * @param categoryId Уникальный идентификатор категории.
 * @param categoryColor Цвет категории.
 * @param selectedCategoryId ID текущей выбранной категории.
 * @param changeSelectedCategory Вызывается при выборе категории.
 * @param onAddCategoryScreenClick Вызывается при нажатии на карточку добавления новой категории.
 * @param modifier Модификатор для внешнего оформления карточки.
 */
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
        elevation = CardDefaults.cardElevation(defaultElevation = defaultDimensions.small),
        shape = RoundedCornerShape(defaultDimensions.categoryCardBorderShape),
        modifier = modifier
            .size(defaultDimensions.categoryCardSize)
            .padding(defaultDimensions.verySmall)
            .clickable {
                // Если id = -1, то это карточка создания категории, иначе просто выбираем категорию
                if (categoryId == ADD_CATEGORY_ID) onAddCategoryScreenClick()
                else changeSelectedCategory(categoryId)
            }
            .border(
                width = defaultDimensions.borderWidth,
                shape = RoundedCornerShape(defaultDimensions.categoryCardBorderShape),
                // Белая категория - выбранная категория
                color = if (selectedCategoryId == categoryId) MaterialTheme.colorScheme.onSurface else Color.Transparent,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(defaultDimensions.small)
        ) {
            // Рисуем категорию создания
            if (categoryId != ADD_CATEGORY_ID) {
                CircleCategoryColor(
                    colorLong = categoryColor.toArgb().toLong(),
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