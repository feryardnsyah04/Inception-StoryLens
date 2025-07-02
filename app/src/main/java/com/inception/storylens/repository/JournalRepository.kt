package com.inception.storylens.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.inception.storylens.model.JournalEntry
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class JournalRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    supabase: SupabaseClient,
    private val context: Context
) {
    private val tag = "StoryLensDebug"
    private val userId: String
        get() = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("Pengguna tidak login")

    private val journalBucket = supabase.storage.from("journals")

    private fun getFileExtension(uri: Uri): String? {
        return context.contentResolver.getType(uri)?.let { mimeType ->
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }

    private suspend fun getBytesFromUri(uri: Uri): ByteArray {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw Exception("Gagal membaca file dari Uri")
        }
    }

    private suspend fun uploadImage(uri: Uri, extension: String): String {
        val fileBytes = getBytesFromUri(uri)
        val filePath = "${userId}/${UUID.randomUUID()}.$extension"

        try {
            journalBucket.upload(filePath, fileBytes)
            val publicUrl = journalBucket.publicUrl(filePath)
            Log.d(tag, "Successfully uploaded image: $publicUrl")
            return publicUrl
        } catch (e: Exception) {
            Log.e(tag, "Failed to upload image: ${e.message}", e)
            throw Exception("Gagal mengunggah gambar: ${e.message}")
        }
    }

    suspend fun getJournalEntries(): Result<List<JournalEntry>> = try {
        val snapshot = firestore.collection("users").document(userId)
            .collection("journals")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get().await()

        val journals = snapshot.documents.mapNotNull { doc ->
            doc.toObject<JournalEntry>()
        }
        Result.success(journals)

    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getJournalById(journalId: String): Result<JournalEntry> = try {
        val doc = firestore.collection("users").document(userId)
            .collection("journals").document(journalId).get().await()
        val journal = doc.toObject<JournalEntry>()
        Result.success(journal ?: throw Exception("Jurnal tidak ditemukan"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addJournal(title: String, note: String, imageUri: Uri?): Result<Unit> = try {
        val imageUrl = if (imageUri != null) {
            val extension = getFileExtension(imageUri)
            if (extension == null || extension !in listOf("jpg", "jpeg", "png")) {
                throw Exception("Format file tidak didukung. Harap pilih .jpg atau .png.")
            }
            uploadImage(imageUri, extension)
        } else { "" }

        val newJournal = JournalEntry(title = title, note = note, imageUrl = imageUrl, timestamp = Timestamp.now())
        firestore.collection("users").document(userId).collection("journals").add(newJournal).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(tag, "Gagal menambah jurnal: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun updateJournal(journalId: String, title: String, note: String, newImageUri: Uri?): Result<Unit> = try {
        val imageUrl = if (newImageUri != null) {
            val extension = getFileExtension(newImageUri)
            if (extension == null || extension !in listOf("jpg", "jpeg", "png")) {
                throw Exception("Format file tidak didukung. Harap pilih .jpg atau .png.")
            }
            // TODO: Hapus gambar lama dari Supabase sebelum upload yang baru
            uploadImage(newImageUri, extension)
        } else {
            getJournalById(journalId).getOrThrow().imageUrl
        }

        val updates = mapOf("title" to title, "note" to note, "imageUrl" to imageUrl)
        firestore.collection("users").document(userId).collection("journals").document(journalId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteJournal(journalId: String): Result<Unit> = try {
        val journalToDelete = getJournalById(journalId).getOrThrow()

        firestore.collection("users").document(userId).collection("journals").document(journalId).delete().await()

        if (journalToDelete.imageUrl.isNotEmpty()) {
            try {
                val filePath = journalToDelete.imageUrl.substringAfterLast("journals/")
                if (filePath.isNotEmpty()) {
                    journalBucket.delete(listOf(filePath))
                    Log.d(tag, "Berhasil menghapus gambar: $filePath")
                }
            } catch (e: Exception) {
                Log.w("JournalRepository", "Gagal menghapus file dari storage (mungkin sudah dihapus): ${e.message}")
            }
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}