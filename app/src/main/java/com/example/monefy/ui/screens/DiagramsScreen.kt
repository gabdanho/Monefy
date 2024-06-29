package com.example.monefy.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monefy.data.Category
import com.example.monefy.data.Finance
import com.example.monefy.model.FakeData
import com.example.monefy.utils.Constants
import com.example.monefy.utils.CustomDateRangePicker
import com.example.monefy.utils.isDateInRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

/*
* Сделать диаграммы по месяцам, по годам и т.д.
* В соответствии с пунктом выше, получить мапу с датой и соответсвующими доходами/расходами за этот период
* Выводим минимум 5 лет, месяцев и т.д. (если есть за предыдущие до вплоть до них)
* Строим рядом с этими диаграммами диаграммы с прибылью, убытками
* Снизу, под диаграммами, будут подписаны даты. Вверху суммы.
* По скольку цвета будут статичными, то делаем легенду об этих цветах
*/

@Composable
fun DiagramScreen(
    financesViewModel: FinancesViewModel,
    updateScreen: () -> Unit
) {
    val uiState by financesViewModel.uiState.collectAsState()

    DiagramsTest(
        customDateRange = uiState.customDateRange,
        showDateRangeDialog = uiState.showDateRangeDialog,
        selectedTabIndex = uiState.selectedTabIndex,
        selectedDateRangeIndex = uiState.selectedDateRangeIndex,
        changeShowDateRangeDialog = financesViewModel::changeShowDateRangeDialog,
        updateCustomDateRange = financesViewModel::updateCustomDateRange,
        changeSelectedTabIndex = financesViewModel::changeSelectedTabIndex,
        changeSelectedDateRangeIndex = financesViewModel::changeSelectedDateRangeIndex,
        getCategoriesByType = financesViewModel::getCategoriesByType,
        getFinancesByCategoryId = financesViewModel::getFinancesByCategoryId,
        updateScreen = updateScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagramsTest(
    customDateRange: List<LocalDate>,
    showDateRangeDialog: Boolean,
    selectedTabIndex: Int,
    selectedDateRangeIndex: Int,
    modifier: Modifier = Modifier,
    changeShowDateRangeDialog: (Boolean) -> Unit,
    updateCustomDateRange: (List<LocalDate>) -> Unit,
    changeSelectedTabIndex: (Int) -> Unit,
    changeSelectedDateRangeIndex: (Int) -> Unit,
    getCategoriesByType: (String) -> Flow<List<Category>>,
    getFinancesByCategoryId: (Int) -> Flow<List<Finance>>,
    updateScreen: () -> Unit
) {
    var isHasFinances by remember { mutableStateOf(false) }

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
            modifier = Modifier.padding(innerPadding)
        ) {
            // Выбор типа финансов: доходы или расходы
            TabRow(selectedTabIndex = selectedTabIndex) {
                Constants.tabItems.forEachIndexed { index, item ->
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
            // Если финансы есть, позволяем пользователю выбрать нужный промежуток даты
            if (isHasFinances) {
                TabRow(
                    selectedTabIndex = selectedDateRangeIndex,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Constants.tabDateRangeItems.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectedDateRangeIndex,
                            onClick = {
                                // Если выбран таб кастомной даты, то выскакивает выбор даты
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
                
                if (isLoadingFinances) { /* Ничего не делаем */ }
                // Если финансов доходов/расходов нет - то выводим сообщение
                else if (financesMap.isNullOrEmpty()) {
                    Text(text = "Нет данных для отображения")
                }
                // Если всё есть, готовим данные для основных функций (pie chart и список финансов)
                else {
                    val finances = financesMap!!.values.flatten()
                    // Мапа с категориями и тотал суммой их
                    val categoriesToSumFinance = categories!!.filter { it.id in financesMap!!.keys }.associate { category ->
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
                        Diagram(categoriesToSumFinance)
                    }
                }
            }
        }
    }
}

@Composable
fun Diagram(
    categoriesToSumFinance: Map<Category, Double>,
) {
    var startLine: Float
    val maxSum = categoriesToSumFinance.values.max()
    var maxHeightLine: Float
    val strokeWidth = 80f
    val padding = strokeWidth / 3
    val canvasSizeWidth = ((strokeWidth + padding) * categoriesToSumFinance.values.size - padding).dp
    val canvasSizeHeight = 300.dp

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .size(canvasSizeWidth, canvasSizeHeight)
                .padding(2.dp)
        ) {
            maxHeightLine = size.height
            startLine = strokeWidth / 2

            // Обрабатываем суммы категорий
            categoriesToSumFinance.forEach { category ->
                val sumPercent = category.value / maxSum

                drawLine(
                    start = Offset(x = startLine, y = size.height),
                    end = Offset(x = startLine, y = (size.height - (sumPercent * maxHeightLine)).toFloat()),
                    color = Color(category.key.color),
                    strokeWidth = strokeWidth
                )

                startLine += strokeWidth + padding
            }
        }
    }
}