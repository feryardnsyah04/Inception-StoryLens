package com.inception.storylens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inception.storylens.ui.auth.ForgotPasswordScreen
import com.inception.storylens.ui.auth.LoginScreen
import com.inception.storylens.ui.auth.RegisterScreen
import com.inception.storylens.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
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
                        Text("Berhasil login!")
                    }
                }
            }
        }
    }
}
