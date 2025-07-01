package com.inception.storylens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.inception.storylens.model.JournalEntry

@Composable
fun JournalPage(
    entries: List<JournalEntry>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onEdit: (JournalEntry) -> Unit,
    onDelete: (JournalEntry) -> Unit,
    onView: (JournalEntry) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE3F2FD))
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Cari") },
            shape = RoundedCornerShape(50),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
            singleLine = true
        )
        Text(
            text = "Semua jurnal harian anda",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            items(entries, key = { it.id }) { entry ->
                JournalItem(
                    entry = entry,
                    onEdit = { onEdit(entry) },
                    onDelete = { onDelete(entry) },
                    onClick = { onView(entry) }
                )
            }
        }
    }
}