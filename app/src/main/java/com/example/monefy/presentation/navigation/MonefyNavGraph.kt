package com.example.monefy.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.monefy.presentation.model.Category
import com.example.monefy.presentation.model.Finance
import com.example.monefy.presentation.navigation.model.MonefyGraph
import com.example.monefy.presentation.navigation.model.nav_type.CategoryNavType
import com.example.monefy.presentation.navigation.model.nav_type.FinanceNavType
import com.example.monefy.presentation.screens.categories.CategoriesScreen
import com.example.monefy.presentation.screens.category_creator.CategoryCreatorScreen
import com.example.monefy.presentation.screens.finance_creator.FinanceCreatorScreen
import com.example.monefy.presentation.screens.diagrams.DiagramScreen
import com.example.monefy.presentation.screens.finances.FinancesScreen
import com.example.monefy.presentation.screens.history.HistoryFinancesScreen
import com.example.monefy.presentation.screens.main_monefy.MainMonefyScreen
import com.example.monefy.presentation.screens.category_editor.CategoryEditorScreen
import com.example.monefy.presentation.screens.finance_editor.FinanceEditorScreen
import kotlin.reflect.typeOf

/**
 * Построение навграфа приложения.
 *
 * @param modifier модификатор для экранов
 */
fun NavGraphBuilder.monefyGraph(
    modifier: Modifier = Modifier
) {
    composable<MonefyGraph.MainMonefyScreen> {
        MainMonefyScreen(modifier = modifier)
    }

    composable<MonefyGraph.HistoryScreen> {
        HistoryFinancesScreen(modifier = modifier)
    }

    composable<MonefyGraph.DiagramsScreen> {
        DiagramScreen(modifier = modifier)
    }

    composable<MonefyGraph.FinancesScreen> {
        val args = it.toRoute<MonefyGraph.FinancesScreen>()
        FinancesScreen(
            categoryId = args.categoryId,
            modifier = modifier
        )
    }

    composable<MonefyGraph.CategoriesScreen> {
        CategoriesScreen(modifier = modifier)
    }

    composable<MonefyGraph.CreateCategoryScreen> {
        CategoryCreatorScreen(modifier = modifier)
    }

    composable<MonefyGraph.CreateFinanceScreen> {
        FinanceCreatorScreen(modifier = modifier)
    }

    composable<MonefyGraph.RewriteCategoryScreen>(
        typeMap = mapOf(
            typeOf<Category>() to CategoryNavType()
        )
    ) {
        val args = it.toRoute<MonefyGraph.RewriteCategoryScreen>()
        CategoryEditorScreen(
            category = args.category,
            modifier = modifier
        )
    }

    composable<MonefyGraph.RewriteFinanceScreen>(
        typeMap = mapOf(
            typeOf<Finance>() to FinanceNavType()
        )
    ) {
        val args = it.toRoute<MonefyGraph.RewriteFinanceScreen>()
        FinanceEditorScreen(
            finance = args.finance,
            modifier = modifier
        )
    }
}