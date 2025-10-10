package com.example.monefy.presentation.screens.finances

import com.example.monefy.presentation.model.Finance

data class FinancesScreenUiState(
    val finances: List<Finance> = emptyList(),
    val categoryId: Int = 0,
)