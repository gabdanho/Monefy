package com.example.monefy.presentation.screens.history

import com.example.monefy.presentation.model.Finance

/**
 * UI State для экрана истории финансовых операций.
 *
 * @property finances Список финансовых операций.
 */
data class HistoryFinancesScreenUiState(
    val finances: List<Finance> = emptyList(),
)