package com.example.monefy.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.monefy.presentation.components.BottomMenuBar
import com.example.monefy.presentation.navigation.NavigationAction
import com.example.monefy.presentation.navigation.ObserveAsEvents
import com.example.monefy.presentation.navigation.model.MonefyGraph
import com.example.monefy.presentation.navigation.monefyGraph

/**
 * Главный экран приложения, содержащий навигационный хост.
 *
 * @param viewModel ViewModel экрана [MainScreenViewModel]. По умолчанию создается через Hilt.
 */
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = hiltViewModel<MainScreenViewModel>(),
) {
    val navigator = viewModel.navigator
    val navController = rememberNavController()

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when (action) {
            is NavigationAction.Navigate -> navController.navigate(
                action.navigationDestination
            ) {
                action.navOptions(this)
            }

            NavigationAction.NavigateToPopBackStack -> navController.popBackStack()
        }
    }

    Scaffold(
        bottomBar = {
            BottomMenuBar(
                onPieChartClick = { navController.navigate(MonefyGraph.MainMonefyScreen) },
                onFinancesListClick = { navController.navigate(MonefyGraph.CategoriesScreen) },
                onAddButtonClick = { navController.navigate(MonefyGraph.CreateFinanceScreen) },
                onDiagramClick = { navController.navigate(MonefyGraph.DiagramsScreen) },
                onHistoryClick = { navController.navigate(MonefyGraph.HistoryScreen) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = navigator.startDestination,
            contentAlignment = Alignment.TopCenter
        ) {
            monefyGraph(modifier = Modifier.padding(paddingValues))
        }
    }
}