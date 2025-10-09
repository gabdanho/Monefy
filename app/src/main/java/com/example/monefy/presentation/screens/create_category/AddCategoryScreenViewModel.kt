package com.example.monefy.presentation.screens.create_category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.mappers.toDomainLayer
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.StringResName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryScreenViewModel @Inject constructor(
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCategoryScreenUiState())
    val uiState: StateFlow<AddCategoryScreenUiState> = _uiState.asStateFlow()

    fun blinkingColorCategory() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorCategoryColor = Color.Red.toColorLong()) } // TODO : убрать Color
                delay(500L)
                _uiState.update { it.copy(textColorCategoryColor = Color.Black.toColorLong()) } // TODO : убрать Color
            }
            _uiState.update { it.copy(isCategoryColorError = false) }
        }
    }

    fun blinkingCategoryName() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorCategoryName = Color.Red.toColorLong()) } // TODO : убрать Color
                delay(500L)
                _uiState.update { it.copy(textColorCategoryName = Color.Black.toColorLong()) } // TODO : убрать Color
            }
            _uiState.update { it.copy(isCategoryColorError = false) }
        }
    }

    fun changeCategoryName(value: String) = _uiState.update { it.copy(categoryName = value) }

    fun changeColorCategory(value: Long) = _uiState.update { it.copy(colorCategory = value) }

    fun changeSelectedFinanceType(value: FinanceType) =
        _uiState.update { it.copy(selectedFinanceType = value) }

    fun changeIsShowColorPicker(value: Boolean) =
        _uiState.update { it.copy(isShowColorPicker = value) }

    fun changeIsShowSnackBar(value: Boolean) =
        _uiState.update { it.copy(isShowSnackBar = value, messageResName = null) }

    fun createCategory() {
        val state = _uiState.value

        viewModelScope.launch {
            if (state.categoryName.isBlank() && state.colorCategory == null) {
                _uiState.update {
                    it.copy(
                        isCategoryNameError = true,
                        isCategoryColorError = true,
                        messageResName = StringResName.ERROR_NO_NAME_AND_COLOR_CATEGORY
                    )
                }
            }
            // Нет названия
            else if (state.categoryName.isBlank()) {
                _uiState.update {
                    it.copy(
                        isCategoryNameError = true,
                        messageResName = StringResName.ERROR_NO_NAME_CATEGORY
                    )
                }
            }
            // Нет цвета
            else if (state.colorCategory == null) {
                _uiState.update {
                    it.copy(
                        isCategoryColorError = true,
                        messageResName = StringResName.ERROR_NO_COLOR_CATEGORY
                    )
                }
            }
            // Всё хорошо - добавляем
            else {
                val newCategory = Category(
                    name = state.categoryName,
                    colorLong = state.colorCategory,
                    type = state.selectedFinanceType
                )
                try {
                    financesRepository.createCategory(newCategory.toDomainLayer())
                    _uiState.update { it.copy(messageResName = StringResName.SUCCESS_CATEGORY_CREATED) }
                } catch (_: Exception) {
                    _uiState.update { it.copy(messageResName = StringResName.ERROR_TO_CREATE_CATEGORY) }
                } finally {
                    changeIsShowSnackBar(true)
                    _uiState.update {
                        it.copy(
                            categoryName = "",
                            selectedFinanceType = FinanceType.EXPENSE,
                            colorCategory = null
                        )
                    }
                }
            }
        }
    }
}