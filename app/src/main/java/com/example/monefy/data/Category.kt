package com.example.monefy.data

import androidx.annotation.NonNull
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monefy.model.Expense

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val color: Color,
    val spends: List<Expense>,
    val totalCategoryPrice: Double,
    val isTapped: Boolean = false
)
