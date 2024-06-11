package com.example.monefy.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
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
import com.example.monefy.ui.screens.RewriteCategoryScreen
import com.example.monefy.ui.screens.FinanceListScreen
import com.example.monefy.ui.screens.RewriteFinanceScreen
import com.example.monefy.ui.screens.FinancesViewModel

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
        composable(
            route = "MainScreen",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            MainScreen(
                financesViewModel = financesViewModel,
                updateScreen = { navController.navigate(route = "MainScreen") },
                goToFinance = { finance ->
                    financesViewModel.changeSelectedFinanceToChange(finance)
                    navController.navigate(route = "RewriteFinanceScreen")
                }
            )
            financesViewModel.resetAllTapedCategories()
        }
        composable(route = "AddFinanceScreen") {
            AddFinanceScreen(
                financesViewModel = financesViewModel,
                context = LocalContext.current,
                onAddCategoryScreenClick = {
                    navController.navigate(route = "AddCategoryScreen")
                }
            )
        }
        composable(route = "AddCategoryScreen") {
            financesViewModel.removeSelectedCategoryColor()
            AddCategoryScreen(
                financesViewModel = financesViewModel,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
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
        composable(route = "FinanceListScreen") {
            FinanceListScreen(
                financesViewModel = financesViewModel,
                rewriteFinanceClick = {
                    navController.navigate("RewriteFinanceScreen")
                }
            )
        }
        composable(route = "RewriteCategoryScreen") {
            RewriteCategoryScreen(
                financesViewModel = financesViewModel,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = "RewriteFinanceScreen") {
            RewriteFinanceScreen(
                financesViewModel = financesViewModel,
                context = LocalContext.current,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
    }
}