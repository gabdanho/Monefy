package com.example.monefy.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.monefy.MonefyApplication
import com.example.monefy.data.Category
import com.example.monefy.data.CategoryDao
import com.example.monefy.data.Finance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SpendingUiState(
    val selectedCategoryColor: Color = Color.Transparent,
    val selectedCategoryId: Int = 0,
    val isColorDialogShow: Boolean = false,
    val selectedCategoryIdFinances: Int = 0,
    val categoryToRewrite: Category = Category(),
    val colorToChange: Color = Color.Transparent,
    val selectedTabIndex: Int = 1,
    val currentCategoryIdForFinances: Int = 0,
    val selectedFinanceToChange: Finance = Finance(),
    val showEmptyFinancesText: Boolean = false
)

class SpendingViewModel(private val categoryDao: CategoryDao) : ViewModel() {
    private val _uiState = MutableStateFlow(SpendingUiState())
    val uiState: StateFlow<SpendingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            updateShowEmptyFinancesText()
            updateTotalCategoryPrice()
        }
    }

    suspend fun updateShowEmptyFinancesText() {
        withContext(Dispatchers.IO) {
            _uiState.update { currentState ->
                currentState.copy(showEmptyFinancesText = categoryDao.getCountFinances() > 0)
            }
        }
    }

    suspend fun isHasSpends(): Boolean {
        return withContext(Dispatchers.IO) {
            categoryDao.getCountFinances() > 0
        }
    }

    fun changeSelectedTabIndex(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedTabIndex = index)
        }
    }

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: String): Flow<List<Category>> = categoryDao.getCategoriesByType(type)

    fun getFinancesByCategoryId(categoryId: Int): Flow<List<Finance>> {
        return categoryDao.getCategoryWithFinances(categoryId).map { categoryWithFinances ->
            categoryWithFinances.finances
        }
    }

    fun changeCurrentCategoryIdForFinances(categoryId: Int) {
        _uiState.update { currentState ->
            currentState.copy(currentCategoryIdForFinances = categoryId)
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
            val categoryWithSpends = categoryDao.getCategoryWithFinances(categoryId).first()
            val finances = categoryWithSpends.finances
            var newTotalCategoryPrice = 0.0

            finances.forEach { finance ->
                newTotalCategoryPrice += finance.price * finance.count.toDouble()
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

    suspend fun addFinance(newFinance: Finance) {
        if (categoryDao.getCategoryById(newFinance.categoryId).first().type == "Расходы")
            categoryDao.addFinance(newFinance.copy(type = "Трата"))
        else
            categoryDao.addFinance(newFinance.copy(type = "Доход"))

        if (uiState.value.showEmptyFinancesText == false ) updateShowEmptyFinancesText()

        updateTotalCategoryPrice()
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

    suspend fun isAllNotTapped(): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext categoryDao.getAllCategories().first().all { !it.isTapped }
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
                categoryDao.updateCategory(newCategory)
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

    fun changeSelectedFinanceToChange(finance: Finance) {
        _uiState.update { currentState ->
            currentState.copy(selectedFinanceToChange = finance)
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

            updateTotalCategoryPrice()
        }
    }

    fun deleteFinance(finance: Finance) {
        viewModelScope.launch {
            categoryDao.deleteFinance(finance)
        }
    }

    fun rewriteFinance(initialFinance: Finance, newFinance: Finance) {
        viewModelScope.launch {
            if (initialFinance.categoryId != newFinance.categoryId) {
                categoryDao.deleteFinance(initialFinance)

                if (categoryDao.getCategoryById(newFinance.categoryId).first().type == "Расходы") {
                    categoryDao.addFinance(newFinance.copy(type = "Трата"))
                }
                else {
                    categoryDao.addFinance(newFinance.copy(type = "Доход"))
                }
            }
            else {
                categoryDao.updateFinance(newFinance)
            }

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