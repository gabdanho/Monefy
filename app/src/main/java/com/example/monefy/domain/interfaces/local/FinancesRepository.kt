package com.example.monefy.domain.interfaces.local

import com.example.monefy.domain.model.Category
import com.example.monefy.domain.model.CategoryWithFinances
import com.example.monefy.domain.model.Finance

interface FinancesRepository {

    suspend fun createCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun updateCategory(category: Category)

    suspend fun getCategoriesId(): List<Int>

    suspend fun getCategoryById(categoryId: Int): Category

    suspend fun getCategoryWithFinances(categoryId: Int): CategoryWithFinances

    suspend fun getFinancesByDateSortDesc(): List<Finance>

    suspend fun getAllCategories(): List<Category>

    suspend fun getCategoriesByType(categoryType: String): List<Category>

    suspend fun getFinancesByType(financeType: String): List<Finance>

    suspend fun addFinance(finance: Finance)

    suspend fun deleteFinance(finance: Finance)

    suspend fun updateFinance(finance: Finance)

    suspend fun getAllFinances(): List<Finance>
}