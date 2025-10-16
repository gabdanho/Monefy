package com.example.monefy.presentation.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.mappers.toPresentationLayer
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.navigation.Navigator
import com.example.monefy.presentation.navigation.model.MonefyGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана категорий.
 *
 * @property navigator Навигатор для перехода между экранами.
 * @property financesRepository Репозиторий, обеспечивающий доступ к данным категорий.
 */
@HiltViewModel
class CategoriesScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesScreenUiState())
    val uiState: StateFlow<CategoriesScreenUiState> = _uiState.asStateFlow()

    init {
        getCategories()
    }

    fun onCategoryClick(id: Int) {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.FinancesScreen(id))
        }
    }

    fun onCreateCategoryClick() {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.CreateCategoryScreen)
        }
    }

    fun onRedactorClick(category: Category) {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.RewriteCategoryScreen(category))
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            val categories = financesRepository.getAllCategories().map { it.toPresentationLayer() }
            _uiState.update { it.copy(categories = categories) }
        }
    }
}