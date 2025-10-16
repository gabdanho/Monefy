package com.example.monefy.presentation.screens.finance_editor

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.StringResName
import java.time.LocalDate

/**
 * UI State для экрана редактирования финансовой операции.
 *
 * @property isCategoryNotSelected Флаг, что категория не выбрана.
 * @property isFinanceNameNotFilled Флаг, что имя операции не заполнено.
 * @property isPriceEqualsZero Флаг, что цена равна нулю.
 * @property isShowDateDialog Показывать ли диалог выбора даты.
 * @property textColorFinanceName Цвет текста для имени операции.
 * @property textColorCategory Цвет текста для выбранной категории.
 * @property textColorFinancePrice Цвет текста для суммы операции.
 * @property selectedCategoryId Выбранная категория (id).
 * @property pickedDate Выбранная дата.
 * @property financeName Название операции.
 * @property financeDescription Описание операции.
 * @property price Сумма операции в виде строки.
 * @property count Количество операций в виде строки.
 * @property isRegular Флаг регулярной операции.
 * @property selectedFinance Редактируемая финансовая запись.
 * @property categories Список доступных категорий.
 * @property messageResName Ресурс сообщения об ошибке или подсказки.
 */
data class FinanceEditorScreenUiState(
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
    val selectedFinance: Finance = Finance(),

    val categories: List<Category> = emptyList(),

    val messageResName: StringResName? = null,
)