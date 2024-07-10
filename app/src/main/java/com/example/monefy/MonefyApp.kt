package com.example.monefy

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.monefy.ui.navigation.MonefyNavGraph
import com.example.monefy.ui.screens.BottomMenuBar

@Composable
fun MonefyApp(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            BottomMenuBar(
                onAddButtonClick = { navController.navigate(route = "AddFinanceScreen") },
                onFinancesListClick = { navController.navigate(route = "CategoriesListScreen") },
                onPieChartClick = { navController.navigate(route = "MainScreen") },
                onDiagramClick = { navController.navigate(route = "DiagramScreen") }
            )
        }
    ) { innerPadding ->
        MonefyNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}