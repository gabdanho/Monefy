package com.example.monefy.presentation.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.presentation.navigation.Navigator
import com.example.monefy.presentation.navigation.model.MonefyGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesScreenViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesScreenUiState())
    val uiState: StateFlow<CategoriesScreenUiState> = _uiState.asStateFlow()

    fun onCategoryClick(id: Int) {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.FinancesScreen)
        }
    }

    fun onCreateCategoryClick() {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.CreateCategoryScreen)
        }
    }

    fun onRedactorClick(id: Int) {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.RewriteCategoryScreen)
        }
    }
}