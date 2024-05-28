package com.example.monefy.ui.screens

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    fun removeSelectedCategoryId() {
        _uiState.update { currentState ->
            currentState.copy(selectedCategoryId = 0)
        }
    }

    suspend fun addSpend(newSpend: Spend) = categoryDao.addSpend(newSpend)

    suspend fun isHasSpends(): Boolean {
        return withContext(Dispatchers.IO) {
            categoryDao.getCountCategories() > 0
        }
    }

    fun totalPriceFromAllCategories(): Double {
        var categories = emptyList<Category>()
        viewModelScope.launch {
            categoryDao.getAllCategories().collect {
                categories = it
            }
        }

        return categories.sumOf { it.totalCategoryPrice }
    }

    suspend fun updateIsTapped(tappedCategory: Category) {
        val categories = categoryDao.getAllCategories().first()
        categories.forEach { category ->
            if (category.id == tappedCategory.id) {
                category.copy(isTapped = !category.isTapped)
            }
            else {
                category.copy(isTapped = false)
            }
            categoryDao.updateCategory(category)
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
        Log.i("SpendingViewModel", spend.toString())
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
                category.copy(isTapped = false)
                categoryDao.updateCategory(category)
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
        }
    }

    fun rewriteSpend(newSpend: Spend) {
        viewModelScope.launch {
            categoryDao.updateSpend(newSpend)
        }
    }

//    fun rewriteSpend(
//        initialSpend: Spend,
//        newSpend: Spend
//    ) {
//        if (initialSpend.categoryId != newSpend.categoryId) {
//            deleteSpend(initialSpend)
//            addSpend(newSpend)
//        }
//        else {
//            viewModelScope.launch {
//                categoryDao.updateSpend(newSpend)
//            }
//        }
//    }
//    fun rewriteCategory(
//        newCategoryColor: Long,
//        newCategoryName: String
//    ): Boolean {
//        _uiState.value.categories.map { category ->
//            if (newCategoryName == category.name && newCategoryName != _uiState.value.selectedCategoryToRewrite.name) {
//                return false
//            }
//        }
//        val rewritedCategory = _uiState.value.selectedCategoryToRewrite.copy(
//            id = _uiState.value.selectedCategoryToRewrite.id,
//            name = newCategoryName,
//            color = newCategoryColor
//        )
//
//        viewModelScope.launch {
//            categoryDao.updateCategory(rewritedCategory)
//        }
//
//        val updatedCategories = _uiState.value.categories.map { category ->
//            if (category.id == _uiState.value.selectedCategoryToRewrite.id) {
//                category.copy(name = newCategoryName, color = newCategoryColor)
//            }
//            else category
//        }
//
//        _uiState.update { currentState ->
//            currentState.copy(categories = updatedCategories, selectedCategoryToRewrite = rewritedCategory)
//        }
//
//        return true
//    }
//    fun changeSelectedCategory(categoryId: Int) {
//        _uiState.update { currentState ->
//            if (categoryId != currentState.selectedCategoryId) {
//                currentState.copy(selectedCategoryId = categoryId)
//            }
//            else {
//                currentState
//            }
//        }
//    }
//  fun changeColorDialogShow(isShow: Boolean) {
////        _uiState.update { currentState ->
////            currentState.copy(isColorDialogShow = isShow)
////        }
////    }
//       fun updateIsTappedFromPieChart(name: String) {
//        val updateCategories = _uiState.value.categories.map { category ->
//            if (name == category.name) {
//                category.copy(isTapped = !category.isTapped)
//            }
//            else {
//                category.copy(isTapped = false)
//            }
//        }
//        _uiState.update { currentState -> currentState.copy(categories = updateCategories) }
//    }
//    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
//
//    fun hasSpends(): LiveData<Boolean> {
//        val result = MutableLiveData<Boolean>()
//        viewModelScope.launch {
//            result.value = categoryDao.getSpendCount() > 0
//        }
//        return result
//    }
//
//    fun getSpendsByCategoryId(categoryId: Int): LiveData<List<Spend>> {
//        var spendsList = MutableLiveData<List<Spend>>()
//
//        viewModelScope.launch {
//            categoryDao.getCategoryWithSpends(categoryId).collect {
//                spendsList.postValue(it.spends) ?: spendsList.postValue(emptyList())
//            }
//        }
//
//        return spendsList
//    }
//

//
//    fun changeSelectedSpendingList(categoryId: Int) {
//        viewModelScope.launch {
//            categoryDao.getCategoryWithSpends(categoryId).collect { categoryWithSpends ->
//                _uiState.update { currentState ->
//                    currentState.copy(selectedSpendingList = categoryWithSpends.spends)
//                }
//            }
//        }
//    }
//
//    fun changeSelectedCategoryToRewrite(category: Category) {
//        _uiState.update { currentState ->
//            currentState.copy(selectedCategoryToRewrite = category)
//        }
//    }
//
//    fun changeSelectedSpendToRewrite(spend: Spend) {
//        _uiState.update { currentState ->
//            currentState.copy(selectedSpendToRewrite = spend)
//        }
//    }
//
//
//    fun removeSelectedCategory() {
//        _uiState.update { currentState ->
//            currentState.copy(selectedCategoryId = 0)
//        }
//    }
//
//    fun removeSelectedCategoryColor() {
//        _uiState.update { currentState ->
//            currentState.copy(selectedColorCategory = null)
//        }
//    }
//
//    fun resetAllTapedCategories() {
//        val updatedCategories = _uiState.value.categories.map { category ->
//            category.copy(isTapped = false)
//        }
//        _uiState.update { currentState -> currentState.copy(categories = updatedCategories) }
//    }
//
//    fun updateIsTappedFromPieChart(name: String) {
//        val updateCategories = _uiState.value.categories.map { category ->
//            if (name == category.name) {
//                category.copy(isTapped = !category.isTapped)
//            }
//            else {
//                category.copy(isTapped = false)
//            }
//        }
//        _uiState.update { currentState -> currentState.copy(categories = updateCategories) }
//    }
//
//    fun rewriteCategory(
//        newCategoryColor: Long,
//        newCategoryName: String
//    ): Boolean {
//        _uiState.value.categories.map { category ->
//            if (newCategoryName == category.name && newCategoryName != _uiState.value.selectedCategoryToRewrite.name) {
//                return false
//            }
//        }
//        val rewritedCategory = _uiState.value.selectedCategoryToRewrite.copy(
//            id = _uiState.value.selectedCategoryToRewrite.id,
//            name = newCategoryName,
//            color = newCategoryColor
//        )
//
//        viewModelScope.launch {
//            categoryDao.updateCategory(rewritedCategory)
//        }
//
//        val updatedCategories = _uiState.value.categories.map { category ->
//            if (category.id == _uiState.value.selectedCategoryToRewrite.id) {
//                category.copy(name = newCategoryName, color = newCategoryColor)
//            }
//            else category
//        }
//
//        _uiState.update { currentState ->
//            currentState.copy(categories = updatedCategories, selectedCategoryToRewrite = rewritedCategory)
//        }
//
//        return true
//    }
//
//    fun rewriteSpend(
//        initialSpend: Spend,
//        newSpend: Spend
//    ) {
//        if (initialSpend.categoryId != newSpend.categoryId) {
//            deleteSpend(initialSpend)
//            addSpend(newSpend)
//        }
//        else {
//            viewModelScope.launch {
//                categoryDao.updateSpend(newSpend)
//            }
//        }
//    }
//
//    fun getTotalPriceFromAllCategories() {
//        _uiState.update { currentState ->
//            val categoriesSum = currentState.categories.sumOf { it.totalCategoryPrice }
//            currentState.copy(totalPriceFromCategories = categoriesSum)
//        }
//    }
//
//    fun addSpend(spend: Spend) {
//        viewModelScope.launch {
//            categoryDao.addSpend(spend)
//        }
//    }
//
//    fun addNewCategory(category: Category): Boolean {
//        // check duplicate
//        _uiState.value.categories.map { currentState ->
//            if (category.name == currentState.name) {
//                return false
//            }
//        }
//
//        viewModelScope.launch {
//            categoryDao.createCategory(category)
//        }
//
//        val updatedCategories = _uiState.value.categories.toMutableList()
//        updatedCategories.add(category)
//        _uiState.update { currentState ->
//            currentState.copy(categories = updatedCategories)
//        }
//
//        return true
//    }
//
//    fun deleteCategory(category: Category) {
//        viewModelScope.launch {
//            val categoryWithSpends = categoryDao.getCategoryWithSpends(category.id).first()
//
//            if (categoryWithSpends.spends.isNotEmpty()) {
//                Log.i("ViewModel", "delete category spends")
//                categoryWithSpends.spends.forEach { spend ->
//                    deleteSpend(spend)
//                }
//            }
//            Log.i("ViewModel", "delete category")
//            categoryDao.deleteCategory(category)
//
//            val updatedCategories = categoryDao.getAllCategories().first()
//            _uiState.update { currentState ->
//                currentState.copy(categories = updatedCategories)
//            }
//        }
//    }
//
//    fun deleteSpend(initialSpend: Spend) {
//        viewModelScope.launch {
//            categoryDao.deleteSpend(initialSpend)
//        }
//    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MonefyApplication)
                SpendingViewModel(application.database.categoryDao())
            }
        }
    }
}