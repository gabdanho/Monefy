package com.example.monefy.presentation.model

import java.time.LocalDate

data class Finance(
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val price: Double = 0.0,
    val count: Int = 0,
    val type: FinanceType = FinanceType.EXPENSE,
    val isRegular: Boolean = false,
    val totalPrice: Double = price * count,
)