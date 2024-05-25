package com.example.monefy.data

import androidx.annotation.NonNull
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @NonNull
    val name: String,
    val color: Color,
    val spends: List<Spend>,
    val totalCategoryPrice: Double,
    val isTapped: Boolean = false
)
