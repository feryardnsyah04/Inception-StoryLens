package com.inception.storylens.ui.auth

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inception.storylens.R
import com.inception.storylens.ui.theme.StoryLensTheme
import com.inception.storylens.viewmodel.AuthViewModel
import com.inception.storylens.repository.AuthRepository
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val forgotPasswordState by authViewModel.forgotPasswordState.collectAsState()
    var showSuccessView by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(forgotPasswordState) {
        when {
            forgotPasswordState.isSuccess -> {
                showSuccessView = true
                delay(4000L)
                onNavigateToLogin()
            }
            forgotPasswordState.error != null -> {
                Toast.makeText(context, "Error: ${forgotPasswordState.error}", Toast.LENGTH_LONG).show()
                authViewModel.clearForgotPasswordError()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface){
        Box(contentAlignment = Alignment.Center) {
            AnimatedVisibility(visible = showSuccessView) {
                SuccessView()
            }

            AnimatedVisibility(visible = !showSuccessView) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock_question),
                        contentDescription = "Forgot Password Icon",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Lupa kata sandi Anda?", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Masukkan email Anda yang terdaftar di bawah ini untuk menerima instruksi pengaturan ulang kata sandi",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { authViewModel.sendPasswordResetEmail(email) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !forgotPasswordState.isLoading
                    ) {
                        if (forgotPasswordState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Kirim")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text("Anda ingat kata sandi Anda?", fontSize = 16.sp)
                        TextButton(
                            onClick = onNavigateToLogin,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Masuk", fontSize = 16.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_envelope_check),
            contentDescription = "Success Icon",
            modifier = Modifier.size(80.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Kami telah mengirimkan instruksi pemulihan kata sandi ke email Anda",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tidak menerima email? Periksa filter spam atau kirim ulang",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Forgot Password - Default View")
@Composable
fun ForgotPasswordScreenPreview() {
    StoryLensTheme {
        @Suppress("ViewModelCreation")
        val fakeViewModel = AuthViewModel(
            repository = object : AuthRepository(null) {
                override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
                    return Result.success(Unit)
                }
            }
        )

        ForgotPasswordScreen(
            authViewModel = fakeViewModel,
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, name = "Forgot Password - Success View")
@Composable
fun SuccessViewPreview() {
    StoryLensTheme {
        SuccessView()
    }
}
