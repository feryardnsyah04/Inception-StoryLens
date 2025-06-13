package com.inception.storylens.ui.home

import com.inception.storylens.model.JournalEntry

data class HomeState(
    val isLoading: Boolean = true,
    val latestJournal: JournalEntry? = null,
    val recentJournals: List<JournalEntry> = emptyList(),
    val error: String? = null,
    val userName: String = "",
    val greeting: String = ""
)