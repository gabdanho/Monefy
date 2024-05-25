package com.example.monefy.ui.navigation

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.monefy.model.fake.FakeData
import com.example.monefy.ui.screens.AddCategoryScreen
import com.example.monefy.ui.screens.AddSpendScreen
import com.example.monefy.ui.screens.MainScreen
import com.example.monefy.ui.screens.CategoriesListScreen
import com.example.monefy.ui.screens.RewriteCategoryScreen
import com.example.monefy.ui.screens.RewriteSpendScreen
import com.example.monefy.ui.screens.SpendingListScreen
import com.example.monefy.ui.screens.SpendingViewModel

@Composable
fun MonefyNavGraph(
    spendingViewModel: SpendingViewModel = viewModel(factory = SpendingViewModel.factory),
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "MainScreen",
        modifier = modifier
    ) {
        composable(route = "MainScreen") {
            MainScreen(
                spendingViewModel = spendingViewModel
            )
            spendingViewModel.resetAllTapedCategories()
        }
        composable(route = "AddSpendScreen") {
            spendingViewModel.removeSelectedCategory()
            AddSpendScreen(
                spendingViewModel = spendingViewModel,
                context = LocalContext.current,
                onAddCategoryScreenClick = {
                    navController.navigate(route = "AddCategoryScreen")
                }
            )
        }
        composable(route = "AddCategoryScreen") {
            spendingViewModel.removeSelectedCategoryColor()
            AddCategoryScreen(
                spendingViewModel = spendingViewModel,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = "CategoriesListScreen") {
            CategoriesListScreen(
                spendingViewModel = spendingViewModel,
                onCategoryClick = { spendingList, category ->
                    spendingViewModel.changeSelectedSpendingList(spendingList)
                    spendingViewModel.changeSelectedCategoryToRewrite(category)
                    navController.navigate(route = "SpendingListScreen")
                },
                onAddCategoryClick = {
                    navController.navigate(route = "AddCategoryScreen")
                },
                rewriteCategoryClick = { category ->
                    spendingViewModel.changeSelectedCategoryToRewrite(category)
                    navController.navigate(route = "RewriteCategoryScreen")
                }
            )
        }
        composable(route = "SpendingListScreen") {
            SpendingListScreen(
                spendingViewModel = spendingViewModel,
                rewriteSpendClick = { spend ->
                    spendingViewModel.changeSelectedSpendToRewrite(spend)
                    spendingViewModel.changeSelectedCategory(spend.categoryName)
                    navController.navigate("RewriteSpendScreen")
                }
            )
        }
        composable(route = "RewriteCategoryScreen") {
            RewriteCategoryScreen(
                spendingViewModel = spendingViewModel,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = "RewriteSpendScreen") {
            RewriteSpendScreen(
                spendingViewModel = spendingViewModel,
                context = LocalContext.current,
                deleteSpend = spendingViewModel::deleteSpend,
                endOfScreen = {
                    navController.popBackStack()
                }
            )
        }
    }
}