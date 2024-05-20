package com.example.monefy.model

import java.time.LocalDate

data class Expense(
    val categoryName: String = "",
    val name: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val price: Double = 0.0,
    val count: Int = 0,
    val totalPrice: Double = price * count.toDouble()
)
