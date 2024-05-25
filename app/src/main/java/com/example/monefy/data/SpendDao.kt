package com.example.monefy.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface SpendDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(spend: Spend)

    @Delete
    suspend fun delete(id: Int)

    @Update
    suspend fun update(spend: Spend)
}