package com.example.monefy.presentation.screens.categories

import com.example.monefy.presentation.model.Category

data class CategoriesScreenUiState(
    val categories: List<Category> = emptyList(),
)