package com.inception.storylens.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inception.storylens.ui.components.LatestJournalCard
import com.inception.storylens.ui.components.RecentJournalItem
import com.inception.storylens.ui.components.StoryLensBottomAppBar
import com.inception.storylens.viewmodel.HomeViewModel

@Composable
fun HomeScreenRoute(
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val state by homeViewModel.homeState.collectAsState()
    HomeScreen(
        state = state,
        navController = navController
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    navController: NavController
) {

    Scaffold(
        bottomBar = {
            StoryLensBottomAppBar(
                navController = navController,
                onAddClick = {
                    navController.navigate("add_journal")
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.error}")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Halo, ${state.userName}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = state.greeting, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (state.latestJournal == null) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text("Selamat Datang di StoryLens!", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Anda belum memiliki jurnal. Tekan tombol '+' untuk membuat cerita pertama Anda.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        item {
                            Text(text = "Terakhir dibuat", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            LatestJournalCard(
                                journal = state.latestJournal,
                                onClick = { navController.navigate("view_journal/${state.latestJournal.id}") }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Jurnal harian anda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (state.recentJournals.isEmpty()) {
                            item {
                                Text("Tidak ada jurnal lainnya.", modifier = Modifier.padding(vertical = 16.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            items(state.recentJournals, key = { it.id }) { journal ->
                                RecentJournalItem(
                                    journal = journal,
                                    onClick = { navController.navigate("view_journal/${journal.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}