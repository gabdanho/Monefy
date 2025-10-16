package com.example.monefy.presentation.screens.main_monefy

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import java.time.LocalDate

/**
 * UI State для главного экрана приложения Monefy.
 *
 * Хранит выбранные вкладки, фильтры по дате, состояние загрузки,
 * информацию о категориях и суммарных данных по финансам.
 *
 * @property selectedFinanceTypeTabIndex Индекс выбранной вкладки доход/расход.
 * @property selectedDateRangeIndex Индекс выбранного диапазона дат.
 * @property isHasFinances Флаг наличия финансовых операций.
 * @property isAllNotTapped Флаг, что ни одна категория не выбрана.
 * @property isLoading Флаг состояния загрузки данных.
 * @property isShowDateRangeDialog Флаг показа диалогового окна выбора диапазона дат.
 * @property categoriesToSumFinance Карта категорий с суммами финансов по каждой категории.
 * @property categoryIdToFinances Карта id категории к списку финансов.
 * @property dateRange Список дат для фильтрации.
 * @property selectedCustomDateRange Пользовательский выбранный диапазон дат.
 * @property totalSum Общая сумма финансов.
 * @property currentCategorySumPrice Сумма финансов для текущей выбранной категории.
 */
data class MainMonefyScreenUiState(
    val selectedFinanceTypeTabIndex: Int = 0,
    val selectedDateRangeIndex: Int = 2,

    val isHasFinances: Boolean = false,
    val isAllNotTapped: Boolean = true,

    val isLoading: Boolean = false,
    val isShowDateRangeDialog: Boolean = false,

    val categoriesToSumFinance: Map<Category, Double> = emptyMap(),
    val categoryIdToFinances: Map<Int, List<Finance>> = emptyMap(),
    val dateRange: List<LocalDate> = emptyList(),
    val selectedCustomDateRange: List<LocalDate> = emptyList(),

    val totalSum: Double = 0.0,
    val currentCategorySumPrice: Double = 0.0,
)