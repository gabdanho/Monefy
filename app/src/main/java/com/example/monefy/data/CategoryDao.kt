package com.example.monefy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    // Создать категорию
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createCategory(category: Category)

    // Удалить категорию
    @Delete
    suspend fun deleteCategory(category: Category)


    // Обновить категорию
    @Update
    suspend fun updateCategory(category: Category)

    // Получить id-шники категорий
    @Query("SELECT id FROM categories")
    fun getCategoriesId(): Flow<List<Int>>

    // Получить категорию по id
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryById(categoryId: Int): Flow<Category>

    // Получить категории с финансами
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryWithFinances(categoryId: Int): Flow<CategoryWithFinances>

    // Получить все категории
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    // Получить категории, отсортированные по дате
    @Query("SELECT * FROM finances ORDER BY date DESC")
    fun getCategoriesByDateSortDesc(): Flow<List<Finance>>

    // Получить категории по типу
    @Query("SELECT * FROM categories WHERE type = :categoryType")
    fun getCategoriesByType(categoryType: String): Flow<List<Category>>

    // Получить доходы/расходы
    @Query("SELECT * FROM finances WHERE type = :financeType")
    fun getFinancesByType(financeType: String): Flow<List<Finance>>

    // Добавить финанс
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFinance(finance: Finance)

    // Удалить финанс
    @Delete
    suspend fun deleteFinance(finance: Finance)

    // Обновить финанс
    @Update
    suspend fun updateFinance(finance: Finance)

    // Получить все финансы
    @Query("SELECT * FROM finances")
    fun getAllFinances(): Flow<List<Finance>>
}

// Дата класс категории с финансами
data class CategoryWithFinances(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val finances: List<Finance>
)