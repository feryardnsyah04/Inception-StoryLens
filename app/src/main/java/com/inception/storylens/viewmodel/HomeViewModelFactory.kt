package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inception.storylens.repository.AuthRepository
import com.inception.storylens.repository.JournalRepository

class HomeViewModelFactory(
    private val authRepository: AuthRepository,
    private val journalRepository: JournalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(authRepository, journalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}