package com.example.monefy.presentation.screens.category_editor

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.StringResName

/**
 * UI-состояние экрана редактирования категории.
 *
 * @property categoryName Введённое имя категории.
 * @property colorCategory Выбранный цвет категории.
 * @property selectedFinanceType Тип категории (доход/расход).
 * @property selectedCategory Категория, которую редактируют.
 * @property isShowColorPicker Показывать ли выбор цвета.
 * @property textColorCategoryName Цвет текста для имени категории.
 * @property textColorCategoryColor Цвет текста для цвета категории.
 * @property isCategoryNameError Флаг ошибки ввода имени категории.
 * @property isCategoryColorError Флаг ошибки выбора цвета категории.
 * @property messageResName Ресурс сообщения об ошибке, если есть.
 */
data class CategoryEditorScreenUiState(
    val categoryName: String = "",
    val colorCategory: Long? = null,
    val selectedFinanceType: FinanceType = FinanceType.EXPENSE,
    val selectedCategory: Category = Category(),

    val isShowColorPicker: Boolean = false,

    val textColorCategoryName: Long = 0xFFFFFFFF,
    val textColorCategoryColor: Long = 0xFFFFFFFF,

    val isCategoryNameError: Boolean = false,
    val isCategoryColorError: Boolean = false,

    val messageResName: StringResName? = null,
)