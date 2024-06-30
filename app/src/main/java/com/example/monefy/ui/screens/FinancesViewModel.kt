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
import java.time.LocalDate

data class FinancesUiState(
    val selectedCategoryColor: Color = Color.Transparent,
    val selectedCategoryId: Int = 0,
    val isColorDialogShow: Boolean = false,
    val selectedCategoryIdFinances: Int = 0,
    val categoryToRewrite: Category = Category(),
    val colorToChange: Color = Color.Transparent,
    val selectedTabIndex: Int = 0,
    val selectedDateRangeIndex: Int = 2,
    val currentCategoryIdForFinances: Int = 0,
    val selectedFinanceToChange: Finance = Finance(),
    val isRevenuesEmpty: Boolean = true,
    val isSpendsEmpty: Boolean = true,
    val showDateRangeDialog: Boolean = false,
    val customDateRange: List<LocalDate> = listOf(LocalDate.now(), LocalDate.now())
)

class FinancesViewModel(private val categoryDao: CategoryDao) : ViewModel() {
    private val _uiState = MutableStateFlow(FinancesUiState())
    val uiState: StateFlow<FinancesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateTotalCategoryPrice()
                checkRevenues()
                checkSpends()
            }
        }
    }

    // Проверяем есть ли в БД доходы
    private suspend fun checkRevenues() {
        withContext(Dispatchers.IO) {
            _uiState.update { currentState ->
                currentState.copy(
                    isRevenuesEmpty = categoryDao.getCategoriesByType("Доходы").first().all { it.totalCategoryPrice == 0.0 }
                )
            }
        }
    }

    // Проверяем есть ли в БД расходы
    private suspend fun checkSpends() {
        withContext(Dispatchers.IO) {
            _uiState.update { currentState ->
                currentState.copy(
                    isSpendsEmpty = categoryDao.getCategoriesByType("Расходы").first().all { it.totalCategoryPrice == 0.0 }
                )
            }
        }
    }

    // Изменяем индекс таба выбора расходов/доходов
    fun changeSelectedTabIndex(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedTabIndex = index)
        }
    }

    // Обновляем кастомную дату
    fun updateCustomDateRange(dateRange: List<LocalDate>) {
        _uiState.update { currentState ->
            currentState.copy(customDateRange = dateRange)
        }
    }

    // Изменяем показатель переменной, показывать ли диалговое окно с выбором даты
    fun changeShowDateRangeDialog(isShow: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(showDateRangeDialog = isShow)
        }
    }

    // Изменяем индекс таба выбора даты
    fun changeSelectedDateRangeIndex(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedDateRangeIndex = index)
        }
    }

    // Получить все категории
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    // Получить категории по id
    fun getCategoriesByType(type: String): Flow<List<Category>> = categoryDao.getCategoriesByType(type)

    // Получить финансы по id категории
    fun getFinancesByCategoryId(categoryId: Int): Flow<List<Finance>> {
        return categoryDao.getCategoryWithFinances(categoryId).map { categoryWithFinances ->
            categoryWithFinances.finances
        }
    }

    // Изменить текущую id категории для вывода финансов на экран в соответствии этой категории
    fun changeCurrentCategoryIdForFinances(categoryId: Int) {
        _uiState.update { currentState ->
            currentState.copy(currentCategoryIdForFinances = categoryId)
        }
    }

    // Добавить категорию
    suspend fun addCategory(newCategory: Category): Boolean {
        return withContext(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories().first()
            // Проходим по категориям
            categories.forEach { category ->
                // Проверяем название нового названия и старой категории
                if (newCategory.name == category.name) {
                    return@withContext false
                }
            }
            // Создаём категорию
            categoryDao.createCategory(newCategory)
            return@withContext true
        }
    }

    // Обновить тотал прайс категории
    suspend fun updateTotalCategoryPrice() {
        val categoriesIdList = categoryDao.getCategoriesId().first()
        // Проходимся по id-шникам
        categoriesIdList.forEach { categoryId ->
            val categoryWithFinances = categoryDao.getCategoryWithFinances(categoryId).first()
            val finances = categoryWithFinances.finances
            var newTotalCategoryPrice = 0.0

            // Проходимся по финансам, чтобы обновить тотал прайс категории
            finances.forEach { finance ->
                newTotalCategoryPrice += finance.price * finance.count.toDouble()
            }

            val updatedCategory = categoryWithFinances.category.copy(totalCategoryPrice = newTotalCategoryPrice)

            categoryDao.updateCategory(updatedCategory)
        }
    }

    // Убрать текущую категорию (это где карточка подсвечивается зеленой)
    fun removeSelectedCategoryId() {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryId = 0)
        }
    }

    // Добавить финанс
    suspend fun addFinance(newFinance: Finance) {
        // Проверяем если категория расходов, то делаем тип финанса трата
        if (categoryDao.getCategoryById(newFinance.categoryId).first().type == "Расходы") {
            categoryDao.addFinance(newFinance.copy(type = "Трата"))
        }
        // Проверяем если категория доходов, то делаем тип финанса доход
        else {
            categoryDao.addFinance(newFinance.copy(type = "Доход"))
        }

        updateTotalCategoryPrice()
        checkRevenues()
        checkSpends()
    }

    // Изменить isTapped категории
    suspend fun updateIsTapped(tappedCategory: Category) {
        val categories = categoryDao.getAllCategories().first()
        // Проходимся по категориям
        categories.map { category ->
            val updatedCategory: Category
            // Если тапнутая категория равна категории из списка, то обновляем её isTapped, для остальных ставим false
            updatedCategory = if (category.id == tappedCategory.id) {
                category.copy(isTapped = !category.isTapped)
            } else {
                category.copy(isTapped = false)
            }
            categoryDao.updateCategory(updatedCategory)
        }
    }

    // Проверяем все ли категории тапнуты
    suspend fun isAllNotTapped(): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext categoryDao.getAllCategories().first().all { !it.isTapped }
        }
    }

    // Изменяем показатель переменной, показывать ли диалговое окно с выбором цвета
    fun changeColorDialogShow(isShow: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isColorDialogShow = isShow)
        }
    }

    // Изменяем цвет категории
    fun changeColorCategory(color: Color) {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryColor = color)
        }
    }

    // Изменить выбранную категорию
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

    // Выбираем категорию, которую необходимо изменить
    fun changeCategoryToRewrite(category: Category) {
        _uiState.update { currentState ->
            currentState.copy(categoryToRewrite = category)
        }
    }

    // Выбранный цвет, который будет использоваться для изменения цвета категории
    fun changeColorToChange(color: Color) {
        _uiState.update { currentState ->
            currentState.copy(colorToChange = color)
        }
    }

    // Убрать выбранный цвет категории
    fun removeSelectedCategoryColor() {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryColor = Color.Transparent)
        }
    }

    // Убрать цвет, который будет использоваться для изменения цвета категории
    fun removeColorToChange() {
        _uiState.update { currentState ->
            currentState.copy(colorToChange = Color.Transparent)
        }
    }

    // Изменить категорию
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

    // Выбираем финанс, который будет изменён
    fun changeSelectedFinanceToChange(finance: Finance) {
        _uiState.update { currentState ->
            currentState.copy(selectedFinanceToChange = finance)
        }
    }

    // Обнулить isTapped категорий
    fun resetAllTapedCategories() {
        viewModelScope.launch {
            val categories = categoryDao.getAllCategories().first()
            categories.forEach { category ->
                categoryDao.updateCategory(category.copy(isTapped = false))
            }
        }
    }

    // Удалить категорию
    suspend fun deleteCategory(category: Category) {
        withContext(Dispatchers.IO) {
            categoryDao.deleteCategory(category)
            updateTotalCategoryPrice()
        }
    }

    // Удалить финанс
    fun deleteFinance(finance: Finance) {
        viewModelScope.launch {
            categoryDao.deleteFinance(finance)
            updateTotalCategoryPrice()
            checkRevenues()
            checkSpends()
        }
    }

    // Переписать финанс
    fun rewriteFinance(initialFinance: Finance, newFinance: Finance) {
        Log.i("FinancesViewModel", "init = ${initialFinance.categoryId} new = ${newFinance.categoryId}")
        viewModelScope.launch {
            if (initialFinance.categoryId != newFinance.categoryId) {
                categoryDao.deleteFinance(initialFinance)

                if (categoryDao.getCategoryById(newFinance.categoryId).first().type == "Расходы") {
                    addFinance(newFinance.copy(type = "Трата"))
                }
                else {
                    addFinance(newFinance.copy(type = "Доход"))
                }
            }
            else {
                categoryDao.updateFinance(newFinance)
            }
            updateTotalCategoryPrice()
            checkSpends()
            checkRevenues()
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MonefyApplication)
                FinancesViewModel(application.database.categoryDao())
            }
        }
    }
}