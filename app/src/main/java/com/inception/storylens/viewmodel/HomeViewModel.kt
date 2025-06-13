package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inception.storylens.repository.AuthRepository
import com.inception.storylens.repository.JournalRepository
import com.inception.storylens.ui.home.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState = _homeState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true)

            val currentUser = authRepository.getCurrentUser()
            val userName = currentUser?.displayName ?: "Pengguna"
            val greeting = getGreetingMessage()

            val journalResult = journalRepository.getJournalEntries()

            journalResult.onSuccess { journals ->
                val latestJournal = journals.firstOrNull()
                val recentJournals = if (journals.size > 1) journals.subList(1, journals.size) else emptyList()

                _homeState.value = HomeState(
                    isLoading = false,
                    userName = userName,
                    greeting = greeting,
                    latestJournal = latestJournal,
                    recentJournals = recentJournals
                )
            }.onFailure { exception ->
                _homeState.value = _homeState.value.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }

    private fun getGreetingMessage(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }
}