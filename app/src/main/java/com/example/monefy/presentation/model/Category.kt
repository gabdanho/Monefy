package com.example.monefy.presentation.model

import kotlinx.serialization.Serializable

/**
 * Категория.
 *
 * @param id Уникальный идентификатор категории.
 * @param name Название категории.
 * @param colorLong Цвет категории в формате Long.
 * @param totalCategoryPrice Общая сумма по категории.
 * @param type Тип категории (доход или расход).
 * @param isTapped Выбрана ли категория на PieChart'е.
 */
@Serializable
data class Category(
    val id: Int = 0,
    val name: String = "",
    val colorLong: Long? = 0L,
    val totalCategoryPrice: Double = 0.0,
    val isTapped: Boolean = false,
    val type: FinanceType = FinanceType.EXPENSE,
)

val CREATION_CATEGORY = Category(
    id = -1,
    name = "+",
    colorLong = null
)