package com.example.monefy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spends")
data class Spend(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val categoryId: Int,
    val name: String,
    val description: String,
    val date: Int,
    val price: Double,
    val count: Int
)
