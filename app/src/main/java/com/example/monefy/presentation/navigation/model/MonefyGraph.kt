package com.example.monefy.presentation.navigation.model

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import kotlinx.serialization.Serializable

@Serializable
sealed class MonefyGraph : NavigationDestination {

    @Serializable
    data object CategoriesScreen : MonefyGraph()

    @Serializable
    data object CreateCategoryScreen : MonefyGraph()

    @Serializable
    data object CreateFinanceScreen : MonefyGraph()

    @Serializable
    data object DiagramsScreen : MonefyGraph()

    @Serializable
    data class FinancesScreen(val categoryId: Int) : MonefyGraph()

    @Serializable
    data object HistoryScreen : MonefyGraph()

    @Serializable
    data object MainMonefyScreen : MonefyGraph()

    @Serializable
    data class RewriteCategoryScreen(val category: Category = Category()) : MonefyGraph()

    @Serializable
    data class RewriteFinanceScreen(val finance: Finance = Finance()) : MonefyGraph()
}