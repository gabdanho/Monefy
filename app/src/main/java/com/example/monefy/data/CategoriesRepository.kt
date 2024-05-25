package com.example.monefy.data

interface CategoriesRepository {
    suspend fun createCategory(category: Category)

    suspend fun deleteCategory(id: Int)

    suspend fun addSpendToCategory(spend: Spend)
}