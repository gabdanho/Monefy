package com.example.monefy.presentation.model

/**
 * Категория с привязанными финансами.
 *
 * @param category Категория.
 * @param finances Список финансов, связанных с категорией.
 */
data class CategoryWithFinances(
    val category: Category,
    val finances: List<Finance>,
)