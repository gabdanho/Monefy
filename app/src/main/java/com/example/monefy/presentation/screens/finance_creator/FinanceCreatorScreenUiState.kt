package com.example.monefy.presentation.screens.finance_creator

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.StringResName
import java.time.LocalDate

/**
 * UI-состояние экрана создания финансовой операции.
 *
 * @property isCategoryNotSelected Флаг ошибки, если категория не выбрана.
 * @property isFinanceNameNotFilled Флаг ошибки, если имя операции не заполнено.
 * @property isPriceEqualsZero Флаг ошибки, если сумма равна нулю.
 * @property isShowDateDialog Показывать ли диалог выбора даты.
 * @property textColorFinanceName Цвет текста имени операции.
 * @property textColorCategory Цвет текста выбранной категории.
 * @property textColorFinancePrice Цвет текста суммы операции.
 * @property selectedCategoryId Id выбранной категории.
 * @property pickedDate Выбранная дата операции.
 * @property financeName Введённое имя операции.
 * @property financeDescription Описание операции.
 * @property price Сумма операции.
 * @property count Количество единиц операции.
 * @property isRegular Флаг регулярной операции.
 * @property categories Список доступных категорий.
 * @property messageResName Ресурс сообщения об ошибке, если есть.
 */
data class FinanceCreatorScreenUiState(
    val isCategoryNotSelected: Boolean = false,
    val isFinanceNameNotFilled: Boolean = false,
    val isPriceEqualsZero: Boolean = false,
    val isShowDateDialog: Boolean = false,

    val textColorFinanceName: Long = 0xFFFFFFFF,
    val textColorCategory: Long = 0xFFFFFFFF,
    val textColorFinancePrice: Long = 0xFFFFFFFF,

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