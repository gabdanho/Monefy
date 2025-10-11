package com.example.monefy.presentation.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.monefy.presentation.navigation.model.MonefyGraph
import com.example.monefy.presentation.screens.categories.CategoriesScreen
import com.example.monefy.presentation.screens.category_creator.CategoryCreatorScreen
import com.example.monefy.presentation.screens.finance_creator.FinanceCreatorScreen
import com.example.monefy.presentation.screens.diagrams.DiagramScreen
import com.example.monefy.presentation.screens.finances.FinancesScreen
import com.example.monefy.presentation.screens.history.HistoryFinancesScreen
import com.example.monefy.presentation.screens.main_monefy.MainMonefyScreen
import com.example.monefy.presentation.screens.category_editor.CategoryEditorScreen
import com.example.monefy.presentation.screens.finance_editor.FinanceEditorScreen

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
        FinancesScreen(modifier = modifier)
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

    composable<MonefyGraph.RewriteCategoryScreen> {
        CategoryEditorScreen(modifier = modifier)
    }

    composable<MonefyGraph.RewriteFinanceScreen> {
        FinanceEditorScreen(modifier = modifier)
    }
}