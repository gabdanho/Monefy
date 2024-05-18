package com.example.monefy.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.R
import com.example.monefy.model.Category
import com.example.monefy.model.Expense
import com.example.monefy.model.addCategory
import com.example.monefy.model.fake.FakeData

@Composable
fun CategoriesListScreen(
    spendingViewModel: SpendingViewModel,
    onAddCategoryClick: () -> Unit,
    rewriteCategoryClick: (Category) -> Unit,
    onCategoryClick: (List<Expense>, Category) -> Unit
) {
    Scaffold { innerPadding ->
        CategoriesList(
            categories = spendingViewModel.uiState.value.categories,
            onCategoryClick = onCategoryClick,
            onAddCategoryClick = onAddCategoryClick,
            rewriteCategoryClick = rewriteCategoryClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CategoriesList(
    categories: List<Category>,
    onCategoryClick: (List<Expense>, Category) -> Unit,
    onAddCategoryClick: () -> Unit,
    rewriteCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(categories + addCategory) { category ->
            CategoryCard(
                category = category,
                onCategoryClick = onCategoryClick,
                onAddCategoryClick = onAddCategoryClick,
                rewriteCategoryClick = rewriteCategoryClick,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onCategoryClick: (List<Expense>, Category) -> Unit,
    onAddCategoryClick: () -> Unit,
    rewriteCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Box {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    ambientColor = category.color,
                    spotColor = category.color,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    if (category.name != "Добавить категорию (+)") onCategoryClick(category.expenses, category)
                    else onAddCategoryClick()
                }
        ) {
            if (category.name != "Добавить категорию (+)") {
                Canvas(
                    modifier = Modifier
                ) {
                    drawCircle(
                        color = category.color,
                        radius = 20f,
                        center = Offset(50f, 50f)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 20f,
                        center = Offset(50f, 50f),
                        style = Stroke(width = 3f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (category.name != "Добавить категорию (+)") {
                    Text(
                        text = category.totalCategoryPrice.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        if (category.name != "Добавить категорию (+)") {
            Row(
                horizontalArrangement = Arrangement.Absolute.Right,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { rewriteCategoryClick(category) }) {
                    Icon(
                        painter = painterResource(R.drawable.change),
                        contentDescription = "Редактировать",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesListPreview() {
    CategoriesList(
        categories = FakeData.fakeCategories,
        onAddCategoryClick = { },
        onCategoryClick = { _, _ -> },
        rewriteCategoryClick = { }
    )
}

@Preview
@Composable
fun CategoryPreview() {
    CategoryCard(
        category = FakeData.fakeCategories.first(),
        onCategoryClick = { _, _ -> },
        onAddCategoryClick = { },
        rewriteCategoryClick = { }
    )
}