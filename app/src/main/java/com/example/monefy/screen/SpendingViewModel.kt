package com.example.monefy.screen

import androidx.lifecycle.ViewModel
import com.example.monefy.model.Category
import com.example.monefy.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SpendingUiState(
    val categories: List<Category> = mutableListOf(),
    val totalPriceFromCategories: Double = 0.0,
    val selectedCategoryName: String = ""
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

    fun changeSelectedCategory(categoryName: String) {
        _uiState.update { selectedCategory ->
            if (categoryName != selectedCategory.selectedCategoryName) {
                selectedCategory.copy(selectedCategoryName = categoryName)
            }
            else {
                selectedCategory
            }
        }
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

    fun addExpense(expense: Expense) {
        val updateCategories = _uiState.value.categories.map { category ->
            if (category.name == expense.categoryName) {
                category.copy(expenses = category.expenses.apply { add(expense) })
            }
            else {
                category
            }
        }
        _uiState.update { currentState -> currentState.copy(categories = updateCategories) }
    }
}