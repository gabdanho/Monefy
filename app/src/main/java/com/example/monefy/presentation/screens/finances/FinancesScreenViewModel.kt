package com.example.monefy.presentation.screens.finances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.mappers.toPresentationLayer
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.navigation.Navigator
import com.example.monefy.presentation.navigation.model.MonefyGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinancesScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinancesScreenUiState())
    val uiState: StateFlow<FinancesScreenUiState> = _uiState.asStateFlow()

    init {
        getFinanceByCategoryId()
    }

    fun navigateToFinanceEditorScreen(finance: Finance) {
        viewModelScope.launch {
            navigator.navigate(MonefyGraph.RewriteFinanceScreen)
        }
    }

    private fun getFinanceByCategoryId() {
        viewModelScope.launch {
            val categoryId = _uiState.value.categoryId
            val financesMapped =
                financesRepository.getCategoryWithFinances(categoryId).finances.map { it.toPresentationLayer() }
            _uiState.update { it.copy(finances = financesMapped) }
        }
    }
}