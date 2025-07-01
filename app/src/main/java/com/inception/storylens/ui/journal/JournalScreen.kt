package com.inception.storylens.ui.journal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    Scaffold(
        bottomBar = {
            StoryLensBottomAppBar(
                selectedItemIndex = 1,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                onAddClick = {
                    navController.navigate("add_journal")
                }
            )
        }
    ) { innerPadding ->
        JournalPage(
            modifier = Modifier.padding(innerPadding),

            entries = viewModel.filteredEntries,
            searchQuery = viewModel.searchQuery,
            onSearchChange = { viewModel.updateSearchQuery(it) },
            onEdit = { entry -> navController.navigate("edit_journal/${entry.id}") },
            onDelete = { entry -> viewModel.deleteJournal(entry.id) },
            onView = { entry -> navController.navigate("view_journal/${entry.id}") }
        )
    }
}