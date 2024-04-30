package com.example.monefy.model

import androidx.compose.ui.graphics.Color

data class Category(
    val name: String,
    val color: Color,
    val expenses: List<Expense>,
    val totalCategoryPrice: Double = expenses.sumOf { it.sumPrice },
    val isTapped: Boolean = false
)