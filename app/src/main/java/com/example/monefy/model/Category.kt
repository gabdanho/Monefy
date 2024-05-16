package com.example.monefy.model

import androidx.compose.ui.graphics.Color

data class Category(
    val name: String,
    val color: Color,
    val expenses: MutableList<Expense> = mutableListOf(),
    val totalCategoryPrice: Double = if (expenses.isNotEmpty()) expenses.sumOf { it.totalPrice } else 0.0,
    val isTapped: Boolean = false
)

val addCategory = listOf(
    Category(
        name = "Добавить категорию (+)",
        color = Color.White
    )
)