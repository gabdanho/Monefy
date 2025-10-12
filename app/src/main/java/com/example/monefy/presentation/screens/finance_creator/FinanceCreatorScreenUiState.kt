package com.example.monefy.presentation.screens.finance_creator

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.StringResName
import java.time.LocalDate

data class FinanceCreatorScreenUiState(
    val isCategoryNotSelected: Boolean = false,
    val isFinanceNameNotFilled: Boolean = false,
    val isPriceEqualsZero: Boolean = false,
    val isShowDateDialog: Boolean = false,

    val textColorFinanceName: Long = Color.White.toColorLong(),
    val textColorCategory: Long = Color.White.toColorLong(),
    val textColorFinancePrice: Long = Color.White.toColorLong(),

    val selectedCategoryId: Int = 0,

    val pickedDate: LocalDate = LocalDate.now(),
    val financeName: String = "",
    val financeDescription: String = "",
    val price: String = "0",
    val count: String = "1",
    val isRegular: Boolean = false,
    val categories: List<Category> = emptyList(),

    val messageResName: StringResName? = null,
)