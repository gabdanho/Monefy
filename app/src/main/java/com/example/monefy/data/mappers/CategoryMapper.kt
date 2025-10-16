package com.example.monefy.data.mappers

import com.example.monefy.data.local.entity.Category
import com.example.monefy.domain.model.Category as CategoryDomain

/**
 * Преобразует [CategoryDomain] → [Category].
 */
fun CategoryDomain.toDataLayer(): Category {
    return Category(
        id = id,
        name = name,
        colorLong = colorLong,
        totalCategoryPrice = totalCategoryPrice,
        type = type,
    )
}

/**
 * Преобразует [Category] → [CategoryDomain].
 */
fun Category.toDomainLayer(): CategoryDomain {
    return CategoryDomain(
        id = id,
        name = name,
        colorLong = colorLong,
        totalCategoryPrice = totalCategoryPrice,
        type = type,
    )
}