package com.inception.storylens.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inception.storylens.repository.AuthRepository
import com.inception.storylens.repository.JournalRepository
import com.inception.storylens.ui.auth.ForgotPasswordScreen
import com.inception.storylens.ui.auth.LoginScreen
import com.inception.storylens.ui.auth.RegisterScreen
import com.inception.storylens.viewmodel.AuthViewModel
import com.inception.storylens.viewmodel.AuthViewModelFactory
import com.inception.storylens.viewmodel.HomeViewModel
import com.inception.storylens.viewmodel.HomeViewModelFactory
import com.inception.storylens.ui.home.HomeScreenRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val authRepository = AuthRepository(firebaseAuth)
    val journalRepository = JournalRepository(firebaseAuth, firestore)
    val authViewModelFactory = AuthViewModelFactory(authRepository)
    val homeViewModelFactory = HomeViewModelFactory(authRepository, journalRepository)

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
            val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)
            HomeScreenRoute(homeViewModel = homeViewModel)
        }
    }
}
