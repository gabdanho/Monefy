package com.example.monefy.presentation.navigation.model

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
    data object FinancesScreen : MonefyGraph()

    @Serializable
    data object HistoryScreen : MonefyGraph()

    @Serializable
    data object MainMonefyScreen : MonefyGraph()

    @Serializable
    data object RewriteCategoryScreen : MonefyGraph()

    @Serializable
    data object RewriteFinanceScreen : MonefyGraph()
}