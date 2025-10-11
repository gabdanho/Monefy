package com.example.monefy.presentation.screens.main_monefy

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.monefy.R
import com.example.monefy.presentation.components.CustomDateRangePicker
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.utils.darken
import kotlinx.coroutines.launch
import kotlin.collections.forEach
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMonefyScreen(
    modifier: Modifier = Modifier,
    viewModel: MainMonefyScreenViewModel = hiltViewModel<MainMonefyScreenViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            // Выбор типа финансов: доходы или расходы
            FinanceTypeSelector(
                selectedFinanceTypeTabIndex = uiState.selectedFinanceTypeTabIndex,
                changeSelectedTabIndex = { viewModel.changeSelectedTabIndex(it) }
            )
            // Если финансы есть, позволяем пользователю выбрать нужный промежуток даты
            if (uiState.isHasFinances) {
                DateRangeSelector(
                    selectedDateRangeIndex = uiState.selectedDateRangeIndex,
                    showDateRangeDialog = { viewModel.changeIsShowDateRangeDialog(true) },
                    changeSelectedDateRangeIndex = { viewModel.changeSelectedDateRangeIndex(it) },
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            when {
                uiState.isLoading -> CircularProgressIndicator()
                !uiState.isHasFinances -> {
                    Text(text = "Операций не найдено. Добавьте их!")
                }
                else -> {
                    when {
                        uiState.categoryIdToFinances.isEmpty() -> Text(text = "Нет данных для отображения")
                        else -> {
                            if (uiState.categoriesToSumFinance.isNotEmpty()) {
                                FinancesPieChart(
                                    categoriesToSumFinance = uiState.categoriesToSumFinance,
                                    categoryIdToFinances = uiState.categoryIdToFinances,
                                    totalPriceFromAllCategories = uiState.totalPriceFromAllCategories,
                                    isAllNotTapped = uiState.isAllNotTapped,
                                    currentCategorySumPrice = uiState.currentCategorySumPrice,
                                    changeCurrentCategorySumPrice = {
                                        viewModel.changeCurrentCategorySumPrice(it)
                                    },
                                    updateIsTapped = { viewModel.updateIsTapped(it) },
                                    modifier = modifier.weight(1.2f)
                                )
                                FinancesTable(
                                    totalPriceFromAllCategories = uiState.totalPriceFromAllCategories,
                                    goToFinance = { viewModel.goToFinance(it) },
                                    categoryIdToFinances = uiState.categoryIdToFinances,
                                    categoriesToSumFinance = uiState.categoriesToSumFinance,
                                    modifier = modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Выскакивающая нижняя страница с выбором промежутка даты
    if (uiState.isShowDateRangeDialog) {
        ModalBottomSheet(onDismissRequest = { viewModel.changeIsShowDateRangeDialog(false) }) {
            CustomDateRangePicker(updateDateRange = { viewModel.updateCustomDateRange(it) })
        }
    }
}

// Донат с категориями
@Composable
private fun FinancesPieChart(
    currentCategorySumPrice: Double,
    totalPriceFromAllCategories: Double,
    isAllNotTapped: Boolean,
    categoriesToSumFinance: Map<Category, Double>,
    categoryIdToFinances: Map<Int, List<Finance>>,
    changeCurrentCategorySumPrice: (Category?) -> Unit,
    updateIsTapped: (Category) -> Unit,
    modifier: Modifier = Modifier,
    radius: Dp = 80.dp,
) {
    val radius = LocalDensity.current.run { radius.toPx() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            val anglePerValue = (360 / totalPriceFromAllCategories)

            val sweepAnglePercentage = categoriesToSumFinance.values.toList().map {
                (it * anglePerValue)
            }
            var circleCenter by remember { mutableStateOf(Offset.Zero) }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(true) {
                        detectTapGestures(
                            onTap = { offset ->
                                // проверяем был ли тач в области диаграммы
                                if ((offset.x - circleCenter.x).toDouble()
                                        .pow(2.0) + (offset.y - circleCenter.y).toDouble()
                                        .pow(2.0) <= (radius.toDouble() + radius / 3f).pow(2.0) &&
                                    (offset.x - circleCenter.x).toDouble()
                                        .pow(2.0) + (offset.y - circleCenter.y).toDouble()
                                        .pow(2.0) >= (radius.toDouble() - radius / 3f).pow(2.0)
                                ) {

                                    // тап -> переводим в углы
                                    val tapAngleInDegrees = (-atan2(
                                        x = circleCenter.y - offset.y,
                                        y = circleCenter.x - offset.x
                                    ) * (180f / PI).toFloat() - 90f).mod(360f)

                                    // по углу смотрим в какую категорию попадаем
                                    var currentAngle = 0.0

                                    categoriesToSumFinance.forEach { category, _ ->
                                        currentAngle += category.totalCategoryPrice * anglePerValue
                                        if (tapAngleInDegrees < currentAngle) {
                                            scope.launch {
                                                updateIsTapped(category)
                                                if (!category.isTapped)
                                                    changeCurrentCategorySumPrice(category)
                                                if (isAllNotTapped)
                                                    changeCurrentCategorySumPrice(null)
                                            }
                                            return@forEach
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
                categoriesToSumFinance.keys.indices.forEach { id ->
                    val categories = categoriesToSumFinance.keys.toList()
                    // Тень категории
                    drawArc(
                        color = Color(
                            categories[id].colorLong
                                ?: Color.Transparent.toColorLong()
                        ).darken(0.4f),
                        startAngle = startAngle,
                        sweepAngle = sweepAnglePercentage[id].toFloat(),
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

                    val finances =
                        categoryIdToFinances[categoriesToSumFinance.keys.toList()[id].id]
                            ?: emptyList()
                    if (finances.isNotEmpty()) {
                        val scale =
                            if (categoriesToSumFinance.keys.toList()[id].isTapped) 1.1f else 1.0f
                        scale(scale) {
                            drawArc(
                                color = Color(
                                    categories[id].colorLong ?: Color.Transparent.toColorLong()
                                ),
                                startAngle = startAngle,
                                sweepAngle = sweepAnglePercentage[id].toFloat(),
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
                        startAngle += sweepAnglePercentage[id].toFloat()
                    }
                }
            }
            Text(String.format("%.2f", currentCategorySumPrice))
        }
    }
}

// Таблица категорий и финансов
@Composable
private fun FinancesTable(
    totalPriceFromAllCategories: Double,
    categoryIdToFinances: Map<Int, List<Finance>>,
    categoriesToSumFinance: Map<Category, Double>,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier,
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
                val finances = categoryIdToFinances[category.id] ?: emptyList()
                val financesFilteredByDate = finances.sortedBy { it.date }

                if (financesFilteredByDate.isNotEmpty() && categoriesToSumFinance.keys.toList()
                        .all { !it.isTapped }
                ) {
                    CategoryBlock(
                        finances = financesFilteredByDate,
                        category = category,
                        categorySum = categoriesToSumFinance[category] ?: 0.0,
                        totalPrice = totalPriceFromAllCategories,
                        goToFinance = goToFinance,
                        showCurrentCategoryWithFinances = false
                    )
                } else if (category.isTapped) {
                    CategoryBlock(
                        finances = financesFilteredByDate,
                        category = category,
                        categorySum = categoriesToSumFinance[category] ?: 0.0,
                        totalPrice = totalPriceFromAllCategories,
                        goToFinance = goToFinance,
                        showCurrentCategoryWithFinances = true
                    )
                }
            }
        }
    }
}

// Блок категории
@Composable
private fun CategoryBlock(
    finances: List<Finance>,
    category: Category,
    categorySum: Double,
    totalPrice: Double,
    showCurrentCategoryWithFinances: Boolean,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier,
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
                        color = Color.fromColorLong(
                            category.colorLong ?: Color.Transparent.toColorLong()
                        )
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
        if (showCurrentCategoryWithFinances) {
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
private fun FinancesBlock(
    finances: List<Finance>,
    goToFinance: (Finance) -> Unit,
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

@Composable
private fun FinanceTypeSelector(
    selectedFinanceTypeTabIndex: Int,
    changeSelectedTabIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedFinanceTypeTabIndex,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        val tabItems = LocalResources.current.getStringArray(R.array.tabItems)

        tabItems.forEachIndexed { index, item ->
            Tab(
                selected = index == selectedFinanceTypeTabIndex,
                onClick = { changeSelectedTabIndex(index) },
                text = { Text(text = item) }
            )
        }
    }
}

@Composable
private fun DateRangeSelector(
    selectedDateRangeIndex: Int,
    showDateRangeDialog: () -> Unit,
    changeSelectedDateRangeIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedDateRangeIndex,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        val tabDateRangeItems = LocalResources.current.getStringArray(R.array.tabDateRangeItems)

        tabDateRangeItems.forEachIndexed { index, item ->
            Tab(
                selected = index == selectedDateRangeIndex,
                onClick = {
                    // Если выбран таб кастомной даты, то выскакивает выбор даты
                    if (index == 0) {
                        showDateRangeDialog()
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
                    if (index == 0) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Выбрать собственный промежуток времени",
                            modifier = Modifier
                        )
                    } else {
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