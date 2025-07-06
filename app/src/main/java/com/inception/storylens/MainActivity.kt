package com.inception.storylens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inception.storylens.ui.theme.StoryLensTheme
import com.inception.storylens.nav.AppNavHost
import com.inception.storylens.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var supabase: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        supabase = createSupabaseClient(
            supabaseUrl = "https://xhuwbvtbwiqxyjwzxhbd.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhodXdidnRid2lxeHlqd3p4aGJkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA0OTQ3ODIsImV4cCI6MjA2NjA3MDc4Mn0.T4p7tKXhkX4jHWS7DPX5q2WOH9iXPZ9xDDO6PKs33kE"
        ) {
            install(Auth)
            install(Storage)
            install(Postgrest)
        }
        authRepository = AuthRepository(firebaseAuth, firestore, supabase, applicationContext)

        checkAutoLogout()

        setContent {
            StoryLensTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AppNavHost()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateLastActivityTimestamp()
    }

    private fun updateLastActivityTimestamp() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("last_activity_timestamp", System.currentTimeMillis())
            apply()
        }
    }

    private fun checkAutoLogout() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val lastActivityTimestamp = sharedPref.getLong("last_activity_timestamp", 0L)

        if (lastActivityTimestamp == 0L) {
            // First launch or user just logged out, save current timestamp
            updateLastActivityTimestamp()
            return
        }

        val currentTime = System.currentTimeMillis()
        val difference = currentTime - lastActivityTimestamp
        val daysDifference = TimeUnit.MILLISECONDS.toDays(difference)

        val autoLogoutThresholdDays = 30L

        if (daysDifference >= autoLogoutThresholdDays && firebaseAuth.currentUser != null) {
            lifecycleScope.launch {
                authRepository.logoutUser()
                // Clear shared preferences on logout
                with(sharedPref.edit()) {
                    remove("last_activity_timestamp")
                    apply()
                }
            }
        }
    }
}