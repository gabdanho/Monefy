package com.example.monefy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.monefy.data.local.entity.Finance
import com.example.monefy.data.local.entity.Category
import com.example.monefy.data.local.model.CategoryWithFinances

/** Dao для таблицы */
@Dao
interface FinancesDao {

    /** Создаёт новую категорию. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createCategory(category: Category)

    /** Удаляет категорию. */
    @Delete
    suspend fun deleteCategory(category: Category)

    /** Обновляет категорию. */
    @Update
    suspend fun updateCategory(category: Category)

    /** Возвращает список ID всех категорий. */
    @Query("SELECT id FROM categories")
    suspend fun getCategoriesId(): List<Int>

    /** Возвращает категорию по её ID. */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): Category

    /** Возвращает финанс по его ID. */
    @Query("SELECT * FROM finances WHERE id = :financeId")
    suspend fun getFinanceById(financeId: Int): Finance

    /** Возвращает категорию с её финансами. */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryWithFinances(categoryId: Int): CategoryWithFinances?

    /** Возвращает финансы, отсортированные по дате (по убыванию). */
    @Query("SELECT * FROM finances ORDER BY date DESC")
    suspend fun getFinancesByDateSortDesc(): List<Finance>

    /** Возвращает все категории. */
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    /** Возвращает категории по их типу. */
    @Query("SELECT * FROM categories WHERE type = :categoryType")
    suspend fun getCategoriesByType(categoryType: String): List<Category>

    /** Возвращает финансы по типу (доход/расход). */
    @Query("SELECT * FROM finances WHERE type = :financeType")
    suspend fun getFinancesByType(financeType: String): List<Finance>

    /** Добавляет новый финанс. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFinance(finance: Finance)

    /** Удаляет финанс. */
    @Delete
    suspend fun deleteFinance(finance: Finance)

    /** Обновляет финанс. */
    @Update
    suspend fun updateFinance(finance: Finance)

    /** Возвращает все финансы. */
    @Query("SELECT * FROM finances")
    suspend fun getAllFinances(): List<Finance>
}