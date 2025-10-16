package com.example.monefy.presentation.screens.main_monefy

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import java.time.LocalDate

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