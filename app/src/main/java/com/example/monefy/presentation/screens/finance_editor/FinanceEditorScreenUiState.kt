package com.example.monefy.presentation.screens.finance_editor

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.StringResName
import java.time.LocalDate

data class FinanceEditorScreenUiState(
    val isCategoryNotSelected: Boolean = false,
    val isFinanceNameNotFilled: Boolean = false,
    val isShowDateDialog: Boolean = false,

    val textColorFinanceName: Long = 0L,
    val textColorCategory: Long = 0L,

    val selectedCategoryId: Int = 0,
    val pickedDate: LocalDate = LocalDate.now(),
    val financeName: String = "",
    val financeDescription: String = "",
    val price: Double = 0.0,
    val count: Int = 1,
    val isRegular: Boolean = false,
    val selectedFinance: Finance = Finance(),

    val categories: List<Category> = emptyList(),

    val messageResName: StringResName? = null,
)