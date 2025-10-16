package com.example.monefy.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.theme.defaultDimensions

private const val GRID_ROWS = 2
private const val GRID_ROWS_WHEN_FEW_CATEGORY = 1
private const val FEW_CATEGORIES = 4

/**
 * Сетка категорий с возможностью выбора и добавления новой категории.
 *
 * @param name Заголовок блока.
 * @param selectedCategoryId ID выбранной категории.
 * @param categories Список всех категорий.
 * @param onAddCategoryScreenClick Обработчик клика по карточке "Добавить категорию".
 * @param changeSelectedCategory Обработчик выбора категории.
 */
@Composable
fun CategoriesGrid(
    name: String,
    selectedCategoryId: Int,
    categories: List<Category>,
    onAddCategoryScreenClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = if (categories.size <= FEW_CATEGORIES) GRID_ROWS_WHEN_FEW_CATEGORY else GRID_ROWS
    val maxHeight =
        if (categories.size <= FEW_CATEGORIES) defaultDimensions.heigthFewCategory else defaultDimensions.heightCategory

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(defaultDimensions.verySmall),
        ) {
            Text(text = name)
        }

        LazyHorizontalGrid(
            rows = GridCells.Fixed(rows),
            modifier = Modifier.height(maxOf(maxHeight))
        ) {
            items(categories) { category ->
                CategoryCard(
                    categoryName = category.name,
                    categoryId = category.id,
                    categoryColor = category.colorLong?.let { Color(it) }
                        ?: Color.Transparent,
                    selectedCategoryId = selectedCategoryId,
                    onAddCategoryScreenClick = { onAddCategoryScreenClick() },
                    changeSelectedCategory = { changeSelectedCategory(category.id) }
                )
            }
        }
    }
}