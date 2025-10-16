package com.example.monefy.presentation.screens.category_editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.mappers.toDomainLayer
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.model.StringResName
import com.example.monefy.presentation.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryEditorScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryEditorScreenUiState())
    val uiState: StateFlow<CategoryEditorScreenUiState> = _uiState.asStateFlow()

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

    fun deleteCategory() {
        viewModelScope.launch {
            val categoryFinances =
                financesRepository.getCategoryWithFinances(_uiState.value.selectedCategory.id)?.finances
            categoryFinances?.let { finances ->
                finances.forEach {
                    financesRepository.deleteFinance(it)
                }
            }
            financesRepository.deleteCategory(_uiState.value.selectedCategory.toDomainLayer())

            _uiState.update { it.copy(messageResName = StringResName.SUCCESS_DELETE_CATEGORY) }
            navigator.navigatePopBackStack()
        }
    }

    fun editCategory() {
        viewModelScope.launch {
            val state = _uiState.value
            val initialCategory = state.selectedCategory

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
                    val updatedCategory = initialCategory.copy(
                        name = state.categoryName,
                        colorLong = state.colorCategory,
                        type = state.selectedFinanceType
                    )

                    try {
                        financesRepository.updateCategory(updatedCategory.toDomainLayer())
                        _uiState.update { it.copy(messageResName = StringResName.SUCCESS_EDIT_CATEGORY) }
                        navigator.navigatePopBackStack()

                    } catch (_: Exception) {
                        _uiState.update { it.copy(messageResName = StringResName.ERROR_TO_EDIT_CATEGORY) }
                    }
                }
            }
        }
    }

    fun initCategory(category: Category) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                categoryName = category.name,
                selectedFinanceType = category.type,
                colorCategory = category.colorLong
            )
        }
    }

    companion object {
        private const val ERROR_COLOR = 0xFFBA1A1A
        private const val TEXT_COLOR = 0xFFFFFFFF
    }
}