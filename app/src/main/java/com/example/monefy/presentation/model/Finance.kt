package com.example.monefy.presentation.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Finance(
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: String = LocalDate.now().toString(),
    val price: Double = 0.0,
    val count: Int = 0,
    val type: FinanceType = FinanceType.EXPENSE,
    val isRegular: Boolean = false,
    val totalPrice: Double = price * count,
)