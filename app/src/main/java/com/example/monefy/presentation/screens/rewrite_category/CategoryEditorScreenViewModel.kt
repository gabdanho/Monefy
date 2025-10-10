package com.example.monefy.presentation.screens.rewrite_category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.mappers.toDomainLayer
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

    fun clearMessage() = _uiState.update { it.copy(messageResName = null) }

    fun deleteCategory() {
        viewModelScope.launch {
            financesRepository.deleteCategory(_uiState.value.selectedCategory.toDomainLayer())
            _uiState.update { it.copy(messageResName = StringResName.SUCCESS_DELETE_CATEGORY) }
        }
    }

    fun editCategory() {
        viewModelScope.launch {
            val state = _uiState.value
            val initialCategory = state.selectedCategory

            val updatedCategory = initialCategory.copy(
                name = state.categoryName.ifEmpty { initialCategory.name },
                colorLong = if (state.colorCategory != null) initialCategory.colorLong else state.colorCategory
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