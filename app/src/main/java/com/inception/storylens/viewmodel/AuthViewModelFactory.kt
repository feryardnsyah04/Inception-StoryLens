package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inception.storylens.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient // Import SupabaseClient
import android.content.Context // Import Context

class AuthViewModelFactory(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val supabase: SupabaseClient, // Tambahkan SupabaseClient
    private val context: Context // Tambahkan Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pass FirebaseAuth, Firestore, SupabaseClient, dan Context ke AuthRepository
            return AuthViewModel(AuthRepository(firebaseAuth, firestore, supabase, context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}