package com.example.monefy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.monefy.data.Category
import com.example.monefy.data.ColorConverter
import com.example.monefy.data.Spend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun MainScreen(
    spendingViewModel: SpendingViewModel,
    modifier: Modifier = Modifier
) {
    Main(
        getAllCategories = spendingViewModel::getAllCategories,
        isHasSpends = spendingViewModel::isHasSpends,
        totalPriceFromAllCategories = spendingViewModel::totalPriceFromAllCategories,
        updateIsTapped = spendingViewModel::updateIsTapped,
        getSpendsByCategoryId = spendingViewModel::getSpendsByCategoryId,
        modifier = modifier
    )
}

@Composable
fun Main(
    getAllCategories: () -> Flow<List<Category>>,
    isHasSpends: suspend () -> Boolean,
    totalPriceFromAllCategories: () -> Double,
    updateIsTapped: suspend (Category) -> Unit,
    getSpendsByCategoryId: (Int) -> Flow<List<Spend>>,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            SpendingPieChart(
                isHasSpends = isHasSpends,
                getAllCategories = getAllCategories,
                totalPriceFromAllCategories = totalPriceFromAllCategories,
                updateIsTapped = updateIsTapped,
                getSpendsByCategoryId = getSpendsByCategoryId,
                modifier = modifier.weight(1.2f)
            )
            SpendingTable(
                getAllCategories = getAllCategories,
                totalPriceFromAllCategories = totalPriceFromAllCategories,
                isHasSpends = isHasSpends,
                getSpendsByCategoryId = getSpendsByCategoryId,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SpendingPieChart(
    isHasSpends: suspend () -> Boolean,
    getAllCategories: () -> Flow<List<Category>>,
    totalPriceFromAllCategories: () -> Double,
    updateIsTapped: suspend (Category) -> Unit,
    getSpendsByCategoryId: (Int) -> Flow<List<Spend>>,
    radius: Float = 300f,
    modifier: Modifier = Modifier
) {
    val categories by getAllCategories().collectAsState(emptyList())
    val spendsMap = categories.associate { category ->
        category.id to getSpendsByCategoryId(category.id).collectAsState(emptyList()).value
    }
    val scope = rememberCoroutineScope()

    var hasSpends by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasSpends = isHasSpends()
    }

    if (hasSpends) {
        val anglePerValue = (360 / totalPriceFromAllCategories())
        val sweepAnglePercentage = categories.map {
            (it.totalCategoryPrice * anglePerValue).toFloat()
        }
        var circleCenter by remember { mutableStateOf(Offset.Zero) }
        var currentCategoryName by remember { mutableStateOf("Все расходы") }
        var currentCategorySumPrice by remember { mutableStateOf(totalPriceFromAllCategories()) }

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
                                        categories.forEach { category ->
                                            currentAngle += category.totalCategoryPrice.toFloat() * anglePerValue.toFloat()
                                            if (tapAngleInDegrees < currentAngle) {
                                                scope.launch {
                                                    updateIsTapped(category)
                                                    if (!category.isTapped) {
                                                        currentCategoryName = category.name
                                                        currentCategorySumPrice =
                                                            category.totalCategoryPrice
                                                    } else {
                                                        currentCategoryName = "Все расходы"
                                                        currentCategorySumPrice =
                                                            totalPriceFromAllCategories()
                                                    }
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

                    for (i in categories.indices) {
                        val spends = spendsMap[categories[i].id] ?: emptyList()
                        if (spends.isNotEmpty()) {
                            val scale = if (categories[i].isTapped) 1.1f else 1.0f
                            scale(scale) {
                                drawArc(
                                    color = Color.Red,
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
                Text(String.format("%.2f", currentCategorySumPrice))
            }
        }
    }
}

@Composable
fun SpendingTable(
    isHasSpends: suspend () -> Boolean,
    totalPriceFromAllCategories: () -> Double,
    getAllCategories: () -> Flow<List<Category>>,
    getSpendsByCategoryId: (Int) -> Flow<List<Spend>>,
    modifier: Modifier = Modifier
) {
    val categories by getAllCategories().collectAsState(emptyList())

    val coroutineScope = rememberCoroutineScope()

    var hasSpends = false
    coroutineScope.launch {
        hasSpends = isHasSpends()
    }

    if (hasSpends) {
        Column(modifier = modifier.padding(8.dp)) {
            Text(
                text = "Расходы",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            LazyColumn {
                items(categories) { category ->
                    val spends by getSpendsByCategoryId(category.id).collectAsState(emptyList())
                    if (spends.isNotEmpty() && categories.all { !it.isTapped }) {
                        ExpenseBlock(
                            category = category,
                            totalPrice = totalPriceFromAllCategories(),
                            getSpendsByCategoryId = getSpendsByCategoryId,
                            showAllExpensesWithoutClick = false
                        )
                    }
                    else if (category.isTapped) {
                        ExpenseBlock(
                            category = category,
                            totalPrice = totalPriceFromAllCategories(),
                            getSpendsByCategoryId = getSpendsByCategoryId,
                            showAllExpensesWithoutClick = true
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
    showAllExpensesWithoutClick: Boolean,
    getSpendsByCategoryId: (Int) -> Flow<List<Spend>>,
    modifier: Modifier = Modifier
) {
    val colorConverter = ColorConverter()

    val spends by getSpendsByCategoryId(category.id).collectAsState(emptyList())

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
                        color = colorConverter.toColor(category.color)
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
        if (showAllExpensesWithoutClick) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                spends.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = it.name)
                        Text(text = String.format("%.2f", 52.52525252))
                    }
                }
            }
        }
        else {
            AnimatedVisibility(visible = expanded) {
                if (expanded) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        spends.forEach {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = it.name)
                                Text(text = String.format("%.2f", 52.525252))
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun MainPreview() {
//    val spendingViewModel = viewModel(factory = SpendingViewModel.factory)
//    Main(
//        categories = FakeData.fakeCategories,
//        totalPriceFromAllCategories = 15000.0,
//        spendingViewModel = spendingViewModel
//    )
//}