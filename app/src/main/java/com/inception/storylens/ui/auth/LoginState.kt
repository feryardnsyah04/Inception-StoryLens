package com.inception.storylens.ui.auth

data class LoginState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val error: String? = null
)