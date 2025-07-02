// Lokasi: app/src/main/java/com/inception/storylens/viewmodel/CalendarViewModel.kt
package com.inception.storylens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inception.storylens.model.Task
import com.inception.storylens.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val tasksForSelectedDate: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CalendarViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        onDateSelected(LocalDate.now())
    }

    fun onDateSelected(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDate = date) }
            val tasks = taskRepository.getTasksForDate(date)
            _uiState.update { it.copy(tasksForSelectedDate = tasks, isLoading = false) }
        }
    }

    fun onTaskCheckedChange(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = isChecked)
            taskRepository.updateTask(updatedTask).onSuccess {
                onDateSelected(_uiState.value.selectedDate) // Refresh list
            }
        }
    }

    fun updateTaskDescription(taskId: String, newDescription: String) {
        val taskToUpdate = _uiState.value.tasksForSelectedDate.find { it.id == taskId }
        if (taskToUpdate != null && newDescription.isNotBlank()) {
            viewModelScope.launch {
                val updatedTask = taskToUpdate.copy(description = newDescription)
                taskRepository.updateTask(updatedTask).onSuccess {
                    onDateSelected(_uiState.value.selectedDate) // Refresh list
                }
            }
        }
    }

    fun addTask(description: String) {
        if (description.isNotBlank()) {
            viewModelScope.launch {
                taskRepository.addTask(_uiState.value.selectedDate, description).onSuccess {
                    onDateSelected(_uiState.value.selectedDate) // Refresh list
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId).onSuccess {
                onDateSelected(_uiState.value.selectedDate) // Refresh list
            }
        }
    }
}