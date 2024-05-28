package com.example.monefy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val color: Long = 0L,
    val totalCategoryPrice: Double = 0.0,
    val isTapped: Boolean = false
)
