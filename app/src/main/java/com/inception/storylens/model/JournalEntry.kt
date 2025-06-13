package com.inception.storylens.model

import com.google.firebase.Timestamp

data class JournalEntry(
    val id: String = "",
    val title: String = "",
    val note: String = "",
    val imageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now()
)