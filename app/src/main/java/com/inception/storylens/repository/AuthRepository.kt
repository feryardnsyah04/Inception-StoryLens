package com.inception.storylens.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

open class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth?
) {

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

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            userCredential?.user?.updateProfile(profileUpdates)?.await()

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
}