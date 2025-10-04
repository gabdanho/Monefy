package com.example.monefy.domain.model

data class CategoryWithFinances(
    val category: Category,
    val finances: List<Finance>,
)