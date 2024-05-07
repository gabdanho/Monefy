package com.example.monefy.model

import java.time.LocalDate

data class Expense(
    val categoryName: String,
    val name: String,
    val description: String = "",
    val date: LocalDate,
    val price: Double,
    val count: Int,
    val totalPrice: Double = price * count.toDouble()
)
