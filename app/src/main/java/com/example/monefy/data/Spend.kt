package com.example.monefy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "spends")
data class Spend(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val categoryName: String,
    val name: String,
    val description: String,
    val date: LocalDate,
    val price: Double,
    val count: Int
)