package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inception.storylens.nav.AppNavHost
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val _navigateToMain = MutableSharedFlow<String>()
    val navigateToMain = _navigateToMain.asSharedFlow()

    fun onStartClicked() {
        viewModelScope.launch {
            _navigateToMain.emit("login")
        }
    }
}