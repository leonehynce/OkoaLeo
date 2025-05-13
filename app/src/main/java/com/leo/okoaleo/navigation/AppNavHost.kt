package com.leo.okoaleo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leone.okoleo.ui.screens.*
//import com.okoaleo.ui.screens.ProductEntryScreen
import com.leo.okoaleo.ui.screens.HomeScreen
import com.leo.okoaleo.ui.screens.LoginScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (Firebase.auth.currentUser != null) ROUTE_HOME else ROUTE_LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(ROUTE_REGISTER) {
            RegisterScreen(navController = navController)
        }

        composable(ROUTE_HOME) {
            HomeScreen(navController = navController)
        }

        composable(ROUTE_PROFILE) {
            ProfileScreen(navController = navController)
        }

        composable(ROUTE_REWARDS) {
            RewardsScreen(navController = navController)
        }

        composable(ROUTE_SEARCH) {
            SearchScreen(navController = navController)
        }

        composable(ROUTE_SETTINGS) {
            SettingsScreen(navController = navController)
        }

        composable(ROUTE_SCAN) {
            ProductScanScreen(navController = navController)
        }
    }
}
