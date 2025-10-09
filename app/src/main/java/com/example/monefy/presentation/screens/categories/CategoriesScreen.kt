package com.example.monefy.presentation.screens.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.R
import com.example.monefy.presentation.components.CircleCategoryColor
import com.example.monefy.presentation.constants.ADD_CATEGORY_ID
import com.example.monefy.presentation.model.Category

private const val ONE_COLUMN = 1
private const val TWO_COLUMN = 2

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoriesScreenViewModel = hiltViewModel<CategoriesScreenViewModel>(),
) {
    Scaffold { innerPadding ->
        // Карточка создания категории
        // val addCategory = Category(id = ADD_CATEGORY_ID, name = "Добавить категорию (+)", colorLong = Color.Transparent.toArgb())
        val uiState by viewModel.uiState.collectAsState()

        LazyVerticalGrid(
            columns = if (uiState.categories.isEmpty()) GridCells.Fixed(ONE_COLUMN)
                else GridCells.Fixed(TWO_COLUMN),
            modifier = modifier
                .padding(innerPadding)
                .padding(bottom = 8.dp)
        ) {
            items(uiState.categories) { category ->
                CategoryCard(
                    category = category,
                    onCategoryClick = { viewModel.onCategoryClick(category.id) },
                    onCreateCategoryClick = { viewModel.onCreateCategoryClick() },
                    onRedactorClick = { viewModel.onRedactorClick(category.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

// Карточка категории
@Composable
private fun CategoryCard(
    category: Category,
    onCategoryClick: () -> Unit,
    onCreateCategoryClick: () -> Unit,
    onRedactorClick: () -> Unit,
    modifier: Modifier = Modifier,
) { // TODO: ВОЗМОЖНО НУЖНО ДОБАВИТЬ BOX, ЕСЛИ БУДУТ ПРОБЛЕМЫ С UI
    Card(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                ambientColor = category.colorLong?.let { Color.fromColorLong(it) } ?: Color.Transparent,
                spotColor = category.colorLong?.let { Color.fromColorLong(it) } ?: Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                // Если -1 - то это категория, иначе создаём категорию
                if (category.id != ADD_CATEGORY_ID) {
                    onCategoryClick()
                } else onCreateCategoryClick()
            }
    ) {
        CategoryInfo(
            category = category,
            onRedactorClick = { onRedactorClick() },
            modifier = Modifier
                .padding(vertical = 32.dp, horizontal = 4.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun CategoryInfo(
    category: Category,
    onRedactorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Кружочек с цветом категории
    if (category.id != ADD_CATEGORY_ID) {
        CircleCategoryColor(
            colorLong = category.colorLong ?: Color.Transparent.toColorLong(),
            radius = 20f,
            center = 50f
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        // Название категории
        Text(
            text = category.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        // Выводим тотал прайс категории
        if (category.id != ADD_CATEGORY_ID) {
            Text(
                text = String.format("%.2f", category.totalCategoryPrice),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    // Если это не карточка создания категории, то значит её можно отредактировать (справа вверху карандашик)
    if (category.id != ADD_CATEGORY_ID) {
        Row(
            horizontalArrangement = Arrangement.Absolute.Right,
            modifier = Modifier.fillMaxWidth()
        ) {
            RedactorIcon(
                onRedactorClick = { onRedactorClick() },
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
private fun RedactorIcon(
    onRedactorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = {
            onRedactorClick()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.change),
            contentDescription = "Редактировать",
            modifier = modifier
        )
    }
}