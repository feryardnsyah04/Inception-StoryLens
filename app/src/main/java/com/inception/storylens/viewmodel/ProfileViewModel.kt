package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import com.inception.storylens.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileState(
    val userName: String = "",
    val userEmail: String = ""
)

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()

    init {
        val currentUser = repository.getCurrentUser()
        _profileState.value = ProfileState(
            userName = currentUser?.displayName ?: "Pengguna",
            userEmail = currentUser?.email ?: "Tidak ada email"
        )
    }
}