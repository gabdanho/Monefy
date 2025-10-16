package com.example.monefy.presentation.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Финанс.
 *
 * @param id Уникальный идентификатор записи.
 * @param categoryId ID категории, к которой относится финанс.
 * @param name Название транзакции.
 * @param description Описание транзакции.
 * @param date Дата в строковом формате.
 * @param price Сумма транзакции.
 * @param count Количество (для повторяющихся операций).
 * @param type Тип транзакции (доход или расход).
 * @param isRegular Флаг регулярности транзакции.
 * @param totalPrice Итоговая стоимость: [price] * [count].
 */
@Serializable
data class Finance(
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: String = LocalDate.now().toString(),
    val price: Double = 0.0,
    val count: Int = 0,
    val type: FinanceType = FinanceType.EXPENSE,
    val isRegular: Boolean = false,
    val totalPrice: Double = price * count,
)