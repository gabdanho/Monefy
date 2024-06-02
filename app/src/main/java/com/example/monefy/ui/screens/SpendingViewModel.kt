package com.example.monefy.ui.screens

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.monefy.MonefyApplication
import com.example.monefy.data.Category
import com.example.monefy.data.CategoryDao
import com.example.monefy.data.Spend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SpendingUiState(
    val selectedCategoryColor: Color = Color.Transparent,
    val selectedCategoryId: Int = 0,
    val isColorDialogShow: Boolean = false,
    val selectedCategoryIdSpends: Int = 0,
    val categoryToRewrite: Category = Category(),
    val colorToChange: Color = Color.Transparent,
    val selectedSpendToChange: Spend = Spend()
)

class SpendingViewModel(private val categoryDao: CategoryDao) : ViewModel() {
    private val _uiState = MutableStateFlow(SpendingUiState())
    val uiState: StateFlow<SpendingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { updateTotalCategoryPrice() }
    }

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getSpendsByCategoryId(categoryId: Int): Flow<List<Spend>> {
        return categoryDao.getCategoryWithSpends(categoryId).map { categoryWithSpends ->
            categoryWithSpends.spends
        }
    }

    suspend fun addCategory(newCategory: Category): Boolean {
        return withContext(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories().first()
            categories.forEach { category ->
                if (newCategory.name == category.name) {
                    return@withContext false
                }
            }
            categoryDao.createCategory(newCategory)
            return@withContext true
        }
    }

    suspend fun updateTotalCategoryPrice() {
        val categoriesIdList = categoryDao.getCategoriesId().first()
        categoriesIdList.forEach { categoryId ->
            val categoryWithSpends = categoryDao.getCategoryWithSpends(categoryId).first()
            val spends = categoryWithSpends.spends
            var newTotalCategoryPrice = 0.0

            spends.forEach { spend ->
                newTotalCategoryPrice += spend.price * spend.count.toDouble()
            }

            val updatedCategory = categoryWithSpends.category.copy(totalCategoryPrice = newTotalCategoryPrice)

            categoryDao.updateCategory(updatedCategory)
        }
    }

    fun removeSelectedCategoryId() {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryId = 0)
        }
    }

    suspend fun addSpend(newSpend: Spend) {
        categoryDao.addSpend(newSpend)
        updateTotalCategoryPrice()
    }

    suspend fun isHasSpends(): Boolean {
        return withContext(Dispatchers.IO) {
            categoryDao.getCountSpends() > 0
        }
    }

    suspend fun totalPriceFromAllCategories(): Double {
        return withContext(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories().first()
            categories.sumOf { it.totalCategoryPrice }
        }
    }

    suspend fun updateIsTapped(tappedCategory: Category) {
        val categories = categoryDao.getAllCategories().first()
        categories.map { category ->
            val updatedCategory: Category
            updatedCategory = if (category.id == tappedCategory.id) {
                category.copy(isTapped = !category.isTapped)
            } else {
                category.copy(isTapped = false)
            }
            categoryDao.updateCategory(updatedCategory)
        }
    }

    fun changeColorDialogShow(isShow: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isColorDialogShow = isShow)
        }
    }

    fun changeColorCategory(color: Color) {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryColor = color)
        }
    }

    fun changeSelectedCategory(categoryId: Int) {
        _uiState.update { currentState ->
            if (categoryId != currentState.selectedCategoryId) {
                currentState.copy(selectedCategoryId = categoryId)
            }
            else {
                currentState
            }
        }
    }

    fun changeSelectedCategoryIdSpends(categoryId: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryIdSpends = categoryId)
        }
    }

    fun changeCategoryToRewrite(category: Category) {
        _uiState.update { currentState ->
            currentState.copy(categoryToRewrite = category)
        }
    }

    fun changeColorToChange(color: Color) {
        _uiState.update { currentState ->
            currentState.copy(colorToChange = color)
        }
    }

    fun removeSelectedCategoryColor() {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryColor = Color.Transparent)
        }
    }

    fun removeColorToChange() {
        _uiState.update { currentState ->
            currentState.copy(colorToChange = Color.Transparent)
        }
    }

    suspend fun rewriteCategory(
        initialCategory: Category,
        newCategory: Category
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val categories = getAllCategories().first()
            if (initialCategory.name == newCategory.name) {
                return@withContext true
            }
            else if (categories.count { it.name == newCategory.name } != 0)
                return@withContext false
            else {
                categoryDao.updateCategory(newCategory)
                return@withContext true
            }
        }
    }

    fun changeSelectedSpendToChange(spend: Spend) {
        _uiState.update { currentState ->
            currentState.copy(selectedSpendToChange = spend)
        }
    }

    fun removeSelectedSpendToChange() {
        _uiState.update { currentState ->
            currentState.copy(selectedSpendToChange = Spend())
        }
    }

    fun resetAllTapedCategories() {
        viewModelScope.launch {
            val categories = categoryDao.getAllCategories().first()
            categories.forEach { category ->
                categoryDao.updateCategory(category.copy(isTapped = false))
            }
        }
    }

    fun deleteCategory(
        category: Category
    ) {
        viewModelScope.launch {
            categoryDao.deleteCategory(category)
        }
    }

    fun deleteSpend(spend: Spend) {
        viewModelScope.launch {
            categoryDao.deleteSpend(spend)
            updateTotalCategoryPrice()
        }
    }

    fun rewriteSpend(newSpend: Spend) {
        viewModelScope.launch {
            categoryDao.updateSpend(newSpend)
            updateTotalCategoryPrice()
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