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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.monefy.data.Category
import com.example.monefy.data.Finance
import com.example.monefy.utils.Constants.tabDateRangeItems
import com.example.monefy.utils.Constants.tabItems
import com.example.monefy.utils.CustomDateRangePicker
import com.example.monefy.utils.isDateInRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.lang.Math.pow
import java.time.LocalDate
import kotlin.math.PI
import kotlin.math.atan2

fun Color.darken(factor: Float): Color {
    return Color(
        red = (this.red * (1 - factor)).coerceIn(0f, 1f),
        green = (this.green * (1 - factor)).coerceIn(0f, 1f),
        blue = (this.blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}

@Composable
fun MainScreen(
    financesViewModel: FinancesViewModel,
    goToFinance: (Finance) -> Unit,
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
        goToFinance = goToFinance,
        modifier = modifier
    )
}

// Формируем экран со всем содержимым (донат, таблица с категориями и финансами)
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
    updateIsTapped: suspend (Category) -> Unit,
    getCategoriesByType: (String) -> Flow<List<Category>>,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    updateCustomDateRange: (List<LocalDate>) -> Unit,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    var isHasFinances by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier) { innerPadding ->
        // Выскакивающая нижняя страница с выбором промежутка даты
        if (showDateRangeDialog) {
            ModalBottomSheet(onDismissRequest = { changeShowDateRangeDialog(false) }) {
                CustomDateRangePicker(
                    changeShowDateRangeDialog = changeShowDateRangeDialog,
                    updateDateRange = updateCustomDateRange
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            // Выбор типа финансов: доходы или расходы
            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            changeSelectedTabIndex(index)
                        },
                        text = { Text(item) }
                    )
                }
            }
            // Если финансы есть, позволяем пользователю выбрать нужный промежуток даты
            if (isHasFinances) {
                TabRow(
                    selectedTabIndex = selectedDateRangeIndex,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    tabDateRangeItems.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectedDateRangeIndex,
                            onClick = {
                                // Если выбран таб кастомной даты, то выскакивает выбор даты
                                if (index == 0) {
                                    changeShowDateRangeDialog(true)
                                }
                                changeSelectedDateRangeIndex(index)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                            ) {
                                // "..." - свой промежуток даты
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
            }

            var categories by remember { mutableStateOf<List<Category>?>(null) }
            var financesMap by remember { mutableStateOf<Map<Int, List<Finance>>?>(null) }
            // Следующие две булевые переменные нужны для определения, что все данные загрузились, прежде чем их выводить
            var isLoadingCategories by remember { mutableStateOf(true) }
            var isLoadingFinances by remember { mutableStateOf(true) }

            // Получаем список категорий
            LaunchedEffect(selectedTabIndex, selectedDateRangeIndex) {
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

            if (isLoadingCategories) { /* ничего не делаем */  }
            // Проверка, если доходов/расходов нет
            else if (categories.isNullOrEmpty()) {
                isHasFinances = false
                if (selectedTabIndex == 0) {
                    Text("Расходов не найдено. Добавьте их!")
                } else if (selectedTabIndex == 1) {
                    Text("Доходов не найдено. Добавьте их!")
                }
            }
            // Если доходы/расходы есть работаем с выбором промежутка даты
            else {
                isHasFinances = true
                val dateRange = when(selectedDateRangeIndex) {
                    // Кастомный промежуток даты
                    0 -> {
                        customDateRange
                    }
                    // Финансы за год
                    1 -> {
                        val now = LocalDate.now()
                        val startOfYear = now.withDayOfYear(1)
                        val endOfYear = now.withDayOfYear(now.lengthOfYear())
                        listOf(startOfYear, endOfYear)
                    }
                    // Финансы за месяц
                    2 -> {
                        val now = LocalDate.now()
                        val startOfMonth = now.withDayOfMonth(1)
                        val endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
                        listOf(startOfMonth, endOfMonth)
                    }
                    // Финансы за сегодняшний день
                    3 -> {
                        listOf(LocalDate.now(), LocalDate.now())
                    }
                    else -> listOf(LocalDate.now(), LocalDate.now())
                }

                // Получаем мапу с финансами доходов или расходов (крч, что выбрал пользователь)
                LaunchedEffect(selectedTabIndex, selectedDateRangeIndex) {
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

                var categoriesToSumFinance by remember { mutableStateOf(mapOf<Category, Double>()) }

                if (isLoadingFinances) { /* Ничего не делаем */ }
                // Если финансов доходов/расходов нет - то выводим сообщение
                else if (financesMap.isNullOrEmpty()) {
                    Text(text = "Нет данных для отображения")
                }
                // Если всё есть, готовим данные для основных функций (pie chart и список финансов)
                else {
                    val finances = financesMap!!.values.flatten()
                    // Мапа с категориями и тотал суммой их
                    categoriesToSumFinance = categories!!.filter { it.id in financesMap!!.keys }.associate { category ->
                        category to finances.filter { it.categoryId == category.id }.sumOf { it.price * it.count.toDouble() }
                    }

                    // Тотал сумма со всех категорий
                    var totalPriceFromAllCategories = 0.0
                    financesMap!!.values.forEach { category ->
                        category.forEach { finance ->
                            totalPriceFromAllCategories += finance.price * finance.count.toDouble()
                        }
                    }

                    // Передаём данные
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

// Донат с категориями
@Composable
fun FinancesPieChart(
    totalPriceFromAllCategories: Double,
    categoriesToSumFinance: Map<Category, Double>,
    financesMap: Map<Int, List<Finance>>,
    isAllNotTapped: suspend () -> Boolean,
    updateIsTapped: suspend (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val radius = LocalDensity.current.run { 80.dp.toPx() }
    val scope = rememberCoroutineScope()

    // Переменная для отображения суммы текущей категории (если никакая не выбрана, то тотал сумма)
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

                // Обработка категорий для отрисовки секций доната
                for (i in categoriesToSumFinance.keys.indices) {
                    // Тень категории
                    drawArc(
                        color = Color(categoriesToSumFinance.keys.toList()[i].color).darken(0.4f),
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

// Таблица категорий и финансов
@Composable
fun FinancesTable(
    totalPriceFromAllCategories: Double,
    categoriesToSumFinance: Map<Category, Double>,
    dateRange: List<LocalDate>,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp
        )
    ) {
        // Выводим категории и их блоки с финансами
        LazyColumn(modifier = modifier.padding(16.dp)) {
            items(categoriesToSumFinance.keys.toList()) { category ->
                val finances by getFinancesByCategoryId(category.id).collectAsState(emptyList())
                val filteredByDateFinances = finances.filter { isDateInRange(it.date, dateRange[0], dateRange[1]) }

                if (filteredByDateFinances.isNotEmpty() && categoriesToSumFinance.keys.toList().all { !it.isTapped }) {
                    CategoryBlock(
                        finances = filteredByDateFinances,
                        category = category,
                        categorySum = categoriesToSumFinance[category] ?: 0.0,
                        totalPrice = totalPriceFromAllCategories,
                        goToFinance = goToFinance,
                        showAllExpensesWithoutClick = false
                    )
                }
                else if (category.isTapped) {
                    CategoryBlock(
                        finances = filteredByDateFinances,
                        category = category,
                        categorySum = categoriesToSumFinance[category] ?: 0.0,
                        totalPrice = totalPriceFromAllCategories,
                        goToFinance = goToFinance,
                        showAllExpensesWithoutClick = true
                    )
                }
            }
        }
    }
}

// Блок категории
@Composable
fun CategoryBlock(
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
                // Кружочек с цветом категории
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
                text = "$categorySum ($percentage %)",
                style = MaterialTheme.typography.titleSmall,
            )
        }
        // Если пользователь нажал на секцию с категорией, то выводим в таблице категорию и соответствующие ей финансы
        if (showAllExpensesWithoutClick) {
            FinancesBlock(
                finances = finances,
                goToFinance = goToFinance
            )
        }
        // Если секция категории не нажата, то выведутся все категории
        else {
            AnimatedVisibility(visible = expanded) {
                // Кликнув по категории, анимированно показываются финансы текущей категории
                if (expanded) {
                    FinancesBlock(
                        finances = finances,
                        goToFinance = goToFinance
                    )
                }
            }
        }
    }
}

// Блок финансов
@Composable
fun FinancesBlock(
    finances: List<Finance>,
    goToFinance: (Finance) -> Unit
) {
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