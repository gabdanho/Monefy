package com.example.monefy.presentation.mappers

import com.example.monefy.presentation.model.CategoryWithFinances
import com.example.monefy.domain.model.CategoryWithFinances as CategoryWithFinancesDomain

/**
 * Преобразует [CategoryWithFinancesDomain] → [CategoryWithFinances].
 */
fun CategoryWithFinancesDomain.toPresentationLayer(): CategoryWithFinances {
    return CategoryWithFinances(
        category = category.toPresentationLayer(),
        finances = finances.map { it.toPresentationLayer() }
    )
}

/**
 * Преобразует [CategoryWithFinances] → [CategoryWithFinancesDomain].
 */
fun CategoryWithFinances.toDomainLayer(): CategoryWithFinancesDomain {
    return CategoryWithFinancesDomain(
        category = category.toDomainLayer(),
        finances = finances.map { it.toDomainLayer() }
    )
}