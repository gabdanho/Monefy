package com.example.monefy.model

data class Expense(
    val categoryName: String,
    val name: String,
    val price: Double,
    val count: Int,
    val sumPrice: Double = price * count.toDouble()
)
