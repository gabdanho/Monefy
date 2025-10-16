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
import androidx.compose.ui.unit.dp
import com.example.monefy.presentation.model.Category

private const val GRID_ROWS = 2
private const val GRID_ROWS_WHEN_FEW_CATEGORY = 1
private const val FEW_CATEGORIES = 4

@Composable
fun CategoriesGrid(
    name: String,
    categories: List<Category>,
    selectedCategoryId: Int,
    onAddCategoryScreenClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = if (categories.size <= FEW_CATEGORIES) GRID_ROWS_WHEN_FEW_CATEGORY else GRID_ROWS
    val maxHeight = if (categories.size <= FEW_CATEGORIES) 100.dp else 200.dp

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
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