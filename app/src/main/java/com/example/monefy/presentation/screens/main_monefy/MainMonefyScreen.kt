package com.example.monefy.presentation.screens.main_monefy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.style.TextAlign
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
            modifier = Modifier.consumeWindowInsets(innerPadding)
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
                    Text(
                        text = "Нет данных для отображения",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                else -> {
                    when {
                        uiState.categoryIdToFinances.isEmpty() -> Text(text = "Нет данных для отображения")
                        else -> {
                            if (uiState.categoriesToSumFinance.isNotEmpty()) {
                                FinancesPieChart(
                                    categoriesToSumFinance = uiState.categoriesToSumFinance,
                                    currentCategorySumPrice = uiState.currentCategorySumPrice,
                                    totalSum = uiState.totalSum,
                                    onCategoryTapped = { viewModel.onCategoryTapped(it) },
                                    modifier = Modifier.weight(1.2f),
                                )
                                FinancesTable(
                                    totalSum = uiState.totalSum,
                                    goToFinance = { viewModel.goToFinance(it) },
                                    categoryIdToFinances = uiState.categoryIdToFinances,
                                    categoriesToSumFinance = uiState.categoriesToSumFinance,
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
fun FinancesPieChart(
    totalSum: Double,
    currentCategorySumPrice: Double,
    categoriesToSumFinance: Map<Category, Double>,
    onCategoryTapped: (Category) -> Unit,
    modifier: Modifier = Modifier,
    radiusDp: Dp = 120.dp,
) {
    val radius = LocalDensity.current.run { radiusDp.toPx() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            val anglePerValue = 360f / totalSum
            val sweepAngleList =
                categoriesToSumFinance.values.map { (it * anglePerValue).toFloat() }
            var circleCenter by remember { mutableStateOf(Offset.Zero) }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(true) {
                        detectTapGestures(
                            onTap = { offset ->
                                val dx = offset.x - circleCenter.x
                                val dy = offset.y - circleCenter.y
                                val dist2 = dx * dx + dy * dy

                                // Проверяем попадание в кольцо
                                if (dist2 <= (radius + radius / 3f).pow(2) &&
                                    dist2 >= (radius - radius / 3f).pow(2)
                                ) {
                                    val tapAngle = (-atan2(
                                        x = circleCenter.y - offset.y,
                                        y = circleCenter.x - offset.x
                                    ) * (180f / PI).toFloat() - 90f).mod(360f)

                                    var currentAngle = 0f
                                    for ((category, value) in categoriesToSumFinance) {
                                        currentAngle += (value * anglePerValue).toFloat()
                                        if (tapAngle < currentAngle) {
                                            scope.launch {
                                                onCategoryTapped(category)
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
                circleCenter = Offset(width / 2f, height / 2f)

                val categories = categoriesToSumFinance.keys.toList()

                for (i in categories.indices) {
                    val category = categories[i]

                    // тень
                    drawArc(
                        color = Color(
                            category.colorLong ?: Color.Transparent.toArgb().toLong()
                        ).darken(0.4f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngleList[i],
                        useCenter = false,
                        style = Stroke(width = radius / 3f),
                        size = Size(radius * 2f, radius * 2f),
                        topLeft = Offset(
                            (width - radius * 2f) / 2f,
                            (height - radius * 2f) / 2f
                        )
                    )

                    val scaleFactor = if (category.isTapped) 1.1f else 1.0f
                    scale(scaleFactor) {
                        drawArc(
                            color = Color(
                                category.colorLong ?: Color.Transparent.toArgb().toLong()
                            ),
                            startAngle = startAngle,
                            sweepAngle = sweepAngleList[i],
                            useCenter = false,
                            style = Stroke(width = radius / 3f),
                            size = Size(radius * 2f, radius * 2f),
                            topLeft = Offset(
                                (width - radius * 2f) / 2f,
                                (height - radius * 2f) / 2f
                            )
                        )
                    }

                    startAngle += sweepAngleList[i]
                }
            }

            Text(String.format("%.2f", currentCategorySumPrice))
        }
    }
}

// Таблица категорий и финансов
@Composable
private fun FinancesTable(
    totalSum: Double,
    categoryIdToFinances: Map<Int, List<Finance>>,
    categoriesToSumFinance: Map<Category, Double>,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
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
                        totalSum = totalSum,
                        goToFinance = goToFinance,
                        showCurrentCategoryWithFinances = false
                    )
                } else if (category.isTapped) {
                    CategoryBlock(
                        finances = financesFilteredByDate,
                        category = category,
                        categorySum = categoriesToSumFinance[category] ?: 0.0,
                        totalSum = totalSum,
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
    totalSum: Double,
    showCurrentCategoryWithFinances: Boolean,
    goToFinance: (Finance) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val percentage = String.format("%.2f", (categorySum / totalSum) * 100)

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
                        color = Color(category.colorLong ?: Color.Transparent.toArgb().toLong())
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