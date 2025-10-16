package com.example.monefy.presentation.screens.categories

import com.example.monefy.presentation.model.Category

/**
 * UI-состояние экрана категорий.
 *
 * @property categories Список категорий для отображения на экране.
 */
data class CategoriesScreenUiState(
    val categories: List<Category> = emptyList(),
)