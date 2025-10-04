package com.example.monefy.data.mappers

import com.example.monefy.data.local.entity.Finance
import com.example.monefy.domain.model.Finance as FinanceDomain

fun FinanceDomain.toDataLayer(): Finance {
    return Finance(
        id = id,
        categoryId = categoryId,
        name = name,
        description = description,
        date = date,
        price = price,
        count = count,
        type = type,
        isRegular = isRegular
    )
}

fun Finance.toDomainLayer(): FinanceDomain {
    return FinanceDomain(
        id = id,
        categoryId = categoryId,
        name = name,
        description = description,
        date = date,
        price = price,
        count = count,
        type = type,
        isRegular = isRegular
    )
}