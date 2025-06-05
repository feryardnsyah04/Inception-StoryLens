package com.inception.storylens.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.inception.storylens.viewmodel.AuthViewModel
import com.inception.storylens.ui.auth.ForgotPasswordScreen
import com.inception.storylens.ui.auth.LoginScreen
import com.inception.storylens.ui.auth.RegisterScreen
import com.inception.storylens.ui.pages.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController, viewModel = authViewModel)
        }
        composable("home") {
            HomeScreen()
        }
    }
}
