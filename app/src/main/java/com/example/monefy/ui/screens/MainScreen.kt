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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import com.example.monefy.data.Category
import com.example.monefy.data.Finance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun MainScreen(
    financesViewModel: FinancesViewModel,
    goToFinance: (Finance) -> Unit,
    updateScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val financesUiState by financesViewModel.uiState.collectAsState()
    financesViewModel.resetAllTapedCategories()

    Main(
        isAllNotTapped = financesViewModel::isAllNotTapped,
        showEmptyFinancesText = financesUiState.showEmptyFinancesText,
        selectedTabIndex = financesUiState.selectedTabIndex,
        getCategoriesByType = financesViewModel::getCategoriesByType,
        updateIsTapped = financesViewModel::updateIsTapped,
        getFinancesByCategoryId = financesViewModel::getFinancesByCategoryId,
        changeSelectedTabIndex = financesViewModel::changeSelectedTabIndex,
        isHasFinances = financesViewModel::isHasFinances,
        updateScreen = updateScreen,
        goToFinance = goToFinance,
        modifier = modifier
    )
}

@Composable
fun Main(
    isAllNotTapped: suspend () -> Boolean,
    showEmptyFinancesText: Boolean,
    selectedTabIndex: Int,
    changeSelectedTabIndex: (Int) -> Unit,
    getCategoriesByType: (String) -> Flow<List<Category>>,
    isHasFinances: suspend () -> Boolean,
    updateIsTapped: suspend (Category) -> Unit,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    updateScreen: () -> Unit,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            FinancesPieChart(
                isAllNotTapped = isAllNotTapped,
                showEmptyFinancesText = showEmptyFinancesText,
                selectedTabIndex = selectedTabIndex,
                isHasFinances = isHasFinances,
                getCategoriesByType = getCategoriesByType,
                updateIsTapped = updateIsTapped,
                getFinancesByCategoryId = getFinancesByCategoryId,
                changeSelectedTabIndex = changeSelectedTabIndex,
                updateScreen = updateScreen,
                modifier = modifier.weight(1.2f)
            )
            FinancesTable(
                selectedTabIndex = selectedTabIndex,
                getCategoriesByType = getCategoriesByType,
                isHasFinances = isHasFinances,
                getFinancesByCategoryId = getFinancesByCategoryId,
                goToFinance = goToFinance,
                modifier = modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FinancesPieChart(
    isAllNotTapped: suspend () -> Boolean,
    showEmptyFinancesText: Boolean,
    selectedTabIndex: Int,
    changeSelectedTabIndex: (Int) -> Unit,
    isHasFinances: suspend () -> Boolean,
    getCategoriesByType: (String) -> Flow<List<Category>>,
    updateIsTapped: suspend (Category) -> Unit,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    updateScreen: () -> Unit,
    radius: Float = 300f,
    modifier: Modifier = Modifier
) {
    val tabItems = listOf("Расходы", "Доходы")

    val categories = when(selectedTabIndex) {
        0 -> getCategoriesByType("Расходы").collectAsState(emptyList()).value
        1 -> getCategoriesByType("Доходы").collectAsState(emptyList()).value
        else -> emptyList()
    }.filter { it.totalCategoryPrice != 0.0 }
    val totalPriceFromAllCategories = categories.sumOf { it.totalCategoryPrice }
    val financesMap = categories.associate { category ->
        category.id to getFinancesByCategoryId(category.id).collectAsState(emptyList()).value
    }.filterValues { it.isNotEmpty() }
    val scope = rememberCoroutineScope()

    var hasFinances by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            hasFinances = isHasFinances()
        }
    }
    if (!showEmptyFinancesText) {
        Text("Расходов/доходов не найдено. Добавьте их!")
    }
    if (hasFinances) {
        val anglePerValue = (360 / totalPriceFromAllCategories)
        val sweepAnglePercentage = categories.map {
            (it.totalCategoryPrice * anglePerValue).toFloat()
        }
        var circleCenter by remember { mutableStateOf(Offset.Zero) }
        var currentCategorySumPrice by remember { mutableStateOf(totalPriceFromAllCategories) }

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            changeSelectedTabIndex(index)
                            updateScreen()
                        },
                        text = { Text(item) }
                    )
                }
            }
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
                                                    if (!category.isTapped)
                                                        currentCategorySumPrice = category.totalCategoryPrice
                                                    if (isAllNotTapped())
                                                        currentCategorySumPrice = totalPriceFromAllCategories
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
                        val finances = financesMap[categories[i].id] ?: emptyList()
                        if (finances.isNotEmpty()) {
                            val scale = if (categories[i].isTapped) 1.1f else 1.0f
                            scale(scale) {
                                drawArc(
                                    color = Color(categories[i].color),
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
fun FinancesTable(
    selectedTabIndex: Int,
    isHasFinances: suspend () -> Boolean,
    getCategoriesByType: (String) -> Flow<List<Category>>,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = when(selectedTabIndex) {
        0 -> getCategoriesByType("Расходы").collectAsState(emptyList()).value
        1 -> getCategoriesByType("Доходы").collectAsState(emptyList()).value
        else -> emptyList()
    }
    val totalPriceFromAllCategories = categories.sumOf { it.totalCategoryPrice }

    var hasFinances by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            hasFinances = isHasFinances()
        }
    }

    if (hasFinances) {
        Column(modifier = modifier.padding(8.dp)) {
            LazyColumn {
                items(categories) { category ->
                    val finances by getFinancesByCategoryId(category.id).collectAsState(emptyList())
                    var totalCategoriesPrice by rememberSaveable { mutableStateOf(0.0) }
                    LaunchedEffect(Unit) {
                        totalCategoriesPrice = totalPriceFromAllCategories
                    }

                    if (finances.isNotEmpty() && categories.all { !it.isTapped }) {
                        ExpenseBlock(
                            category = category,
                            totalPrice = totalCategoriesPrice,
                            getFinancesByCategoryId = getFinancesByCategoryId,
                            goToFinance = goToFinance,
                            showAllExpensesWithoutClick = false
                        )
                    }
                    else if (category.isTapped) {
                        ExpenseBlock(
                            category = category,
                            totalPrice = totalCategoriesPrice,
                            getFinancesByCategoryId = getFinancesByCategoryId,
                            goToFinance = goToFinance,
                            showAllExpensesWithoutClick = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseBlock(
    category: Category,
    totalPrice: Double,
    showAllExpensesWithoutClick: Boolean,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    val finances by getFinancesByCategoryId(category.id).collectAsState(emptyList())

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
                        color = Color(category.color)
                    )
                }
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = "${category.totalCategoryPrice} ($percentage %)",
                style = MaterialTheme.typography.titleSmall,
            )
        }
        if (showAllExpensesWithoutClick) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                finances.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { goToFinance(it) }
                            .fillMaxWidth()
                    ) {
                        Text(text = it.name)
                        Text(
                            text = String.format("%.2f", it.count.toDouble() * it.price),
                            style = MaterialTheme.typography.titleSmall
                        )
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
                        finances.forEach {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .clickable { goToFinance(it) }
                                    .fillMaxWidth()
                            ) {
                                Text(text = it.name)
                                Text(text = String.format("%.2f", it.count.toDouble() * it.price))
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