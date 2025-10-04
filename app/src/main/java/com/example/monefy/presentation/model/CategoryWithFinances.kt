package com.example.monefy.presentation.model

data class CategoryWithFinances(
    val category: Category,
    val finances: List<Finance>,
)