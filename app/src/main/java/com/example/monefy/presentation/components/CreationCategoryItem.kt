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
import androidx.compose.ui.res.stringResource
import com.example.monefy.R
import com.example.monefy.presentation.model.CREATION_CATEGORY
import com.example.monefy.presentation.theme.defaultDimensions

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
                .padding(defaultDimensions.verySmall),
        ) {
            Text(text = stringResource(R.string.text_add_category))
        }
        CategoryCard(
            categoryName = CREATION_CATEGORY.name,
            categoryId = CREATION_CATEGORY.id,
            categoryColor = CREATION_CATEGORY.colorLong?.let { Color(it) }
                ?: Color.Transparent,
            selectedCategoryId = CREATION_CATEGORY.id,
            onAddCategoryScreenClick = { onAddCategoryScreenClick() },
            changeSelectedCategory = { },
            modifier = Modifier.size(defaultDimensions.mediumSize)
        )
    }
}