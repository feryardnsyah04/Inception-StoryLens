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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.inception.storylens.model.JournalEntry
import com.inception.storylens.ui.components.StoryLensBottomAppBar
import com.inception.storylens.ui.theme.StoryLensTheme
import com.inception.storylens.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun HomeScreenRoute(homeViewModel: HomeViewModel) {
    val state by homeViewModel.homeState.collectAsState()
    HomeScreen(state = state)
}

@Composable
fun HomeScreen(state: HomeState) {
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            StoryLensBottomAppBar(
                selectedItemIndex = selectedItem,
                onItemSelected = { selectedItem = it },
                onAddClick = {
                    // TODO: Logika untuk menangani klik tombol '+'
                    //   println("Tombol Tambah diklik!")
                }
            )
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding), color = MaterialTheme.colorScheme.background){
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

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface,
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    item {
                        Text(text = "Terakhir dibuat", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        state.latestJournal?.let {
                            LatestJournalCard(journal = it)
                        } ?: Text("Belum ada jurnal dibuat.", modifier = Modifier.padding(vertical = 16.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface,
                            thickness = 1.dp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Jurnal harian anda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (state.recentJournals.isEmpty() && state.latestJournal == null) {
                        item { Text("Jurnal harian anda masih kosong.", modifier = Modifier.padding(vertical = 16.dp)) }
                    } else {
                        items(state.recentJournals) { journal ->
                            RecentJournalItem(journal = journal)
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
            AsyncImage(
                model = journal.imageUrl,
                contentDescription = journal.title,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
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
        AsyncImage(
            model = journal.imageUrl,
            contentDescription = journal.title,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = journal.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = formatDate(journal.timestamp), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

private fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
    return sdf.format(timestamp.toDate())
}

@Preview(showBackground = true, name = "Home Screen - Success State")
@Composable
fun HomeScreenSuccessPreview() {
    val fakeLatestJournal = JournalEntry(
        id = "1",
        title = "Tugas PBP",
        imageUrl = "", // URL placeholder
        timestamp = Timestamp.now()
    )
    val fakeRecentJournals = listOf(
        JournalEntry(id = "2", title = "Egestas sed nisi felis ut", timestamp = Timestamp.now()),
        JournalEntry(id = "3", title = "Quis senectus at", timestamp = Timestamp.now())
    )
    val fakeState = HomeState(
        isLoading = false,
        userName = "User01",
        greeting = "Selamat Pagi",
        latestJournal = fakeLatestJournal,
        recentJournals = fakeRecentJournals
    )
    StoryLensTheme {
        HomeScreen(state = fakeState)
    }
}

@Preview(showBackground = true, name = "Home Screen - Loading State")
@Composable
fun HomeScreenLoadingPreview() {
    val fakeState = HomeState(isLoading = true)
    StoryLensTheme {
        HomeScreen(state = fakeState)
    }
}