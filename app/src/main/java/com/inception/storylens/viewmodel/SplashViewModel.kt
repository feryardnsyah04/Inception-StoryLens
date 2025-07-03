// 1. Sesuaikan nama package
package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// 2. Hapus import yang tidak digunakan
// import com.example.loadingscreen.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Splash Screen.
 * Tugasnya adalah menangani logika navigasi setelah delay.
 */
class SplashViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(3000L) // Delay selama 3 detik
            // 3. Arahkan ke rute "onboarding" sebagai String
            _navigationEvent.emit("onboarding")
        }
    }
}