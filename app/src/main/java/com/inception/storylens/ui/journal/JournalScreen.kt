package com.inception.storylens.ui.journal

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.inception.storylens.ui.components.JournalPage
import com.inception.storylens.ui.components.StoryLensBottomAppBar
import com.inception.storylens.viewmodel.JournalViewModel

@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        bottomBar = {
            // Pemanggilannya menjadi lebih sederhana
            StoryLensBottomAppBar(
                navController = navController,
                onAddClick = {
                    navController.navigate("add_journal")
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                JournalPage(
                    modifier = Modifier.fillMaxSize(),
                    entries = viewModel.filteredEntries,
                    searchQuery = viewModel.searchQuery,
                    onSearchChange = { viewModel.updateSearchQuery(it) },
                    onEdit = { entry -> navController.navigate("edit_journal/${entry.id}") },
                    onDelete = { entry -> viewModel.deleteJournal(entry.id) },
                    onView = { entry -> navController.navigate("view_journal/${entry.id}") }
                )
            }
        }
    }
}