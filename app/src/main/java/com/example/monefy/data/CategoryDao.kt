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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Query("SELECT id FROM categories")
    fun getCategoriesId(): Flow<List<Int>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryById(categoryId: Int): Flow<Category>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryWithFinances(categoryId: Int): Flow<CategoryWithFinances>

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :categoryType")
    fun getCategoriesByType(categoryType: String): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFinance(finance: Finance)

    @Delete
    suspend fun deleteFinance(finance: Finance)

    @Update
    suspend fun updateFinance(finance: Finance)
}

data class CategoryWithFinances(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val finances: List<Finance>
)