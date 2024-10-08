package com.example.monefy.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.R
import com.example.monefy.data.Category
import com.example.monefy.model.FakeData.fakeCategoriesList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun CategoriesListScreen(
    financesViewModel: FinancesViewModel,
    onAddCategoryClick: () -> Unit,
    onCategoryClick: () -> Unit,
    rewriteCategoryClick: () -> Unit,
) {
    Scaffold { innerPadding ->
        CategoriesList(
            getAllCategories = financesViewModel::getAllCategories,
            changeCurrentCategoryIdForFinances = financesViewModel::changeCurrentCategoryIdForFinances,
            changeCategoryToRewrite = financesViewModel::changeCategoryToRewrite,
            onCategoryClick = onCategoryClick,
            rewriteCategoryClick = rewriteCategoryClick,
            onAddCategoryClick = onAddCategoryClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// Список категорий
@Composable
fun CategoriesList(
    getAllCategories: () -> Flow<List<Category>>,
    changeCurrentCategoryIdForFinances: (Int) -> Unit,
    changeCategoryToRewrite: (Category) -> Unit,
    onCategoryClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    rewriteCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by getAllCategories().collectAsState(emptyList())
    // Карточка создания категории
    val addCategory = Category(id = -1, name = "Добавить категорию (+)", color = Color.Transparent.toArgb())

    LazyVerticalGrid(
        columns = if (categories.isEmpty()) GridCells.Fixed(1) else GridCells.Fixed(2),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(categories + addCategory) { category ->
            CategoryCard(
                category = category,
                changeCurrentCategoryIdForFinances = changeCurrentCategoryIdForFinances,
                changeCategoryToRewrite = changeCategoryToRewrite,
                onCategoryClick = onCategoryClick,
                onAddCategoryClick = onAddCategoryClick,
                rewriteCategoryClick = rewriteCategoryClick,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// Карточка категории
@Composable
fun CategoryCard(
    category: Category,
    changeCurrentCategoryIdForFinances: (Int) -> Unit,
    changeCategoryToRewrite: (Category) -> Unit,
    onCategoryClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    rewriteCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    ambientColor = Color(category.color),
                    spotColor = Color(category.color),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    // Если -1 - то это, создать категорию
                    if (category.id != -1) {
                        changeCurrentCategoryIdForFinances(category.id)
                        onCategoryClick()
                    } else onAddCategoryClick()
                }
        ) {
            // Кружочек с цветом категории
            if (category.id != -1) {
                Canvas(
                    modifier = Modifier
                ) {
                    drawCircle(
                        color = Color(category.color),
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(vertical = 32.dp, horizontal = 4.dp)
                    .fillMaxWidth()
            ) {
                // Название категории
                Text(
                    text = category.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                // Выводим тотал прайс категории
                if (category.id != -1) {
                    Text(
                        text = String.format("%.2f", category.totalCategoryPrice),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        // Если это не карточка создания категории, то значит её можно отредактировать (справа вверху карандашик)
        if (category.id != -1) {
            Row(
                horizontalArrangement = Arrangement.Absolute.Right,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    changeCategoryToRewrite(category)
                    rewriteCategoryClick()
                }
                ) {
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
        getAllCategories = { flowOf(fakeCategoriesList) },
        changeCurrentCategoryIdForFinances = { },
        changeCategoryToRewrite = { },
        onCategoryClick = { /*TODO*/ },
        onAddCategoryClick = { /*TODO*/ },
        rewriteCategoryClick = { /*TODO*/ }
    )
}