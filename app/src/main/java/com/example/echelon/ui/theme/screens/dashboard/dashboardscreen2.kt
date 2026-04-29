package com.example.echelon.ui.theme.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun dashboard2(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {}
        composable("profile") {}
        composable("settings") {}
    }
}