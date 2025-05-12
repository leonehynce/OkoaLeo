package com.leo.okoaleo.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leo.okoaleo.ui.screens.*

// Make sure ALL screens are under the same consistent package
//import com.leo.okoaleo.ui.screens.LoginScreen
//import com.leo.okoaleo.ui.screens.ProductScanScreen
//import com.leo.okoaleo.ui.screens.ProfileScreen
//import com.leo.okoaleo.ui.screens.RegisterScreen
//import com.leo.okoaleo.ui.screens.RewardsScreen
//import com.leo.okoaleo.ui.screens.SearchScreen
//import com.leo.okoaleo.ui.screens.SettingsScreen
import com.leone.okoleo.ui.screens.LoginScreen
import com.leone.okoleo.ui.screens.ProductScanScreen
import com.leone.okoleo.ui.screens.ProfileScreen
import com.leone.okoleo.ui.screens.RegisterScreen
import com.leone.okoleo.ui.screens.RewardsScreen
import com.leone.okoleo.ui.screens.SearchScreen
import com.leone.okoleo.ui.screens.SettingsScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    val startDestination = if (currentUser != null) Routes.Home.route else Routes.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    currentUser = Firebase.auth.currentUser
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    currentUser = Firebase.auth.currentUser
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                onNavigateToScan = { navController.navigate(Routes.Scan.route) },
                onNavigateToSearch = { navController.navigate(Routes.Search.route) },
                onNavigateToRewards = { navController.navigate(Routes.Rewards.route) },
                onNavigateToSettings = { navController.navigate(Routes.Settings.route) },
                onLogout = {
                    Firebase.auth.signOut()
                    currentUser = null
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Rewards.route) {
            RewardsScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Search.route) {
            SearchScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Settings.route) {
            SettingsScreen(
                navController = navController,
                onLogout = {
                    Firebase.auth.signOut()
                    currentUser = null
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Scan.route) {
            ProductScanScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
