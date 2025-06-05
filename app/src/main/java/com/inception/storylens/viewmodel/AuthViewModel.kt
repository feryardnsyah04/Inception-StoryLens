package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage

    fun registerUser(email: String, password: String, onSuccess: () -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authMessage.value = "Berhasil daftar"
                    onSuccess()
                } else {
                    _authMessage.value = task.exception?.message
                }
            }
    }

    fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _authMessage.value = if (task.isSuccessful) {
                    "Email reset dikirim"
                } else {
                    task.exception?.message
                }
            }
    }

    fun clearMessage() {
        _authMessage.value = null
    }
}
