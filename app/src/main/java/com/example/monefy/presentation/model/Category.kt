package com.example.monefy.presentation.model

data class Category(
    val id: Int = 0,
    val name: String = "",
    val colorLong: Long = 0L,
    val totalCategoryPrice: Double = 0.0,
    val isTapped: Boolean = false,
    val type: FinanceType = FinanceType.EXPENSE,
)