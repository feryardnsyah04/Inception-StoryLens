package com.inception.storylens.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.inception.storylens.model.JournalEntry
import com.inception.storylens.ui.components.StoryLensBottomAppBar
import com.inception.storylens.utils.formatDate
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedItemIndex = remember(currentRoute) {
        when (currentRoute) {
            "home" -> 0
            "journal" -> 1
            "calendar" -> 2
            "profile" -> 3
            else -> 0
        }
    }

    Scaffold(
        bottomBar = {
            StoryLensBottomAppBar(
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { index ->
                    val destinationRoute = when (index) {
                        0 -> "home"
                        1 -> "journal"
                        2 -> "calendar"
                        3 -> "profile"
                        else -> "home"
                    }

                    if (currentRoute != destinationRoute) {
                        navController.navigate(destinationRoute) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
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
                                Text(
                                    "Selamat Datang di StoryLens!",
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Anda belum memiliki jurnal. Tekan tombol '+' untuk membuat cerita pertama Anda.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        item {
                            Text(text = "Terakhir dibuat", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            LatestJournalCard(journal = state.latestJournal)
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Jurnal harian anda", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (state.recentJournals.isEmpty()) {
                            item {
                                Text(
                                    "Tidak ada jurnal lainnya.",
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(state.recentJournals, key = { it.id }) { journal ->
                                RecentJournalItem(journal = journal)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LatestJournalCard(journal: JournalEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            AsyncImage(model = journal.imageUrl, contentDescription = journal.title, modifier = Modifier.fillMaxWidth().height(180.dp), contentScale = ContentScale.Crop)
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = journal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = formatDate(journal.timestamp), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun RecentJournalItem(journal: JournalEntry) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(model = journal.imageUrl, contentDescription = journal.title, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = journal.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = formatDate(journal.timestamp), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}
