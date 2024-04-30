package com.example.monefy.screen

import androidx.lifecycle.ViewModel
import com.example.monefy.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SpendingUiState(
    val categories: List<Category> = emptyList(),
    val totalPriceFromCategories: Double = 0.0
)

class SpendingViewModel(categories: List<Category>) : ViewModel() {
    private val _uiState = MutableStateFlow(SpendingUiState())
    val uiState: StateFlow<SpendingUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(categories = categories)
        }
        getTotalPriceFromAllCategories()
    }

    fun updateIsTappedFromPieChart(name: String) {
        val updateCategories = _uiState.value.categories.map { category ->
            if (name == category.name) {
                category.copy(isTapped = !category.isTapped)
            }
            else {
                category.copy(isTapped = false)
            }
        }
        _uiState.update { currentState -> currentState.copy(categories = updateCategories) }
    }

    fun getTotalPriceFromAllCategories() {
        _uiState.update { currentState ->
            val categoriesSum = currentState.categories.sumOf { it.totalCategoryPrice }
            currentState.copy(totalPriceFromCategories = categoriesSum)
        }
    }
}