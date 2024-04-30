package com.example.monefy.screen

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.model.Category
import com.example.monefy.model.fake.FakeData
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val spendingViewModel = SpendingViewModel(FakeData.fakeCategories)

    Scaffold(
        bottomBar = { BottomMenuBar() },
        modifier = modifier
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            SpendingPieChart(
                spendingViewModel = spendingViewModel,
                modifier = Modifier.weight(1f)
            )
            SpendingTable(
                spendingViewModel = spendingViewModel,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}

@Composable
fun SpendingPieChart(
    radius: Float = 300f,
    spendingViewModel: SpendingViewModel,
    modifier: Modifier = Modifier
) {
    val spendingUiState by spendingViewModel.uiState.collectAsState()

    val categoriesList = spendingUiState.categories
    val totalPrice = spendingUiState.totalPriceFromCategories
    val anglePerValue = (360 / totalPrice)
    val sweepAnglePercentage = categoriesList.map {
        (it.totalCategoryPrice * anglePerValue).toFloat()
    }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var currentCategoryName by remember { mutableStateOf("") }

    Box(
        modifier = modifier,
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
                                // тап переводим в углы
                                val tapAngleInDegrees = (-atan2(
                                    x = circleCenter.y - offset.y,
                                    y = circleCenter.x - offset.x
                                ) * (180f / PI).toFloat() - 90f).mod(360f)

                                // по углу смотрим в какую категорию попадаем
                                var currentAngle = 0f
                                spendingUiState.categories.forEach { category ->
                                    currentAngle += category.totalCategoryPrice.toFloat() * anglePerValue.toFloat()
                                    if (tapAngleInDegrees < currentAngle) {
                                        val name = category.name
                                        spendingViewModel.updateIsTappedFromPieChart(name)
                                        currentCategoryName = if (!category.isTapped) name else ""
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

            for (i in categoriesList.indices) {
                val scale = if (categoriesList[i].isTapped) 1.1f else 1.0f
                scale(scale) {
                    drawArc(
                        color = categoriesList[i].color,
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
        Text(
            text = currentCategoryName
        )
    }
}

@Composable
fun SpendingTable(
    spendingViewModel: SpendingViewModel,
    modifier: Modifier = Modifier
) {
    val spendingUiState by spendingViewModel.uiState.collectAsState()

    val totalPrice = spendingUiState.totalPriceFromCategories
    val categoriesList = spendingUiState.categories

    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Расходы",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        LazyColumn {
            items(categoriesList) { category ->
                ExpenseBlock(
                    category = category,
                    totalPrice = totalPrice
                )
            }
        }
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
                            Text(text = it.sumPrice.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomMenuBar(modifier: Modifier = Modifier) {
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
        IconButton(onClick = { /*TODO*/ }) {
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

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}