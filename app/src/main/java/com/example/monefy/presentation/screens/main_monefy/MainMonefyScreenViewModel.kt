package com.example.monefy.presentation.screens.main_monefy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monefy.domain.interfaces.local.FinancesRepository
import com.example.monefy.presentation.mappers.toPresentationLayer
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.model.FinanceType
import com.example.monefy.presentation.navigation.Navigator
import com.example.monefy.presentation.navigation.model.MonefyGraph
import com.example.monefy.presentation.utils.isDateInRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainMonefyScreenViewModel @Inject constructor(
    private val navigator: Navigator,
    private val financesRepository: FinancesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainMonefyScreenUiState())
    val uiState: StateFlow<MainMonefyScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            updateDateRange()
            getTotalPriceFromAllCategories()
        }
    }

    fun changeCurrentCategorySumPrice(category: Category?) {
        if (category == null) {
            _uiState.update { it.copy(currentCategorySumPrice = it.totalPriceFromAllCategories) }
        } else {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(currentCategorySumPrice = it.categoriesToSumFinance[category] ?: 0.0)
                }
            }
        }
    }

    fun updateIsTapped(tappedCategory: Category) {
        val updatedMap = _uiState.value.categoriesToSumFinance.mapKeys { (category, _) ->
            if (category.id == tappedCategory.id) category.copy(isTapped = !category.isTapped) else category.copy(
                isTapped = false
            )
        }
        val isAllNoTapped = updatedMap.keys.all { !it.isTapped }
        _uiState.update {
            it.copy(
                categoriesToSumFinance = updatedMap,
                isAllNotTapped = isAllNoTapped
            )
        }
    }

    fun goToFinance(finance: Finance) {
        viewModelScope.launch {
            navigator.navigate(destination = MonefyGraph.RewriteFinanceScreen)
        }
    }

    fun changeSelectedTabIndex(index: Int) {
        _uiState.update { it.copy(selectedFinanceTypeTabIndex = index) }
        updateCategoriesData()
    }

    fun changeIsShowDateRangeDialog(isShow: Boolean) =
        _uiState.update { it.copy(isShowDateRangeDialog = isShow) }

    fun changeSelectedDateRangeIndex(index: Int) {
        _uiState.update { it.copy(selectedDateRangeIndex = index) }
        updateDateRange()
    }

    fun updateCustomDateRange(customDateRange: List<LocalDate>) {
        _uiState.update { it.copy(selectedCustomDateRange = customDateRange) }
        updateDateRange()
    }

    private fun updateDateRange() {
        val dateRangeIndex = _uiState.value.selectedDateRangeIndex

        val dateRange = when (dateRangeIndex) {
            // Кастомный промежуток даты
            0 -> {
                _uiState.value.selectedCustomDateRange
            }
            // Финансы за год
            1 -> {
                val now = LocalDate.now()
                val startOfYear = now.withDayOfYear(1)
                val endOfYear = now.withDayOfYear(now.lengthOfYear())
                listOf(startOfYear, endOfYear)
            }
            // Финансы за месяц
            2 -> {
                val now = LocalDate.now()
                val startOfMonth = now.withDayOfMonth(1)
                val endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
                listOf(startOfMonth, endOfMonth)
            }
            // Финансы за сегодняшний день
            3 -> {
                listOf(LocalDate.now(), LocalDate.now())
            }

            else -> listOf(LocalDate.now(), LocalDate.now())
        }
        _uiState.update { it.copy(dateRange = dateRange) }
        updateCategoriesData()
    }

    private fun updateCategoriesData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val financeTypeIndex = _uiState.value.selectedFinanceTypeTabIndex
            val currentDateRange = _uiState.value.dateRange

            val type = if (financeTypeIndex == 0) FinanceType.EXPENSE else FinanceType.REVENUE
            val categories =
                financesRepository.getCategoriesByType(type.tag).map { it.toPresentationLayer() }

            val categoriesToFinances = categories.associateWith { category ->
                financesRepository.getCategoryWithFinances(category.id).finances
                    .filter {
                        isDateInRange(
                            it.date,
                            currentDateRange.first(),
                            currentDateRange.last()
                        )
                    }
                    .map { it.toPresentationLayer() }
            }

            val categoriesIdToFinances = categoriesToFinances
                .mapKeys { (category, _) ->
                    category.id
                }

            val categoriesToSumFinance = categoriesToFinances
                .mapValues { (_, finances) ->
                    finances.sumOf { it.price * it.count }
                }

            _uiState.update {
                it.copy(
                    categoryIdToFinances = categoriesIdToFinances,
                    categoriesToSumFinance = categoriesToSumFinance,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun getTotalPriceFromAllCategories() {
        val finances = financesRepository.getAllFinances()
        var totalSum = 0.0
        if (finances.isNotEmpty()) {
            totalSum = finances.sumOf { it.price * it.count }
            _uiState.update { it.copy(isHasFinances = true) }
        }
        _uiState.update { it.copy(totalPriceFromAllCategories = totalSum) }
    }
}

/** finances sdelat mapu po kategoriyam id-shnikam i uporadochit' po date*/
// val finances by getFinancesByCategoryId(category.id).collectAsState(emptyList())
//                val filteredByDateFinances =
//                    finances.filter { isDateInRange(it.date, dateRange[0], dateRange[1]) }