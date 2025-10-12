package com.example.monefy.presentation.screens.history

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
class HistoryFinancesScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryFinancesScreenUiState())
    val uiState: StateFlow<HistoryFinancesScreenUiState> = _uiState.asStateFlow()

    init {
        getFinances()
    }

    fun goToFinance(finance: Finance) {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.RewriteFinanceScreen(finance))
        }
    }

    private fun getFinances() {
        viewModelScope.launch {
            val mappedFinances =
                financesRepository.getFinancesByDateSortDesc().map { it.toPresentationLayer() }
            _uiState.update { it.copy(finances = mappedFinances) }
        }
    }
}