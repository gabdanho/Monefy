package com.example.monefy.presentation.screens.create_finance

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
class AddFinanceScreenViewModel @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFinanceScreenUiState())
    val uiState: StateFlow<AddFinanceScreenUiState> = _uiState.asStateFlow()

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

    fun createFinance() {
        viewModelScope.launch {
            // Нет названия и категории
            if (_uiState.value.financeName.isEmpty() && _uiState.value.selectedCategoryId == 0) {
                _uiState.update {
                    it.copy(
                        isFinanceNameNotFilled = true,
                        isCategoryNotSelected = true,
                        messageResName = StringResName.ERROR_NO_NAME_FINANCE_AND_NO_SELECTED_CATEGORY
                    )
                }
            }
            // Нет названия
            else if (_uiState.value.financeName.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isFinanceNameNotFilled = true,
                        messageResName = StringResName.ERROR_NO_NAME_FINANCE
                    )
                }
            }
            // Нет категории
            else if (_uiState.value.selectedCategoryId == 0) {
                _uiState.update {
                    it.copy(
                        isCategoryNotSelected = true,
                        messageResName = StringResName.ERROR_NO_NAME_CATEGORY
                    )
                }
            }
            // Если всё хорошо - добавляем, при этом очищаем введённые параметры
            else {
                val state = _uiState.value
                val newFinance = Finance(
                    categoryId = state.selectedCategoryId,
                    name = state.financeName,
                    description = state.financeDescription,
                    count = state.count,
                    price = state.price,
                    date = state.pickedDate,
                    isRegular = state.isRegular
                )
                try {
                    financesRepository.addFinance(newFinance.toDomainLayer())

                    _uiState.update {
                        it.copy(
                            financeName = "",
                            price = 0.0,
                            count = 1,
                            pickedDate = LocalDate.now(),
                            financeDescription = "",
                            selectedCategoryId = 0,
                            isRegular = false,
                            messageResName = StringResName.SUCCESS_FINANCE_CREATED
                        )
                    }
                } catch (_: Exception) {
                    _uiState.update { it.copy(messageResName = StringResName.ERROR_TO_CREATE_FINANCE) }
                }
            }
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            val categories = financesRepository.getAllCategories().map { it.toPresentationLayer() }
            _uiState.update { it.copy(categories = categories) }
        }
    }
}