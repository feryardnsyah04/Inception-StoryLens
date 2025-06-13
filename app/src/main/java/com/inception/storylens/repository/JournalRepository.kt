package com.inception.storylens.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.inception.storylens.model.JournalEntry
import kotlinx.coroutines.tasks.await

class JournalRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun getJournalEntries(): Result<List<JournalEntry>> {
        return try {
            val userId = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("Pengguna tidak login"))

            val snapshot = firestore.collection("users").document(userId)
                .collection("journals")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .await()

            val journals = snapshot.documents.mapNotNull { doc ->
                doc.toObject(JournalEntry::class.java)?.copy(id = doc.id)
            }
            Result.success(journals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}