package com.example.monefy.presentation.screens.create_category

import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.StringResName

data class AddCategoryScreenUiState(
    val categoryName: String = "",
    val colorCategory: Long? = null,
    val selectedFinanceType: FinanceType = FinanceType.EXPENSE,

    val isShowColorPicker: Boolean = false,

    val textColorCategoryName: Long = 0L,
    val textColorCategoryColor: Long = 0L,

    val isCategoryNameError: Boolean = false,
    val isCategoryColorError: Boolean = false,

    val messageResName: StringResName? = null,

    val isShowSnackBar: Boolean = false,
)