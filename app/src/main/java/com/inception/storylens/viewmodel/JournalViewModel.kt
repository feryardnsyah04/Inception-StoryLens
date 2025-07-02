package com.inception.storylens.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inception.storylens.model.JournalEntry
import com.inception.storylens.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JournalUiState(
    val journals: List<JournalEntry> = emptyList(),
    val selectedJournal: JournalEntry? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState = _uiState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    val filteredEntries: List<JournalEntry>
    get() = uiState.value.journals.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.note.contains(searchQuery, ignoreCase = true)
    }

    init {
        loadJournals()
    }

    fun loadJournals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getJournalEntries().onSuccess { journals ->
                _uiState.value = _uiState.value.copy(journals = journals, isLoading = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(error = error.message, isLoading = false)
            }
        }
    }

    fun getJournalById(id: String) {
        viewModelScope.launch {
            repository.getJournalById(id).onSuccess { journal ->
                _uiState.value = _uiState.value.copy(selectedJournal = journal)
            }
        }
    }

    fun addJournal(title: String, note: String, imageUri: Uri?) {
        viewModelScope.launch {
            repository.addJournal(title, note, imageUri).onSuccess {
                loadJournals()
            }
        }
    }

    fun updateJournal(id: String, title: String, note: String, imageUri: Uri?) {
        viewModelScope.launch {
            repository.updateJournal(id, title, note, imageUri).onSuccess {
                loadJournals()
            }
        }
    }

    fun deleteJournal(id: String) {
        viewModelScope.launch {
            repository.deleteJournal(id).onSuccess {
                loadJournals()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}