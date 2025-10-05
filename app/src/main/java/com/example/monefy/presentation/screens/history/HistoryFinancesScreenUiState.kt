package com.example.monefy.presentation.screens.history

import com.example.monefy.presentation.model.Finance

data class HistoryFinancesScreenUiState(
    val finances: List<Finance> = emptyList(),
)