package com.example.monefy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monefy.model.fake.FakeData
import com.example.monefy.ui.screens.AddCategoryScreen
import com.example.monefy.ui.screens.AddSpendScreen
import com.example.monefy.ui.screens.MainScreen
import com.example.monefy.ui.screens.SpendingViewModel

@Composable
fun MonefyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val spendingViewModel = SpendingViewModel(FakeData.fakeCategories)

    NavHost(
        navController = navController,
        startDestination = "MainScreen",
        modifier = modifier
    ) {
        composable(route = "MainScreen") {
            MainScreen(
                spendingViewModel = spendingViewModel,
                onAddButtonClick = {
                    navController.navigate(route = "AddSpendScreen")
                }
            )
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
            AddCategoryScreen(spendingViewModel = spendingViewModel)
        }
    }
}