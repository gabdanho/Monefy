package com.example.monefy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность категории (таблица `categories`).
 *
 * @param id Уникальный идентификатор категории.
 * @param name Название категории.
 * @param colorLong Цвет категории в формате Long.
 * @param totalCategoryPrice Общая сумма по категории.
 * @param type Тип категории (доход или расход).
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val colorLong: Long = 0L,
    val totalCategoryPrice: Double = 0.0,
    val type: String = ""
)