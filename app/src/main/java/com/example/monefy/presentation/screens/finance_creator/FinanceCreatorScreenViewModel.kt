package com.example.monefy.presentation.screens.finance_creator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.mappers.toDomainLayer
import com.example.monefy.presentation.mappers.toPresentationLayer
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.StringResName
import com.example.monefy.presentation.navigation.Navigator
import com.example.monefy.presentation.navigation.model.MonefyGraph
import com.example.monefy.presentation.utils.priceFormatter
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
class FinanceCreatorScreenViewModel @Inject constructor(
    private val financesRepository: FinancesRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceCreatorScreenUiState())
    val uiState: StateFlow<FinanceCreatorScreenUiState> = _uiState.asStateFlow()

    init {
        getCategories()
    }

    fun blinkingFinanceName() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorFinanceName = ERROR_COLOR) }
                delay(500L)
                _uiState.update { it.copy(textColorFinanceName = TEXT_COLOR) }
            }
            _uiState.update { it.copy(isFinanceNameNotFilled = false) }
        }
    }

    fun blinkingSelectedTypeFinance() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorCategory = ERROR_COLOR) }
                delay(500L)
                _uiState.update { it.copy(textColorCategory = TEXT_COLOR) }
            }
            _uiState.update { it.copy(isCategoryNotSelected = false) }
        }
    }

    fun blinkingFinancePrice() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorFinancePrice = ERROR_COLOR) }
                delay(500L)
                _uiState.update { it.copy(textColorFinancePrice = TEXT_COLOR) }
            }
            _uiState.update { it.copy(isPriceEqualsZero = false) }
        }
    }

    fun onFinanceNameChange(value: String) = _uiState.update { it.copy(financeName = value) }

    fun onPriceChange(value: String) {
        priceFormatter(value)?.let { newValue ->
            _uiState.update { it.copy(price = newValue) }
        }
    }

    fun minusCount() {
        if (_uiState.value.count.toInt() >= 1) {
            _uiState.update { it.copy(count = (it.count.toInt() - 1).toString()) }
        }
    }

    fun plusCount() = _uiState.update { it.copy(count = (it.count.toInt() + 1).toString()) }

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
            val state = _uiState.value

            when {
                state.financeName.isEmpty() -> {
                    _uiState.update {
                        it.copy(
                            isFinanceNameNotFilled = true,
                            messageResName = StringResName.ERROR_NO_NAME_FINANCE
                        )
                    }
                }

                state.selectedCategoryId == 0 -> {
                    _uiState.update {
                        it.copy(
                            isCategoryNotSelected = true,
                            messageResName = StringResName.ERROR_NO_NAME_CATEGORY
                        )
                    }
                }

                state.price.isEmpty() || state.price.toDouble() <= 0 -> {
                    _uiState.update {
                        it.copy(
                            isPriceEqualsZero = true,
                            messageResName = StringResName.ERROR_PRICE_EQUALS_ZERO
                        )
                    }
                }

                else -> {
                    val state = _uiState.value
                    val newFinance = Finance(
                        categoryId = state.selectedCategoryId,
                        name = state.financeName,
                        description = state.financeDescription,
                        count = state.count.toInt(),
                        price = state.price.toDouble(),
                        date = state.pickedDate.toString(),
                        type = state.categories.first { it.id == state.selectedCategoryId }.type,
                        isRegular = state.isRegular
                    )
                    try {
                        financesRepository.addFinance(newFinance.toDomainLayer())

                        _uiState.update {
                            it.copy(
                                financeName = "",
                                price = "0",
                                count = "1",
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
    }

    private fun getCategories() {
        viewModelScope.launch {
            val categories = financesRepository.getAllCategories().map { it.toPresentationLayer() }
            _uiState.update { it.copy(categories = categories) }
        }
    }

    companion object {
        private const val ERROR_COLOR = 0xFFBA1A1A
        private const val TEXT_COLOR = 0xFFFFFFFF
    }
}