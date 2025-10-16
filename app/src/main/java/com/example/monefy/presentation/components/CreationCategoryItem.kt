package com.example.monefy.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.monefy.presentation.model.CREATION_CATEGORY

@Composable
fun CreationCategoryItem(
    onAddCategoryScreenClick: () -> Unit,
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
            Text(text = "Добавить категорию")
        }
        CategoryCard(
            categoryName = CREATION_CATEGORY.name,
            categoryId = CREATION_CATEGORY.id,
            categoryColor = CREATION_CATEGORY.colorLong?.let { Color(it) }
                ?: Color.Transparent,
            selectedCategoryId = CREATION_CATEGORY.id,
            onAddCategoryScreenClick = { onAddCategoryScreenClick() },
            changeSelectedCategory = { },
            modifier = Modifier.size(70.dp)
        )
    }
}