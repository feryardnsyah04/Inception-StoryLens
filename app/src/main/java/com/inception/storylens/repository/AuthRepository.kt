package com.inception.storylens.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.inception.storylens.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

open class AuthRepository(
    private val firebaseAuth: FirebaseAuth?,
    firestore: FirebaseFirestore,
    supabase: SupabaseClient, // Tambahkan SupabaseClient
    private val context: Context // Tambahkan Context untuk FileProvider
) {
    private val tag = "AuthRepository"
    private val usersCollection = firestore.collection("users")
    private val avatarBucket = supabase.storage.from("avatars") // Bucket khusus avatar

    private fun getFileExtension(uri: Uri): String? {
        return context.contentResolver.getType(uri)?.let { mimeType ->
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }

    private suspend fun uploadAvatarImage(uri: Uri, extension: String, userId: String): String {
        val fileBytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw Exception("Gagal membaca file dari Uri")
        }
        val filePath = "${userId}/avatar_${UUID.randomUUID()}.$extension"

        return try {
            avatarBucket.upload(filePath, fileBytes)
            val publicUrl = avatarBucket.publicUrl(filePath)
            Log.d(tag, "Successfully uploaded avatar: $publicUrl")
            publicUrl
        } catch (e: Exception) {
            Log.e(tag, "Failed to upload avatar: ${e.message}", e)
            throw Exception("Gagal mengunggah avatar: ${e.message}")
        }
    }

    private suspend fun deleteOldAvatar(oldAvatarUrl: String?, userId: String) {
        if (oldAvatarUrl != null && oldAvatarUrl.isNotEmpty()) {
            try {
                // Supabase public URL structure:
                // [supabaseUrl]/storage/v1/object/public/avatars/[userId]/avatar_UUID.extension
                val filePath = oldAvatarUrl.substringAfter("public/avatars/")
                if (filePath.startsWith("$userId/")) { // Ensure it's the user's avatar
                    avatarBucket.delete(listOf(filePath))
                    Log.d(tag, "Berhasil menghapus avatar lama: $filePath")
                }
            } catch (e: Exception) {
                Log.w(tag, "Gagal menghapus avatar lama dari storage (mungkin sudah dihapus atau URL salah): ${e.message}")
            }
        }
    }

    open suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth?.signInWithEmailAndPassword(email, password)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            val userCredential = firebaseAuth?.createUserWithEmailAndPassword(email, password)?.await()
            val firebaseUser = userCredential?.user

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser?.updateProfile(profileUpdates)?.await()

            if (firebaseUser != null) {
                val newUser = User(uid = firebaseUser.uid, name = name, email = email, avatarUrl = null)
                usersCollection.document(firebaseUser.uid).set(newUser).await()
            } else {
                throw IllegalStateException("Firebase user is null after registration")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth?.sendPasswordResetEmail(email)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = firebaseAuth?.currentUser

    suspend fun getUserDetails(uid: String): Result<User> {
        return try {
            val document = usersCollection.document(uid).get().await()
            val user = document.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User data not found in Firestore"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserName(uid: String, newName: String): Result<Unit> {
        return try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            firebaseAuth?.currentUser?.updateProfile(profileUpdates)?.await()

            usersCollection.document(uid).update("name", newName).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserAvatar(uid: String, imageUri: Uri?): Result<String> {
        return try {
            val currentUserData = getUserDetails(uid).getOrThrow()
            val oldAvatarUrl = currentUserData.avatarUrl

            val imageUrl = if (imageUri != null) {
                val extension = getFileExtension(imageUri)
                    ?: throw Exception("Tidak dapat menentukan ekstensi file untuk avatar.")
                if (extension !in listOf("jpg", "jpeg", "png")) {
                    throw Exception("Format file tidak didukung. Harap pilih .jpg atau .png.")
                }
                // Hapus avatar lama sebelum mengunggah yang baru
                deleteOldAvatar(oldAvatarUrl, uid)
                uploadAvatarImage(imageUri, extension, uid)
            } else {
                // Jika imageUri null, berarti user ingin menghapus avatar atau tidak mengubahnya
                // Jika oldAvatarUrl ada, kita hapus
                deleteOldAvatar(oldAvatarUrl, uid)
                null // Set avatarUrl di Firestore menjadi null
            }

            usersCollection.document(uid).update("avatarUrl", imageUrl).await()
            Result.success(imageUrl ?: "") // Kembalikan URL baru atau string kosong jika dihapus
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            firebaseAuth?.currentUser?.updatePassword(newPassword)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logoutUser() {
        firebaseAuth?.signOut()
    }
}