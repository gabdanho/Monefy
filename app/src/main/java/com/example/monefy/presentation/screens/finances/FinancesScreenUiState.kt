package com.example.monefy.presentation.screens.finances

import com.example.monefy.presentation.model.Finance

/**
 * UI State для экрана финансов выбранной категории.
 *
 * @property finances Список финансовых операций (может быть null).
 */
data class FinancesScreenUiState(
    val finances: List<Finance>? = emptyList(),
)