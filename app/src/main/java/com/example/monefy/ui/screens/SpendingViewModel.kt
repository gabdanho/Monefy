package com.example.monefy.ui.screens

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.monefy.MonefyApplication
import com.example.monefy.data.CategoryDao
import com.example.monefy.data.Spend
import com.example.monefy.model.Category
import com.example.monefy.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SpendingUiState(
    val categories: List<Category> = listOf(),
    val totalPriceFromCategories: Double = 0.0,
    val selectedCategoryName: String = "",
    val selectedColorCategory: Color = Color.Transparent,
    val isColorDialogShow: Boolean = false,
    val selectedSpendingList: List<Expense> = listOf(),
    val selectedCategoryToRewrite: Category = Category(),
    val selectedSpendToRewrite: Expense = Expense()
)

class SpendingViewModel(private val categoryDao: CategoryDao) : ViewModel() {
    private val _uiState = MutableStateFlow(SpendingUiState())
    val uiState: StateFlow<SpendingUiState> = _uiState.asStateFlow()

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

    fun changeSelectedSpendingList(newSpendings: List<Expense>) {
        _uiState.update { currentState ->
            currentState.copy(selectedSpendingList = newSpendings)
        }
    }

    fun changeSelectedCategoryToRewrite(category: Category) {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryToRewrite = category)
        }
    }

    fun changeSelectedSpendToRewrite(expense: Expense) {
        _uiState.update { currentState ->
            currentState.copy(selectedSpendToRewrite = expense)
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

    fun rewriteCategory(
        initialName: String,
        newCategoryColor: Color,
        newCategoryName: String
    ): Boolean {
        _uiState.value.categories.map { category ->
            if (newCategoryName == category.name && newCategoryName != initialName) {
                return false
            }
        }
        val rewritedCategory = _uiState.value.selectedCategoryToRewrite.copy(
            name = newCategoryName,
            color = newCategoryColor
        )


        val updatedCategories = _uiState.value.categories.map { category ->
            if (category.name == initialName) {
                category.copy(name = newCategoryName, color = newCategoryColor)
            }
            else category
        }

        _uiState.update { currentState ->
            currentState.copy(categories = updatedCategories, selectedCategoryToRewrite = rewritedCategory)
        }

        return true
    }

    fun rewriteExpense(
        initialSpend: Expense,
        newSpend: Expense
    ) {
        if (initialSpend.categoryName != newSpend.categoryName) {
            deleteSpend(initialSpend)
            addExpense(newSpend)
        }
        else {
            val updatedCategories = _uiState.value.categories.map { category ->
                val updatedSpends = category.expenses.map { expense ->
                    if (initialSpend.name == expense.name) {
                        expense.copy(
                            name = newSpend.name,
                            categoryName = newSpend.categoryName,
                            price = newSpend.price,
                            count = newSpend.count,
                            description = newSpend.description,
                            date = newSpend.date,
                            totalPrice = newSpend.price * newSpend.count.toDouble()
                        )
                    }
                    else {
                        expense
                    }
                }
                Log.i("SpendViewModel", updatedSpends.toString())
                if (initialSpend.categoryName == category.name) {
                    changeSelectedSpendingList(updatedSpends)
                }
                category.copy(
                    expenses = updatedSpends.toMutableList(),
                    totalCategoryPrice = if (updatedSpends.isNotEmpty()) updatedSpends.sumOf { it.totalPrice } else 0.0
                )
            }

            _uiState.update { currentState ->
                currentState.copy(categories = updatedCategories)
            }
            getTotalPriceFromAllCategories()
        }
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
        getTotalPriceFromAllCategories()
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

    fun deleteCategory(deleteCategoryName: String) {
        val updatedCategories = _uiState.value.categories.filter { category ->
            category.name != deleteCategoryName
        }

        _uiState.update { currentState ->
            currentState.copy(categories = updatedCategories)
        }
        getTotalPriceFromAllCategories()
    }

    fun deleteSpend(initialSpend: Expense) {
        val updatedCategories = _uiState.value.categories.map { category ->
            val updatedCategoryPrice = if (initialSpend.categoryName == category.name) category.totalCategoryPrice - initialSpend.count.toDouble() * initialSpend.price
            else category.totalCategoryPrice

            val updatedSpends = if (initialSpend.categoryName == category.name) category.expenses.filter { it.name != initialSpend.name }
            else category.expenses

            if (initialSpend.categoryName == category.name) {
                changeSelectedSpendingList(updatedSpends)
            }

            category.copy(
                expenses = updatedSpends.toMutableList(),
                totalCategoryPrice = updatedCategoryPrice
            )
        }

        _uiState.update { currentState ->
            currentState.copy(categories = updatedCategories)
        }
        getTotalPriceFromAllCategories()
    }

    fun changeColorDialogShow(isShow: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isColorDialogShow = isShow)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MonefyApplication)
                SpendingViewModel(application.database.categoryDao())
            }
        }
    }
}