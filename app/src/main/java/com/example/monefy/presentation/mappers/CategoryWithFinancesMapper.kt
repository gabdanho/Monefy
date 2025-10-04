package com.example.monefy.presentation.mappers

import com.example.monefy.presentation.model.CategoryWithFinances
import com.example.monefy.domain.model.CategoryWithFinances as CategoryWithFinancesDomain

fun CategoryWithFinancesDomain.toPresentationLayer(): CategoryWithFinances {
    return CategoryWithFinances(
        category = category.toPresentationLayer(),
        finances = finances.map { it.toPresentationLayer() }
    )
}

fun CategoryWithFinances.toDomainLayer(): CategoryWithFinancesDomain {
    return CategoryWithFinancesDomain(
        category = category.toDomainLayer(),
        finances = finances.map { it.toDomainLayer() }
    )
}