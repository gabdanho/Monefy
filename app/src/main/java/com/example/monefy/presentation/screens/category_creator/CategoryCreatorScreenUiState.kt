package com.example.monefy.presentation.screens.category_creator

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.StringResName

data class CategoryCreatorScreenUiState(
    val categoryName: String = "",
    val colorCategory: Long? = null,
    val selectedFinanceType: FinanceType = FinanceType.EXPENSE,

    val isShowColorPicker: Boolean = false,

    val textColorCategoryName: Long = Color.White.toColorLong(),
    val textColorCategoryColor: Long = Color.White.toColorLong(),

    val isCategoryNameError: Boolean = false,
    val isCategoryColorError: Boolean = false,

    val messageResName: StringResName? = null,
)