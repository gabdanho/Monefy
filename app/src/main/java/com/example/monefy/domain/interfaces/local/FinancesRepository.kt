package com.example.monefy.domain.interfaces.local

import com.example.monefy.domain.model.Category
import com.example.monefy.domain.model.CategoryWithFinances
import com.example.monefy.domain.model.Finance

/**
 * Интерфейс репозитория для управления категориями и финансами.
 */
interface FinancesRepository {

    /** Создаёт категорию. */
    suspend fun createCategory(category: Category)

    /** Удаляет категорию. */
    suspend fun deleteCategory(category: Category)

    /** Обновляет категорию. */
    suspend fun updateCategory(category: Category)

    /** Возвращает список ID всех категорий. */
    suspend fun getCategoriesId(): List<Int>

    /** Возвращает категорию по ID. */
    suspend fun getCategoryById(categoryId: Int): Category

    /** Возвращает категорию с её финансами. */
    suspend fun getCategoryWithFinances(categoryId: Int): CategoryWithFinances?

    /** Возвращает финансы, отсортированные по дате (по убыванию). */
    suspend fun getFinancesByDateSortDesc(): List<Finance>

    /** Возвращает все категории. */
    suspend fun getAllCategories(): List<Category>

    /** Возвращает категории по типу. */
    suspend fun getCategoriesByType(categoryType: String): List<Category>

    /** Возвращает финансы по типу. */
    suspend fun getFinancesByType(financeType: String): List<Finance>

    /** Добавляет финанс. */
    suspend fun addFinance(finance: Finance)

    /** Удаляет финанс. */
    suspend fun deleteFinance(finance: Finance)

    /** Обновляет финанс. */
    suspend fun updateFinance(newFinance: Finance)

    /** Возвращает все финансы. */
    suspend fun getAllFinances(): List<Finance>
}