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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.monefy.R
import com.example.monefy.presentation.components.CircleCategoryColor
import com.example.monefy.presentation.constants.ADD_CATEGORY_ID
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.theme.defaultDimensions
import java.util.Locale

private const val TWO_COLUMN = 2

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoriesScreenViewModel = hiltViewModel<CategoriesScreenViewModel>(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            LifecycleEventObserver{ _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.getCategories()
                }
            }
        )
    }

    Scaffold { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        if (uiState.categories.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(TWO_COLUMN),
                modifier = modifier
                    .padding(innerPadding)
                    .padding(bottom = defaultDimensions.small)
            ) {
                items(uiState.categories) { category ->
                    CategoryCard(
                        category = category,
                        onCategoryClick = { viewModel.onCategoryClick(category.id) },
                        onCreateCategoryClick = { viewModel.onCreateCategoryClick() },
                        onRedactorClick = { viewModel.onRedactorClick(category) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(defaultDimensions.medium)
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.text_no_data),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(defaultDimensions.small)
            )
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
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = defaultDimensions.categoryCardElevation,
                ambientColor = category.colorLong?.let { Color(it) } ?: Color.Transparent,
                spotColor = category.colorLong?.let { Color(it) } ?: Color.Transparent,
                shape = RoundedCornerShape(defaultDimensions.categoryCardBorderShape)
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
                .padding(vertical = defaultDimensions.big, horizontal = defaultDimensions.verySmall)
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
            colorLong = category.colorLong ?: Color.Transparent.toArgb().toLong(),
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
                text = String.format(Locale.getDefault(), "%.2f", category.totalCategoryPrice),
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
                modifier = Modifier.size(defaultDimensions.iconSize)
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
            contentDescription = stringResource(R.string.content_edit),
            modifier = modifier
        )
    }
}