package com.example.monefy.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monefy.ui.screens.AddCategoryScreen
import com.example.monefy.ui.screens.AddFinanceScreen
import com.example.monefy.ui.screens.MainScreen
import com.example.monefy.ui.screens.CategoriesListScreen
import com.example.monefy.ui.screens.DiagramScreen
import com.example.monefy.ui.screens.RewriteCategoryScreen
import com.example.monefy.ui.screens.FinanceListScreen
import com.example.monefy.ui.screens.RewriteFinanceScreen
import com.example.monefy.ui.screens.FinancesViewModel
import com.example.monefy.ui.screens.HistoryFinancesScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MonefyNavGraph(
    financesViewModel: FinancesViewModel = viewModel(factory = FinancesViewModel.factory),
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "MainScreen",
        modifier = modifier
    ) {
        // Основной экран с донатом и таблицей категорий с расходами
        composable(
            route = "MainScreen",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            MainScreen(
                financesViewModel = financesViewModel,
                goToFinance = { finance ->
                    financesViewModel.changeSelectedCategory(finance.categoryId)
                    financesViewModel.changeSelectedFinanceToChange(finance)
                    navController.navigate(route = "RewriteFinanceScreen")
                }
            )
        }
        // Экран добавления финансов
        composable(route = "AddFinanceScreen") {
            AddFinanceScreen(
                financesViewModel = financesViewModel,
                context = LocalContext.current,
                onAddCategoryScreenClick = {
                    navController.navigate(route = "AddCategoryScreen")
                }
            )
        }
        // Экран добавления категории
        composable(route = "AddCategoryScreen") {
            financesViewModel.removeSelectedCategoryColor()
            AddCategoryScreen(
                financesViewModel = financesViewModel,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
        // Экран со списком категории
        composable(route = "CategoriesListScreen") {
            CategoriesListScreen(
                financesViewModel = financesViewModel,
                onCategoryClick = {
                    navController.navigate(route = "FinanceListScreen")
                },
                onAddCategoryClick = {
                    navController.navigate(route = "AddCategoryScreen")
                },
                rewriteCategoryClick = {
                    navController.navigate(route = "RewriteCategoryScreen")
                }
            )
        }
        // Экран с финансами
        composable(route = "FinanceListScreen") {
            FinanceListScreen(
                financesViewModel = financesViewModel,
                rewriteFinanceClick = {
                    navController.navigate("RewriteFinanceScreen")
                }
            )
        }
        // Экран для изменения категории
        composable(route = "RewriteCategoryScreen") {
            RewriteCategoryScreen(
                financesViewModel = financesViewModel,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
        // Экран для изменении финанса
        composable(route = "RewriteFinanceScreen") {
            RewriteFinanceScreen(
                financesViewModel = financesViewModel,
                context = LocalContext.current,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
        // Экран для диаграм
        composable(route = "DiagramScreen") {
            DiagramScreen(
                financesViewModel = financesViewModel,
                updateScreen = {
                    navController.navigate("DiagramScreen")
                }
            )
        }
        // Экран истории финансов
        composable(route = "HistoryFinancesScreen") {
            HistoryFinancesScreen(
                financesViewModel = financesViewModel,
                goToFinance = { finance ->
                    financesViewModel.changeSelectedCategory(finance.categoryId)
                    financesViewModel.changeSelectedFinanceToChange(finance)
                    navController.navigate(route = "RewriteFinanceScreen")
                }
            )
        }
    }
}