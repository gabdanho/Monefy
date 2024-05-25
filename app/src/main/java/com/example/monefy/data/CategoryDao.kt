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
    suspend fun create(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Update
    suspend fun update(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSpend(spend: Spend)

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryWithSpends(categoryId: Int): Flow<CategoryWithSpends>
}

data class CategoryWithSpends(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val spends: List<Spend>
)