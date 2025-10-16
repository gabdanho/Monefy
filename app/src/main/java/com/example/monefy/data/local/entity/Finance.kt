package com.example.monefy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность финанс (таблица `finances`).
 *
 * @param id Уникальный идентификатор записи.
 * @param categoryId ID категории, к которой относится финанс.
 * @param name Название транзакции.
 * @param description Описание транзакции.
 * @param date Дата в строковом формате (например, "2025-10-16").
 * @param price Сумма транзакции.
 * @param count Количество (для повторяющихся операций).
 * @param type Тип транзакции (доход или расход).
 * @param isRegular Флаг регулярности транзакции.
 */
@Entity(tableName = "finances")
data class Finance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val price: Double = 0.0,
    val count: Int = 0,
    val type: String = "",
    val isRegular: Boolean = false
)