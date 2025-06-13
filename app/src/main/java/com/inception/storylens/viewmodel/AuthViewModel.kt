package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inception.storylens.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

data class LoginState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val error: String? = null
)

data class RegisterState(
    val isLoading: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val error: String? = null
)

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordState())
    val forgotPasswordState = _forgotPasswordState.asStateFlow()

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true)
            val result = repository.loginUser(email, password)
            result.onSuccess {
                _loginState.value = LoginState(isLoginSuccess = true)
            }.onFailure { exception ->
                _loginState.value = LoginState(error = exception.message ?: "An unknown error occurred")
            }
        }
    }

    fun clearLoginError() {
        _loginState.value = _loginState.value.copy(error = null)
    }

    fun registerUser(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState(error = "Semua kolom harus diisi")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState(isLoading = true)
            val result = repository.registerUser(name, email, password)
            result.onSuccess {
                _registerState.value = RegisterState(isRegisterSuccess = true)
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is FirebaseAuthWeakPasswordException -> {
                        "Password terlalu lemah. Pastikan memiliki minimal 8 karakter, angka, dan karakter spesial."
                    }
                    else -> {
                        exception.message ?: "Registrasi gagal"
                    }
                }
                _registerState.value = RegisterState(error = errorMessage)
            }
        }
    }

    fun clearRegisterError() {
        _registerState.value = _registerState.value.copy(error = null)
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = ForgotPasswordState(isLoading = true)
            val result = repository.sendPasswordResetEmail(email)
            result.onSuccess {
                _forgotPasswordState.value = ForgotPasswordState(isSuccess = true)
            }.onFailure { exception ->
                _forgotPasswordState.value = ForgotPasswordState(error = exception.message)
            }
        }
    }

    fun clearForgotPasswordError() {
        _forgotPasswordState.value = _forgotPasswordState.value.copy(error = null)
    }
}
