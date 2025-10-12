package com.example.monefy.domain.model

data class Finance(
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val price: Double = 0.0,
    val count: Int = 0,
    val type: String = "",
    val isRegular: Boolean = false,
)