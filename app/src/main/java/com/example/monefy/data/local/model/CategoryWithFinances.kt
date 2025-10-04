package com.example.monefy.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.monefy.data.local.entity.Category
import com.example.monefy.data.local.entity.Finance

data class CategoryWithFinances(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val finances: List<Finance>
)