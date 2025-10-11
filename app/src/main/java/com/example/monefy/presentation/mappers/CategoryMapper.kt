package com.example.monefy.presentation.mappers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.domain.model.Category as CategoryDomain

fun CategoryDomain.toPresentationLayer(): Category {
    return Category(
        id = id,
        name = name,
        colorLong = colorLong,
        totalCategoryPrice = totalCategoryPrice,
        type = FinanceType.fromTag(type),
    )
}

fun Category.toDomainLayer(): CategoryDomain {
    return CategoryDomain(
        id = id,
        name = name,
        colorLong = colorLong ?: Color.Transparent.toColorLong(),
        totalCategoryPrice = totalCategoryPrice,
        type = type.tag,
    )
}