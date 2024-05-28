package com.example.monefy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spends")
data class Spend(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: Long = 0L,
    val price: Double = 0.0,
    val count: Int = 0
)
