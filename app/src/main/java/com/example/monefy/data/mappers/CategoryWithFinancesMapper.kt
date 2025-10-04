package com.example.monefy.data.mappers

import com.example.monefy.data.local.model.CategoryWithFinances
import com.example.monefy.domain.model.CategoryWithFinances as CategoryWithFinancesDomain

fun CategoryWithFinancesDomain.toDataLayer(): CategoryWithFinances {
    return CategoryWithFinances(
        category = category.toDataLayer(),
        finances = finances.map { it.toDataLayer() }
    )
}

fun CategoryWithFinances.toDomainLayer(): CategoryWithFinancesDomain {
    return CategoryWithFinancesDomain(
        category = category.toDomainLayer(),
        finances = finances.map { it.toDomainLayer() }
    )
}