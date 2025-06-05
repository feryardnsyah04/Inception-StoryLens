package com.inception.storylens.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.inception.storylens.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    val authMessage by viewModel.authMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reset Password", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.resetPassword(email)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kirim Email Reset")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Kembali ke Login")
        }

        if (!authMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = authMessage ?: "", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    val dummyNavController = rememberNavController()
    val dummyViewModel = remember { AuthViewModel() }

    ForgotPasswordScreen(navController = dummyNavController, viewModel = dummyViewModel)
}
