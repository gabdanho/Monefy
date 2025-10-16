package com.example.monefy.presentation.model

/**
 * Информация для диаграммы, отображающей соотношение доходов и расходов за определённую дату.
 *
 * @param date Дата, к которой относится информация (в формате строки).
 * @param totalRevenuesToExpenses Пара значений:
 *  - first — общая сумма доходов,
 *  - second — общая сумма расходов.
 */
data class DiagramInfo(
    val date: String = "",
    val totalRevenuesToExpenses: Pair<Double, Double>,
)