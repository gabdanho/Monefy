package com.example.monefy.utils

import com.example.monefy.model.Category


fun isAllCategoriesEmpty(categories: List<Category>): Boolean {
    for (category in categories) {
        if (category.expenses.isNotEmpty()) return false
    }
    return true
}