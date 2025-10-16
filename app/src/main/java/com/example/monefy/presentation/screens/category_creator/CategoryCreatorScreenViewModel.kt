package com.example.monefy.presentation.screens.category_creator

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

/**
 * ViewModel для экрана создания новой категории.
 *
 * @property financesRepository Репозиторий, используемый для сохранения категории.
 */
@HiltViewModel
class CategoryCreatorScreenViewModel @Inject constructor(
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryCreatorScreenUiState())
    val uiState: StateFlow<CategoryCreatorScreenUiState> = _uiState.asStateFlow()

    fun blinkingColorCategory() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorCategoryColor = ERROR_COLOR) }
                delay(500L)
                _uiState.update { it.copy(textColorCategoryColor = TEXT_COLOR) }
            }
            _uiState.update { it.copy(isCategoryColorError = false) }
        }
    }

    fun blinkingCategoryName() {
        viewModelScope.launch {
            repeat(3) {
                delay(500L)
                _uiState.update { it.copy(textColorCategoryName = ERROR_COLOR) }
                delay(500L)
                _uiState.update { it.copy(textColorCategoryName = TEXT_COLOR) }
            }
            _uiState.update { it.copy(isCategoryNameError = false) }
        }
    }

    fun changeCategoryName(value: String) = _uiState.update { it.copy(categoryName = value) }

    fun changeColorCategory(value: Long) {
        _uiState.update { it.copy(colorCategory = value) }
        changeIsShowColorPicker(false)
    }

    fun changeSelectedFinanceType(value: FinanceType) =
        _uiState.update { it.copy(selectedFinanceType = value) }

    fun changeIsShowColorPicker(value: Boolean) =
        _uiState.update { it.copy(isShowColorPicker = value) }

    fun clearMessage() = _uiState.update { it.copy(messageResName = null) }

    fun createCategory() {
        val state = _uiState.value

        viewModelScope.launch {
            when {
                state.categoryName.isBlank() -> _uiState.update {
                    it.copy(
                        isCategoryNameError = true,
                        messageResName = StringResName.ERROR_NO_NAME_CATEGORY
                    )
                }
                state.colorCategory == null -> _uiState.update {
                    it.copy(
                        isCategoryColorError = true,
                        messageResName = StringResName.ERROR_NO_COLOR_CATEGORY
                    )
                }
                else -> {
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

    companion object {
        private const val ERROR_COLOR = 0xFFBA1A1A
        private const val TEXT_COLOR = 0xFFFFFFFF
    }
}