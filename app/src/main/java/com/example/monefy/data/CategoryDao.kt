package com.example.monefy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(category: Category)

    @Delete
    suspend fun delete(id: Int)

    @Update
    suspend fun addSpend(spend: Spend)
}