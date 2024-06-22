package com.example.monefy.ui.screens

import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.example.monefy.utils.CustomDateRangePicker
import com.example.monefy.utils.isDateInRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.lang.Math.pow
import java.time.LocalDate
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
        customDateRange = financesUiState.customDateRange,
        showDateRangeDialog = financesUiState.showDateRangeDialog,
        isAllNotTapped = financesViewModel::isAllNotTapped,
        selectedTabIndex = financesUiState.selectedTabIndex,
        selectedDateRangeIndex = financesUiState.selectedDateRangeIndex,
        getCategoriesByType = financesViewModel::getCategoriesByType,
        updateIsTapped = financesViewModel::updateIsTapped,
        getFinancesByCategoryId = financesViewModel::getFinancesByCategoryId,
        changeSelectedTabIndex = financesViewModel::changeSelectedTabIndex,
        changeSelectedDateRangeIndex = financesViewModel::changeSelectedDateRangeIndex,
        changeShowDateRangeDialog = financesViewModel::changeShowDateRangeDialog,
        updateCustomDateRange = financesViewModel::updateCustomDateRange,
        updateScreen = updateScreen,
        goToFinance = goToFinance,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    customDateRange: List<LocalDate>,
    isAllNotTapped: suspend () -> Boolean,
    showDateRangeDialog: Boolean,
    changeShowDateRangeDialog: (Boolean) -> Unit,
    selectedTabIndex: Int,
    selectedDateRangeIndex: Int,
    changeSelectedTabIndex: (Int) -> Unit,
    changeSelectedDateRangeIndex: (Int) -> Unit,
    getCategoriesByType: (String) -> Flow<List<Category>>,
    updateIsTapped: suspend (Category) -> Unit,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    updateCustomDateRange: (List<LocalDate>) -> Unit,
    updateScreen: () -> Unit,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabItems = listOf("Расходы", "Доходы")
    val tabDateRangeItems = listOf("...", "Год", "Месяц", "Сегодня")

    Scaffold(modifier = modifier) { innerPadding ->
        if (showDateRangeDialog) {
            ModalBottomSheet(onDismissRequest = { changeShowDateRangeDialog(false) }) {
                CustomDateRangePicker(
                    changeShowDateRangeDialog = changeShowDateRangeDialog,
                    updateDateRange = updateCustomDateRange,
                    updateScreen = updateScreen
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
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
            TabRow(
                selectedTabIndex = selectedDateRangeIndex,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                tabDateRangeItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedDateRangeIndex,
                        onClick = {
                            if (index == 0) {
                                changeShowDateRangeDialog(true)
                            }
                            changeSelectedDateRangeIndex(index)
                            updateScreen()
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                        ) {
                            if (item == "...") {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "Выбрать собственный промежуток времени",
                                    modifier = Modifier
                                )
                            }
                            else {
                                Text(
                                    text = item,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }

            var categories by remember { mutableStateOf<List<Category>?>(null) }
            var financesMap by remember { mutableStateOf<Map<Int, List<Finance>>?>(null) }
            var isLoadingCategories by remember { mutableStateOf(true) }
            var isLoadingFinances by remember { mutableStateOf(true) }

            LaunchedEffect(selectedTabIndex) {
                isLoadingCategories = true
                val flow = if (selectedTabIndex == 0)
                    getCategoriesByType("Расходы")
                else
                    getCategoriesByType("Доходы")

                flow.collect { fetchedCategories ->
                    categories = fetchedCategories.filter { it.totalCategoryPrice != 0.0 }
                    isLoadingCategories = false
                }
            }

            if (isLoadingCategories) { }
            else if (categories.isNullOrEmpty()) {
                if (selectedTabIndex == 0) {
                    Text("Расходов не найдено. Добавьте их!")
                } else if (selectedTabIndex == 1) {
                    Text("Доходов не найдено. Добавьте их!")
                }
            }
            else {
                val dateRange = when(selectedDateRangeIndex) {
                    0 -> {
                        customDateRange
                    }
                    1 -> {
                        val now = LocalDate.now()
                        val startOfYear = now.withDayOfYear(1)
                        val endOfYear = now.withDayOfYear(now.lengthOfYear())
                        listOf(startOfYear, endOfYear)
                    }
                    2 -> {
                        val now = LocalDate.now()
                        val startOfMonth = now.withDayOfMonth(1)
                        val endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
                        listOf(startOfMonth, endOfMonth)
                    }
                    3 -> {
                        listOf(LocalDate.now(), LocalDate.now())
                    }
                    else -> listOf(LocalDate.now(), LocalDate.now())
                }
                Log.i("MainScreen", dateRange.toString())

                LaunchedEffect(Unit) {
                    isLoadingFinances = true
                    val flowMap = categories!!.associate { category ->
                        category.id to getFinancesByCategoryId(category.id)
                    }

                    val combinedFlow = combine(flowMap.values.toList()) { financeLists ->
                        flowMap.keys.zip(financeLists).toMap()
                    }

                    combinedFlow.collect { newFinancesMap ->
                        financesMap = newFinancesMap.mapValues { (_, finances) ->
                            finances.filter { isDateInRange(it.date, dateRange[0], dateRange[1]) }
                        }.filterValues { it.isNotEmpty() }
                        isLoadingFinances = false
                    }
                }
                if (isLoadingFinances) { }
                else if (financesMap.isNullOrEmpty()) {
                    Text(text = "Нет данных для отображения")
                }
                else {
                    val finances = financesMap!!.values.flatten()
                    val categoriesToSumFinance = categories!!.filter { it.id in financesMap!!.keys }.associate { category ->
                        category to finances.filter { it.categoryId == category.id }.sumOf { it.price * it.count.toDouble() }
                    }

                    var totalPriceFromAllCategories = 0.0
                    financesMap!!.values.forEach { category ->
                        category.forEach { finance ->
                            totalPriceFromAllCategories += finance.price * finance.count.toDouble()
                        }
                    }
                    if (categoriesToSumFinance.isNotEmpty() && totalPriceFromAllCategories != 0.0) {
                        FinancesPieChart(
                            categoriesToSumFinance = categoriesToSumFinance,
                            financesMap = financesMap!!,
                            totalPriceFromAllCategories = totalPriceFromAllCategories,
                            isAllNotTapped = isAllNotTapped,
                            updateIsTapped = updateIsTapped,
                            modifier = modifier.weight(1.2f)
                        )
                        FinancesTable(
                            totalPriceFromAllCategories = totalPriceFromAllCategories,
                            categoriesToSumFinance = categoriesToSumFinance,
                            dateRange = dateRange,
                            getFinancesByCategoryId = getFinancesByCategoryId,
                            goToFinance = goToFinance,
                            modifier = modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FinancesPieChart(
    totalPriceFromAllCategories: Double,
    categoriesToSumFinance: Map<Category, Double>,
    financesMap: Map<Int, List<Finance>>,
    isAllNotTapped: suspend () -> Boolean,
    updateIsTapped: suspend (Category) -> Unit,
    radius: Float = 300f,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var currentCategorySumPrice by remember { mutableStateOf(totalPriceFromAllCategories) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            val anglePerValue = (360 / totalPriceFromAllCategories)

            val sweepAnglePercentage = categoriesToSumFinance.values.toList().map {
                (it * anglePerValue).toFloat()
            }
            var circleCenter by remember { mutableStateOf(Offset.Zero) }

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

                                    categoriesToSumFinance.forEach { category ->
                                        currentAngle += category.value.toFloat() * anglePerValue.toFloat()
                                        if (tapAngleInDegrees < currentAngle) {
                                            scope.launch {
                                                updateIsTapped(category.key)
                                                if (!category.key.isTapped)
                                                    currentCategorySumPrice =
                                                        financesMap[category.key.id]?.sumOf { it.price * it.count.toDouble() }
                                                            ?: 0.0
                                                if (isAllNotTapped())
                                                    currentCategorySumPrice =
                                                        totalPriceFromAllCategories
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

                for (i in categoriesToSumFinance.keys.indices) {
                    val finances = financesMap[categoriesToSumFinance.keys.toList()[i].id] ?: emptyList()
                    if (finances.isNotEmpty()) {
                        val scale = if (categoriesToSumFinance.keys.toList()[i].isTapped) 1.1f else 1.0f
                        scale(scale) {
                            drawArc(
                                color = Color(categoriesToSumFinance.keys.toList()[i].color),
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

@Composable
fun FinancesTable(
    totalPriceFromAllCategories: Double,
    categoriesToSumFinance: Map<Category, Double>,
    dateRange: List<LocalDate>,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        LazyColumn {
            items(categoriesToSumFinance.keys.toList()) { category ->
                val finances by getFinancesByCategoryId(category.id).collectAsState(emptyList())
                val filteredByDateFinances = finances.filter { isDateInRange(it.date, dateRange[0], dateRange[1]) }

                var totalCategoriesPrice by rememberSaveable { mutableStateOf(0.0) }
                LaunchedEffect(Unit) {
                    totalCategoriesPrice = totalPriceFromAllCategories
                }

                if (filteredByDateFinances.isNotEmpty() && categoriesToSumFinance.keys.toList().all { !it.isTapped }) {
                    ExpenseBlock(
                        finances = filteredByDateFinances,
                        category = category,
                        categorySum = categoriesToSumFinance[category] ?: 0.0,
                        totalPrice = totalCategoriesPrice,
                        goToFinance = goToFinance,
                        showAllExpensesWithoutClick = false
                    )
                }
                else if (category.isTapped) {
                    ExpenseBlock(
                        finances = filteredByDateFinances,
                        category = category,
                        categorySum = categoriesToSumFinance[category] ?: 0.0,
                        totalPrice = totalCategoriesPrice,
                        goToFinance = goToFinance,
                        showAllExpensesWithoutClick = true
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseBlock(
    finances: List<Finance>,
    category: Category,
    categorySum: Double,
    totalPrice: Double,
    showAllExpensesWithoutClick: Boolean,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val percentage = String.format("%.2f", (categorySum / totalPrice) * 100)

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
                text = "${categorySum} ($percentage %)",
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