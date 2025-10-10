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
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.unit.dp
import com.example.monefy.presentation.model.Category

@Composable
fun CategoriesGrid(
    name: String,
    categories: List<Category>,
    selectedCategoryId: Int,
    onAddCategoryScreenClick: () -> Unit,
    changeSelectedCategory: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            rows = GridCells.Fixed(2),
            modifier = Modifier
                .height(maxOf(200.dp))
                .padding(bottom = 8.dp)
        ) {
            items(categories) { category ->
                CategoryCard(
                    categoryName = category.name,
                    categoryId = category.id,
                    categoryColor = category.colorLong?.let { Color.fromColorLong(it) }
                        ?: Color.Transparent,
                    selectedCategoryId = selectedCategoryId,
                    onAddCategoryScreenClick = { onAddCategoryScreenClick() },
                    changeSelectedCategory = { changeSelectedCategory(category.id) }
                )
            }
        }
    }
}