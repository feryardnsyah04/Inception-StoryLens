package com.inception.storylens.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.inception.storylens.model.Task
import com.inception.storylens.repository.TaskRepository
import com.inception.storylens.ui.components.StoryLensBottomAppBar
import com.inception.storylens.viewmodel.CalendarViewModel
import com.inception.storylens.viewmodel.CalendarViewModelFactory
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    navController: NavController,
    // --- PERBAIKAN DI SINI: Hapus nilai default viewModel() ---
    // Sekarang CalendarScreen hanya menerima ViewModel yang sudah dibuat oleh AppNavHost
    viewModel: CalendarViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val calendarState = rememberSelectableCalendarState(
        initialSelection = listOf(uiState.selectedDate)
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            StoryLensBottomAppBar(
                navController = navController,
                onAddClick = { navController.navigate("add_journal") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // =============================================================
            // BAGIAN KALENDER (TIDAK ADA PERUBAHAN SESUAI INSTRUKSI)
            // =============================================================
            Text(
                text = calendarState.monthState.currentMonth.year.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 4.dp
            ) {
                SelectableCalendar(
                    calendarState = calendarState,
                    monthHeader = { monthState ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { coroutineScope.launch { monthState.currentMonth = monthState.currentMonth.minusMonths(1) } }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Bulan Sebelumnya")
                            }
                            Text(
                                text = monthState.currentMonth.month.getDisplayName(JavaTextStyle.FULL, Locale("id", "ID")).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { coroutineScope.launch { monthState.currentMonth = monthState.currentMonth.plusMonths(1) } }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Bulan Berikutnya")
                            }
                        }
                    },
                    dayContent = { dayState ->
                        val isSelected = calendarState.selectionState.isDateSelected(dayState.date)
                        val isCurrentMonth = dayState.isFromCurrentMonth
                        val isToday = dayState.date == LocalDate.now()
                        val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        val textColor = if (isSelected) Color.White else if (isCurrentMonth) Color.Black else Color.Gray

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .then(
                                    if (isToday && !isSelected) {
                                        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .size(40.dp)
                                .clickable {
                                    viewModel.onDateSelected(dayState.date)
                                    calendarState.selectionState.onDateSelected(dayState.date)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayState.date.dayOfMonth.toString(),
                                color = textColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    },
                    daysOfWeekHeader = { daysOfWeek ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            daysOfWeek.forEach { dayOfWeek ->
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = dayOfWeek.getDisplayName(JavaTextStyle.SHORT, Locale("id", "ID")).uppercase(),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                )
            }
            // =============================================================
            // AKHIR BAGIAN KALENDER
            // =============================================================

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tugas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- PERUBAHAN BAGIAN TUGAS DIMULAI DI SINI ---
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f), // Agar daftar tugas mengisi ruang
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(uiState.tasksForSelectedDate, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onSaveEdit = { newDescription ->
                                    viewModel.updateTaskDescription(task.id, newDescription)
                                },
                                onCheckedChange = { isChecked ->
                                    viewModel.onTaskCheckedChange(task, isChecked)
                                },
                                // Menambahkan fungsi hapus
                                onDelete = { viewModel.deleteTask(task.id) }
                            )
                        }
                    }
                    // Menambahkan bagian untuk input tugas baru
                    AddTaskSection(onAddTask = { description -> viewModel.addTask(description) })
                }
            }
        }
    }
}

// Komponen baru untuk input tugas
@Composable
fun AddTaskSection(onAddTask: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Tambah tugas baru...") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onAddTask(text)
                text = ""
                keyboardController?.hide()
            })
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                onAddTask(text)
                text = ""
            },
            enabled = text.isNotBlank()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
        }
    }
}

// Komponen TaskItem yang diperbarui
@Composable
fun TaskItem(
    task: Task,
    onSaveEdit: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit // Parameter baru untuk hapus
) {
    var text by remember(task.description) { mutableStateOf(task.description) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            textStyle = TextStyle(
                fontSize = 16.sp,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (task.isCompleted) Color.Gray else Color.Black
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f), // Modifier agar tombol hapus terdorong ke kanan
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSaveEdit(text)
                    keyboardController?.hide()
                }
            )
        )
        // Tombol Hapus untuk setiap item
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Close, contentDescription = "Hapus Tugas", tint = Color.Gray)
        }
    }
}