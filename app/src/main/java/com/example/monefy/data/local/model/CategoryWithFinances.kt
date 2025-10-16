package com.example.monefy.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.monefy.data.local.entity.Category
import com.example.monefy.data.local.entity.Finance

/**
 * Категория с привязанными финансами.
 *
 * @param category Категория.
 * @param finances Список финансов, связанных с категорией.
 */
data class CategoryWithFinances(
    @Embedded val category: Category = Category(),
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val finances: List<Finance> = emptyList()
)