package com.example.monefy.presentation.screens.finance_editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.constants.MAX_PRICE
import com.example.monefy.presentation.mappers.toDomainLayer
import com.example.monefy.presentation.mappers.toPresentationLayer
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.StringResName
import com.example.monefy.presentation.navigation.Navigator
import com.example.monefy.presentation.navigation.model.MonefyGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FinanceEditorScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceEditorScreenUiState())
    val uiState: StateFlow<FinanceEditorScreenUiState> = _uiState.asStateFlow()

    init {
        getCategories()
    }

    fun blinkingFinanceName() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorFinanceName = Color.Red.toColorLong()) } // TODO : убрать Color
                delay(500L)
                _uiState.update { it.copy(textColorFinanceName = Color.Black.toColorLong()) } // TODO : убрать Color
            }
            _uiState.update { it.copy(isFinanceNameNotFilled = false) }
        }
    }

    fun blinkingSelectedTypeFinance() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorCategory = Color.Red.toColorLong()) } // TODO : убрать Color
                delay(500L)
                _uiState.update { it.copy(textColorCategory = Color.Black.toColorLong()) } // TODO : убрать Color
            }
            _uiState.update { it.copy(isCategoryNotSelected = false) }
        }
    }

    fun onFinanceNameChange(value: String) = _uiState.update { it.copy(financeName = value) }

    fun onPriceChange(value: String) {
        if (value == "" || value == ".") {
            _uiState.update { it.copy(price = 0.0) }
        }
        // Если пользователь удаляет символ
        else if (value.length < _uiState.value.price.toString().length) {
            _uiState.update { it.copy(price = value.toDouble()) }
        }
        // Если пользователь пытается ввести после введённого нуля еще один - запрещаем
        else if (value == "00") {
            // SKIP, NE NADO
        }
        // Проверяем что вводится цифра или точка && Точка одна или нет && Проверяем чтобы число не было больше константы
        else if (value.all { it.isDigit() || it == '.' } && value.count { it == '.' } <= 1 && value.toDouble() < MAX_PRICE) {
            _uiState.update { it.copy(price = value.toDouble()) }
        }
    }

    fun minusCount() {
        if (_uiState.value.count >= 1) {
            _uiState.update { it.copy(count = it.count - 1) }
        }
    }

    fun plusCount() = _uiState.update { it.copy(count = it.count + 1) }

    fun onCountChange(value: String) {
        if (value.isDigitsOnly() && value.toInt() != 0) {
            _uiState.update { it.copy(count = value.toInt()) }
        }
    }

    fun changeSelectedCategory(id: Int) = _uiState.update { it.copy(selectedCategoryId = id) }

    fun onAddCategoryScreenClick() {
        viewModelScope.launch {
            navigator.navigate(MonefyGraph.CreateCategoryScreen)
        }
    }

    fun onChangeRegular() = _uiState.update { it.copy(isRegular = !it.isRegular) }

    fun changeIsShowDateDialog(value: Boolean) =
        _uiState.update { it.copy(isShowDateDialog = value) }

    fun onDescriptionChange(value: String) = _uiState.update { it.copy(financeDescription = value) }

    fun clearMessage() = _uiState.update { it.copy(messageResName = null) }

    fun onPickedDateChange(date: LocalDate) {
        _uiState.update { it.copy(pickedDate = date) }
    }

    fun editFinance() {
        viewModelScope.launch {
            val state = _uiState.value
            val initialFinance = state.selectedFinance

            if (state.financeName.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isFinanceNameNotFilled = true,
                        messageResName = StringResName.ERROR_NO_NAME_FINANCE
                    )
                }
            } else {
                val updatedFinance = initialFinance.copy(
                    name = state.financeName,
                    categoryId = state.selectedCategoryId,
                    description = state.financeDescription,
                    price = state.price,
                    type = state.categories[state.selectedCategoryId].type,
                    date = state.pickedDate.toString(),
                    count = state.count
                )

                try {
                    financesRepository.updateFinance(updatedFinance.toDomainLayer())
                    _uiState.update { it.copy(messageResName = StringResName.SUCCESS_EDIT_FINANCE) }
                    navigator.navigatePopBackStack()
                } catch (_: Exception) {
                    _uiState.update { it.copy(messageResName = StringResName.ERROR_TO_EDIT_FINANCE) }
                }
            }
        }
    }

    fun deleteFinance() {
        viewModelScope.launch {
            financesRepository.deleteFinance(_uiState.value.selectedFinance.toDomainLayer())
            _uiState.update { it.copy(messageResName = StringResName.SUCCESS_DELETE_FINANCE) }
        }
    }

    fun initFinance(finance: Finance) {
        _uiState.update {
            it.copy(
                selectedFinance = finance,
                pickedDate = LocalDate.parse(finance.date),
                financeName = finance.name,
                financeDescription = finance.description,
                price = finance.price,
                count = finance.count,
                isRegular = finance.isRegular,
                selectedCategoryId = finance.categoryId
            )
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            val categories = financesRepository.getAllCategories().map { it.toPresentationLayer() }
            _uiState.update { it.copy(categories = categories) }
        }
    }
}