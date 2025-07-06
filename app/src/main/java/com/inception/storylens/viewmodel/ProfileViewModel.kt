package com.inception.storylens.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inception.storylens.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val userName: String = "Pengguna",
    val userEmail: String = "Tidak ada email",
    val avatarUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdateSuccess: Boolean = false,
    val isPasswordChangeSuccess: Boolean = false,
    val passwordChangeError: String? = null
)

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true, error = null, isUpdateSuccess = false, isPasswordChangeSuccess = false) }
            val currentUser = repository.getCurrentUser()
            if (currentUser != null) {
                val userDetailsResult = repository.getUserDetails(currentUser.uid)
                userDetailsResult.onSuccess { user ->
                    _profileState.update {
                        it.copy(
                            userName = user.name,
                            userEmail = user.email,
                            avatarUrl = user.avatarUrl,
                            isLoading = false
                        )
                    }
                }.onFailure { e ->
                    _profileState.update {
                        it.copy(
                            error = e.message ?: "Failed to load user data.",
                            isLoading = false
                        )
                    }
                }
            } else {
                _profileState.update { it.copy(error = "User not logged in.", isLoading = false) }
            }
        }
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true, error = null, isUpdateSuccess = false) }
            val currentUser = repository.getCurrentUser()
            if (currentUser != null) {
                repository.updateUserName(currentUser.uid, newName).onSuccess {
                    _profileState.update { it.copy(userName = newName, isLoading = false, isUpdateSuccess = true) }
                    loadUserProfile() // Refresh data from Firestore
                }.onFailure { e ->
                    _profileState.update { it.copy(error = e.message ?: "Failed to update name.", isLoading = false) }
                }
            } else {
                _profileState.update { it.copy(error = "User not logged in.", isLoading = false) }
            }
        }
    }

    fun updateUserAvatar(imageUri: Uri?) {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true, error = null, isUpdateSuccess = false) }
            val currentUser = repository.getCurrentUser()
            if (currentUser != null) {
                repository.updateUserAvatar(currentUser.uid, imageUri).onSuccess { newUrl ->
                    _profileState.update { it.copy(avatarUrl = newUrl, isLoading = false, isUpdateSuccess = true) }
                    loadUserProfile() // Refresh data from Firestore
                }.onFailure { e ->
                    _profileState.update { it.copy(error = e.message ?: "Failed to update avatar.", isLoading = false) }
                }
            } else {
                _profileState.update { it.copy(error = "User not logged in.", isLoading = false) }
            }
        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            _profileState.update { it.copy(isLoading = true, passwordChangeError = null, isPasswordChangeSuccess = false) }
            repository.updatePassword(newPassword).onSuccess {
                _profileState.update { it.copy(isLoading = false, isPasswordChangeSuccess = true) }
            }.onFailure { e ->
                _profileState.update { it.copy(isLoading = false, passwordChangeError = e.message ?: "Failed to change password.") }
            }
        }
    }

    fun logout() {
        repository.logoutUser()
    }

    fun clearUpdateStatus() {
        _profileState.update { it.copy(isUpdateSuccess = false, error = null) }
    }

    fun clearPasswordChangeStatus() {
        _profileState.update { it.copy(isPasswordChangeSuccess = false, passwordChangeError = null) }
    }
}