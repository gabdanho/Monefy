package com.example.monefy.presentation.screens.diagrams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.model.DiagramInfo
import com.example.monefy.presentation.model.FinanceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/**
 * ViewModel для экрана диаграмм.
 *
 * @property financesRepository Репозиторий, обеспечивающий доступ к данным финансов.
 */
@HiltViewModel
class DiagramsScreenViewModel @Inject constructor(
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiagramsScreenUiState())
    val uiState: StateFlow<DiagramsScreenUiState> = _uiState.asStateFlow()

    init {
        changeSelectedDiagramTabIndex(index = 0)
    }

    fun changeSelectedDiagramTabIndex(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }

        viewModelScope.launch {
            val revenues = financesRepository.getFinancesByType(FinanceType.REVENUE.tag)
            val spends = financesRepository.getFinancesByType(FinanceType.EXPENSE.tag)

            if (revenues.isEmpty() && spends.isEmpty()) {
                _uiState.update { it.copy(diagramsInfo = emptyList()) }
            } else {
                val listDates = mutableListOf<String>()
                val dateRanges = mutableListOf<LongRange>()
                val allFinances = revenues + spends

                val tabIndex = _uiState.value.selectedTabIndex

                val minDate = allFinances.minOf { LocalDate.parse(it.date) }.toEpochDay()
                val maxDate = allFinances.maxOf { LocalDate.parse(it.date) }.toEpochDay()

                when (tabIndex) {
                    // По дням
                    0 -> { dateRanges.add(minDate..maxDate) }
                    // По неделям
                    1 -> {
                        var currentDate = minDate

                        while (currentDate <= maxDate) {
                            // Находим первый и последний день недели
                            val startOfWeek = LocalDate.ofEpochDay(currentDate)
                                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            val endOfWeek = startOfWeek.plusDays(6)

                            // Добавляем рэнжу в список
                            dateRanges.add(startOfWeek.toEpochDay()..endOfWeek.toEpochDay())

                            // Делаем на +1 день, чтобы начать следующую неделю
                            currentDate = endOfWeek.plusDays(1).toEpochDay()
                        }
                    }
                    // По месяцам
                    2 -> {
                        var currentDate = minDate

                        while (currentDate <= maxDate) {
                            // Находим первый и последний день месяца
                            val startOfMonth = LocalDate.ofEpochDay(currentDate)
                                .with(TemporalAdjusters.firstDayOfMonth())
                            val endOfMonth = LocalDate.ofEpochDay(currentDate)
                                .with(TemporalAdjusters.lastDayOfMonth())

                            // Добавляем рэнжу в список
                            dateRanges.add(startOfMonth.toEpochDay()..endOfMonth.toEpochDay())

                            // Делаем на +1 день, чтобы начать следующий месяц
                            currentDate = endOfMonth.plusDays(1).toEpochDay()
                        }
                    }
                    // По годам
                    3 -> {
                        var currentDate = minDate

                        while (currentDate <= maxDate) {
                            // Находим первый и последний день года
                            val startOfYear = LocalDate.ofEpochDay(currentDate)
                                .with(TemporalAdjusters.firstDayOfYear())
                            val endOfYear = LocalDate.ofEpochDay(currentDate)
                                .with(TemporalAdjusters.lastDayOfYear())

                            // Добавляем рэнжу в список
                            dateRanges.add(startOfYear.toEpochDay()..endOfYear.toEpochDay())

                            // Делаем на +1 день, чтобы начать следующий год
                            currentDate = endOfYear.plusDays(1).toEpochDay()
                        }
                    }
                }

                val listSums = mutableListOf<Pair<Double, Double>>()
                dateRanges.forEach { range ->
                    // С днями работаем иным способом, с остальными одинаково
                    if (tabIndex != 0) {
                        val revenuesSum =
                            revenues.sumOf { if (LocalDate.parse(it.date).toEpochDay() in range) it.count * it.price else 0.0 }
                        val spendsSum =
                            spends.sumOf { if (LocalDate.parse(it.date).toEpochDay() in range) it.count * it.price else 0.0 }

                        listSums.add(Pair(revenuesSum, spendsSum))
                        // В соответствии с типом рэнжы, будет разный вывод даты
                        listDates.add(
                            when (tabIndex) {
                                1 -> LocalDate.ofEpochDay(range.last)
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yy"))

                                2 -> LocalDate.ofEpochDay(range.first)
                                    .format(DateTimeFormatter.ofPattern("MM/yy"))

                                3 -> "${LocalDate.ofEpochDay(range.first).year}"
                                else -> ""
                            }
                        )
                    } else {
                        range.forEach { day ->
                            // Находим суммы доходов и расходов
                            val revenuesSum =
                                revenues.sumOf { if (LocalDate.parse(it.date).toEpochDay() == day) it.count * it.price else 0.0 }
                            val spendsSum =
                                spends.sumOf { if (LocalDate.parse(it.date).toEpochDay() == day) it.count * it.price else 0.0 }

                            listSums.add(Pair(revenuesSum, spendsSum))
                            listDates.add(
                                LocalDate.ofEpochDay(day)
                                    .format(DateTimeFormatter.ofPattern("dd/MM"))
                            )
                        }
                    }
                }

                val formattedDiagramsInfo = listDates.zip(listSums) { date, sums ->
                    DiagramInfo(date = date, totalRevenuesToExpenses = sums)
                }

                _uiState.update { it.copy(diagramsInfo = formattedDiagramsInfo) }
            }
        }
    }
}