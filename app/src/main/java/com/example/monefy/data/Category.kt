package com.example.monefy.data

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val color: Int = Color.argb(0, 0, 0, 0),
    val totalCategoryPrice: Double = 0.0,
    val isTapped: Boolean = false
)
