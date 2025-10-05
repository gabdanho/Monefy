package com.example.monefy.presentation.mappers

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.domain.model.Category as CategoryDomain

fun CategoryDomain.toPresentationLayer(): Category {
    return Category(
        id = id,
        name = name,
        colorLong = colorLong,
        totalCategoryPrice = totalCategoryPrice,
        isTapped = isTapped,
        type = FinanceType.fromTag(type),
    )
}

fun Category.toDomainLayer(): CategoryDomain {
    return CategoryDomain(
        id = id,
        name = name,
        colorLong = colorLong,
        totalCategoryPrice = totalCategoryPrice,
        isTapped = isTapped,
        type = type.tag,
    )
}