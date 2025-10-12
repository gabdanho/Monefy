package com.example.monefy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finances")
data class Finance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val date: String = "",
    val price: Double = 0.0,
    val count: Int = 0,
    val type: String = "",
    val isRegular: Boolean = false
)