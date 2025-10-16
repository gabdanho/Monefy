package com.example.monefy.presentation.navigation.model

import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import kotlinx.serialization.Serializable

/**
 * Навигационная модель приложения Monefy.
 */
@Serializable
sealed class MonefyGraph : NavigationDestination {

    /** Экран отображения всех категорий. */
    @Serializable
    data object CategoriesScreen : MonefyGraph()

    /** Экран создания новой категории. */
    @Serializable
    data object CreateCategoryScreen : MonefyGraph()

    /** Экран создания новой финансовой операции. */
    @Serializable
    data object CreateFinanceScreen : MonefyGraph()

    /** Экран с диаграммами. */
    @Serializable
    data object DiagramsScreen : MonefyGraph()

    /**
     * Экран списка финансов, связанных с конкретной категорией.
     *
     * @param categoryId Идентификатор категории, для которой отображаются финансы.
     */
    @Serializable
    data class FinancesScreen(val categoryId: Int) : MonefyGraph()

    /** Экран истории всех финансовых операций. */
    @Serializable
    data object HistoryScreen : MonefyGraph()

    /** Главный экран приложения Monefy. */
    @Serializable
    data object MainMonefyScreen : MonefyGraph()

    /**
     * Экран редактирования существующей категории.
     *
     * @param category Категория, которую нужно изменить.
     */
    @Serializable
    data class RewriteCategoryScreen(val category: Category = Category()) : MonefyGraph()

    /**
     * Экран редактирования существующего финанса.
     *
     * @param finance Финансовая запись, которую нужно изменить.
     */
    @Serializable
    data class RewriteFinanceScreen(val finance: Finance = Finance()) : MonefyGraph()
}