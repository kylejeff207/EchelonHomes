package com.example.echelon.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.echelon.ui.theme.screens.dashboard.Dashboard
import com.example.echelon.ui.theme.screens.dashboard.dashboard2
import com.example.echelon.ui.theme.screens.house.AddHouseScreen
import com.example.echelon.ui.theme.screens.house.UpdateHouseScreen
import com.example.echelon.ui.theme.screens.house.ViewHouseScreen
import com.example.echelon.ui.theme.screens.login.LoginScreen
import com.example.echelon.ui.theme.screens.register.RegisterScreen
import com.example.echelon.ui.theme.screens.splashscreen.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(ROUTE_DASHBOARD1) {
            Dashboard(navController = navController)
        }
        composable(ROUTE_DASHBOARD2) {
            dashboard2(navController = navController)
        }
        composable(ROUTE_ADDHOUSE) {
            AddHouseScreen(navController = navController)
        }
        composable(
            route = ROUTE_UPDATEHOUSE,
            arguments = listOf(navArgument("houseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId") ?: ""
            UpdateHouseScreen(navController = navController, houseId = houseId)
        }
        composable(ROUTE_VIEWHOUSE) {
            ViewHouseScreen(navController = navController)
        }
    }
}
