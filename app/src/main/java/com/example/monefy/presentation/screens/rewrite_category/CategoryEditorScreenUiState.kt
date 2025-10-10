package com.example.monefy.presentation.screens.rewrite_category

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.StringResName

data class CategoryEditorScreenUiState(
    val categoryName: String = "",
    val colorCategory: Long? = null,
    val selectedFinanceType: FinanceType = FinanceType.EXPENSE,
    val selectedCategory: Category = Category(),

    val isShowColorPicker: Boolean = false,

    val textColorCategoryName: Long = 0L,
    val textColorCategoryColor: Long = 0L,

    val isCategoryNameError: Boolean = false,
    val isCategoryColorError: Boolean = false,

    val messageResName: StringResName? = null,
)