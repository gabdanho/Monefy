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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpend(spend: Spend)

    @Update
    suspend fun updateSpend(spend: Spend)

    @Delete
    suspend fun deleteSpend(spend: Spend)

    @Query("SELECT COUNT(*) FROM categories")
    fun getCountCategories(): Int

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryWithSpends(categoryId: Int): Flow<CategoryWithSpends>

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>
}

data class CategoryWithSpends(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val spends: List<Spend>
)