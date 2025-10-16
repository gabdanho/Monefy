package com.example.monefy.domain.model

/**
 * Категория.
 *
 * @param id Уникальный идентификатор категории.
 * @param name Название категории.
 * @param colorLong Цвет категории в формате Long.
 * @param totalCategoryPrice Общая сумма по категории.
 * @param type Тип категории (доход или расход).
 */
data class Category(
    val id: Int = 0,
    val name: String = "",
    val colorLong: Long = 0L,
    val totalCategoryPrice: Double = 0.0,
    val type: String = "",
)