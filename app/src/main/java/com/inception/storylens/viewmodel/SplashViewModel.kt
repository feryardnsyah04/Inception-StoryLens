package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(3000L) // Delay selama 3 detik
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Jika sudah login, langsung arahkan ke Home
                _navigationEvent.emit("home")
            } else {
                // Jika belum, arahkan ke Onboarding (atau Login jika Onboarding hanya sekali)
                _navigationEvent.emit("onboarding")
            }
        }
    }
}