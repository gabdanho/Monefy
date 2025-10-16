package com.example.monefy.presentation.screens.category_creator

import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.StringResName

/**
 * UI-состояние экрана создания категории.
 *
 * @property categoryName Введённое пользователем имя категории.
 * @property colorCategory Выбранный цвет категории в формате Long.
 * @property selectedFinanceType Тип категории (доход/расход).
 * @property isShowColorPicker Показывать ли окно выбора цвета.
 * @property textColorCategoryName Цвет текста для имени категории.
 * @property textColorCategoryColor Цвет текста для отображения цвета категории.
 * @property isCategoryNameError Флаг ошибки ввода имени категории.
 * @property isCategoryColorError Флаг ошибки выбора цвета категории.
 * @property messageResName Ресурс сообщения об ошибке, если есть.
 */
data class CategoryCreatorScreenUiState(
    val categoryName: String = "",
    val colorCategory: Long? = null,
    val selectedFinanceType: FinanceType = FinanceType.EXPENSE,

    val isShowColorPicker: Boolean = false,

    val textColorCategoryName: Long = 0xFFFFFFFF,
    val textColorCategoryColor: Long = 0xFFFFFFFF,

    val isCategoryNameError: Boolean = false,
    val isCategoryColorError: Boolean = false,

    val messageResName: StringResName? = null,
)