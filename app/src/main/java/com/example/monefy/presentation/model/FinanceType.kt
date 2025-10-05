package com.example.monefy.presentation.model

const val EXPENSE_TAG = "EXPENSE"
const val REVENUE_TAG = "REVENUE"

enum class FinanceType(val tag: String) {
    EXPENSE(EXPENSE_TAG),
    REVENUE(REVENUE_TAG);

    companion object {
        fun fromTag(value: String): FinanceType {
            return when (value) {
                EXPENSE_TAG -> EXPENSE
                REVENUE_TAG -> REVENUE
                else -> throw IllegalStateException("Unknown FinanceType")
            }
        }
    }
}