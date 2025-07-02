package com.inception.storylens.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class JournalEntry(
    @DocumentId val id: String = "",

    val title: String = "",
    val note: String = "",
    val imageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now()
)