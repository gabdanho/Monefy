package com.example.monefy.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.model.Category
import com.example.monefy.model.fake.FakeData
import com.example.monefy.utils.isAllCategoriesEmpty
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun MainScreen(
    onAddButtonClick: () -> Unit,
    spendingViewModel: SpendingViewModel,
    modifier: Modifier = Modifier
) {
    val spendingUiState by spendingViewModel.uiState.collectAsState()
    Main(
        spendingViewModel = spendingViewModel,
        categories = spendingUiState.categories,
        totalPriceFromAllCategories = spendingUiState.totalPriceFromCategories,
        onAddButtonClick = onAddButtonClick,
        modifier = modifier
    )
}

@Composable
fun Main(
    spendingViewModel: SpendingViewModel,
    categories: List<Category>,
    totalPriceFromAllCategories: Double,
    onAddButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = { BottomMenuBar(onAddButtonClick) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            SpendingPieChart(
                spendingViewModel = spendingViewModel,
                modifier = modifier.weight(1.2f)
            )
            SpendingTable(
                categories = categories,
                totalPriceFromAllCategories = totalPriceFromAllCategories,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SpendingPieChart(
    spendingViewModel: SpendingViewModel,
    radius: Float = 300f,
    modifier: Modifier = Modifier
) {
    if (!isAllCategoriesEmpty(spendingViewModel.uiState.value.categories)) {
        val anglePerValue = (360 / spendingViewModel.uiState.value.totalPriceFromCategories)
        val sweepAnglePercentage = spendingViewModel.uiState.value.categories.map {
            (it.totalCategoryPrice * anglePerValue).toFloat()
        }
        var circleCenter by remember { mutableStateOf(Offset.Zero) }
        var currentCategoryName by remember { mutableStateOf("Все расходы") }
        var currentCategorySumPrice by remember { mutableStateOf(spendingViewModel.uiState.value.totalPriceFromCategories) }

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentCategoryName,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Box(
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(true) {
                            detectTapGestures(
                                onTap = { offset ->
                                    // проверяем был ли тач в области диаграммы
                                    if (pow(
                                            (offset.x - circleCenter.x).toDouble(),
                                            2.0
                                        ) + pow(
                                            (offset.y - circleCenter.y).toDouble(),
                                            2.0
                                        ) <= pow(radius.toDouble() + radius / 3f, 2.0) &&
                                        pow(
                                            (offset.x - circleCenter.x).toDouble(),
                                            2.0
                                        ) + pow(
                                            (offset.y - circleCenter.y).toDouble(),
                                            2.0
                                        ) >= pow(radius.toDouble() - radius / 3f, 2.0)
                                    ) {
                                        // тап -> переводим в углы
                                        val tapAngleInDegrees = (-atan2(
                                            x = circleCenter.y - offset.y,
                                            y = circleCenter.x - offset.x
                                        ) * (180f / PI).toFloat() - 90f).mod(360f)

                                        // по углу смотрим в какую категорию попадаем
                                        var currentAngle = 0f
                                        spendingViewModel.uiState.value.categories.forEach { category ->
                                            currentAngle += category.totalCategoryPrice.toFloat() * anglePerValue.toFloat()
                                            if (tapAngleInDegrees < currentAngle) {
                                                spendingViewModel.updateIsTappedFromPieChart(category.name)
                                                if (!category.isTapped) {
                                                    currentCategoryName = category.name
                                                    currentCategorySumPrice = category.totalCategoryPrice
                                                }
                                                else {
                                                    currentCategoryName = "Все расходы"
                                                    currentCategorySumPrice = spendingViewModel.uiState.value.totalPriceFromCategories
                                                }
                                                return@detectTapGestures
                                            }
                                        }
                                    }
                                }
                            )
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    var startAngle = 0f
                    circleCenter = Offset(x = width / 2f, y = height / 2f)

                    // Тень
                    drawCircle(
                        color = Color.LightGray,
                        center = Offset(circleCenter.x, circleCenter.y),
                        radius = radius,
                        alpha = 0.8f,
                        style = Stroke(
                            width = radius / 3f
                        )
                    )

                    for (i in spendingViewModel.uiState.value.categories.indices) {
                        if (spendingViewModel.uiState.value.categories[i].expenses.isNotEmpty()) {
                            val scale = if (spendingViewModel.uiState.value.categories[i].isTapped) 1.1f else 1.0f
                            scale(scale) {
                                drawArc(
                                    color = spendingViewModel.uiState.value.categories[i].color,
                                    startAngle = startAngle,
                                    sweepAngle = sweepAnglePercentage[i],
                                    useCenter = false,
                                    style = Stroke(
                                        width = radius / 3f
                                    ),
                                    size = Size(
                                        radius * 2f,
                                        radius * 2f
                                    ),
                                    topLeft = Offset(
                                        (width - radius * 2f) / 2f,
                                        (height - radius * 2f) / 2f
                                    )
                                )
                            }
                            startAngle += sweepAnglePercentage[i]
                        }
                    }
                }
                Text(currentCategorySumPrice.toString())
            }
        }
    }
}

@Composable
fun SpendingTable(
    categories: List<Category>,
    totalPriceFromAllCategories: Double,
    modifier: Modifier = Modifier
) {
    if (!isAllCategoriesEmpty(categories)) {
        Column(modifier = modifier.padding(8.dp)) {
            Text(
                text = "Расходы",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            LazyColumn {
                items(categories) { category ->
                    if (category.expenses.isNotEmpty()) {
                        ExpenseBlock(
                            category = category,
                            totalPrice = totalPriceFromAllCategories
                        )
                    }
                }
            }
        }
    }
    else {
        Text("Расходов/доходов не найдено. Добавьте их!")
    }
}

@Composable
fun ExpenseBlock(
    category: Category,
    totalPrice: Double,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val percentage = String.format("%.2f", (category.totalCategoryPrice / totalPrice) * 100)

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(Modifier.size(10.dp)) {
                    drawCircle(
                        color = category.color
                    )
                }
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = "$percentage %",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        // расходы выбранной категории
        AnimatedVisibility(visible = expanded) {
            if (expanded) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    (category.expenses).forEach {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = it.name)
                            Text(text = it.totalPrice.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomMenuBar(
    onAddButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier.fillMaxWidth()
        ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu"
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit"
            )
        }
        IconButton(onClick = onAddButtonClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Menu",
                modifier = Modifier
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, Color.Black), shape = CircleShape)
                    .size(40.dp)
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Menu"
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Menu"
            )
        }
   }
}

//@Preview
//@Composable
//fun MainPreview() {
//    val _string = ""
//    Main(
//        categories = FakeData.fakeCategories,
//        totalPriceFromAllCategories = 15000.0,
//        updateIsTappedFromPieChart = { _string -> },
//        onAddButtonClick = { /*TODO*/ }
//    )
//}