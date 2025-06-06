package com.inception.storylens.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.inception.storylens.repository.AuthRepository
import com.inception.storylens.ui.auth.ForgotPasswordScreen
import com.inception.storylens.ui.auth.LoginScreen
import com.inception.storylens.ui.auth.RegisterScreen
import com.inception.storylens.viewmodel.AuthViewModel
import com.inception.storylens.viewmodel.AuthViewModelFactory

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val firebaseAuth = FirebaseAuth.getInstance()
    val authRepository = AuthRepository(firebaseAuth)
    val authViewModelFactory = AuthViewModelFactory(authRepository)

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }

        composable("register") {
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("forgot_password") {
            val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Selamat Datang! Login Berhasil.")
            }
        }
    }
}
