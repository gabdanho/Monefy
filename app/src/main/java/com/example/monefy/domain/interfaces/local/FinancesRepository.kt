package com.example.monefy.domain.interfaces.local

import com.example.monefy.domain.model.Category
import com.example.monefy.domain.model.CategoryWithFinances
import com.example.monefy.domain.model.Finance
import kotlinx.coroutines.flow.Flow

interface FinancesRepository {

    suspend fun createCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun updateCategory(category: Category)

    suspend fun getCategoriesId(): Flow<List<Int>>

    suspend fun getCategoryById(categoryId: Int): Flow<Category>

    suspend fun getCategoryWithFinances(categoryId: Int): Flow<CategoryWithFinances>

    suspend fun getCategoriesByDateSortDesc(): Flow<List<Finance>>

    suspend fun getAllCategories(): Flow<List<Category>>

    suspend fun getCategoriesByType(categoryType: String): Flow<List<Category>>

    suspend fun getFinancesByType(financeType: String): Flow<List<Finance>>

    suspend fun addFinance(finance: Finance)

    suspend fun deleteFinance(finance: Finance)

    suspend fun updateFinance(finance: Finance)

    suspend fun getAllFinances(): Flow<List<Finance>>
}