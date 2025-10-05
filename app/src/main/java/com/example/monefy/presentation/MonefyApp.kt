package com.example.monefy.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.monefy.presentation.navigation.MonefyNavGraph
import com.example.monefy.presentation.components.BottomMenuBar

@Composable
fun MonefyApp(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            BottomMenuBar(
                onAddButtonClick = { navController.navigate(route = "AddFinanceScreen") },
                onFinancesListClick = { navController.navigate(route = "CategoriesListScreen") },
                onPieChartClick = { navController.navigate(route = "MainScreen") },
                onDiagramClick = { navController.navigate(route = "DiagramScreen") },
                onHistoryClick = { navController.navigate(route = "HistoryFinancesScreen") }
            )
        }
    ) { innerPadding ->
        MonefyNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}