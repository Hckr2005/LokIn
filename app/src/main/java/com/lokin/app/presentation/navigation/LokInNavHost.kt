package com.lokin.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lokin.app.presentation.ui.auth.AuthViewModel
import com.lokin.app.presentation.ui.auth.LoginScreen
import com.lokin.app.presentation.ui.auth.RegisterScreen
import com.lokin.app.presentation.ui.history.HistoryScreen
import com.lokin.app.presentation.ui.main.MainScreen
import com.lokin.app.presentation.ui.onboarding.OnboardingScreen
import com.lokin.app.presentation.ui.vitals.VitalScreen

@Composable
fun LokInNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val start = if (authViewModel.isLoggedIn) Screen.Main.route else Screen.Login.route

    NavHost(navController = navController, startDestination = start, modifier = modifier) {

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}