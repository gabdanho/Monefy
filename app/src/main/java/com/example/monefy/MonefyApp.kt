package com.example.monefy

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.monefy.ui.navigation.MonefyNavGraph

@Composable
fun MonefyApp(navController: NavHostController = rememberNavController()) {
    MonefyNavGraph(navController)
}