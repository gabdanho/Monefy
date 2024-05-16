package com.example.monefy.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.monefy.model.Category
import com.example.monefy.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SpendingUiState(
    val categories: List<Category> = listOf(),
    val totalPriceFromCategories: Double = 0.0,
    val selectedCategoryName: String = "",
    val selectedColorCategory: Color = Color.Transparent,
    val isColorDialogShow: Boolean = false
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

    fun changeColorCategory(color: Color) {
        _uiState.update { currentState ->
            currentState.copy(selectedColorCategory = color)
        }
    }

    fun removeSelectedCategory() {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryName = "")
        }
    }

    fun removeSelectedCategoryColor() {
        _uiState.update { currentState ->
            currentState.copy(selectedColorCategory = Color.Transparent)
        }
    }

    fun resetAllTapedCategories() {
        val updatedCategories = _uiState.value.categories.map { category ->
            category.copy(isTapped = false)
        }
        _uiState.update { currentState -> currentState.copy(categories = updatedCategories) }
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
        var totalExpensePrice = 0.0
        val updatedCategories = _uiState.value.categories.map { category ->
            if (category.name == expense.categoryName) {
                totalExpensePrice = expense.totalPrice
                category.copy(
                    expenses = category.expenses.apply { add(expense) },
                    totalCategoryPrice = category.totalCategoryPrice + expense.totalPrice
                )
            }
            else {
                category
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                categories = updatedCategories,
                totalPriceFromCategories = currentState.totalPriceFromCategories + totalExpensePrice
            )
        }
    }

    fun addNewCategory(category: Category): Boolean {
        // check duplicate
        _uiState.value.categories.map { currentState ->
            if (category.name == currentState.name) {
                return false
            }
        }

        val updatedCategories = _uiState.value.categories.toMutableList()
        updatedCategories.add(category)
        _uiState.update { currentState ->
            currentState.copy(categories = updatedCategories)
        }

        return true
    }

    fun changeColorDialogShow(isShow: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isColorDialogShow = isShow)
        }
    }
}