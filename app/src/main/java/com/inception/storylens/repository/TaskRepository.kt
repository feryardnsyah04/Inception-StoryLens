// Lokasi: app/src/main/java/com/inception/storylens/repository/TaskRepository.kt
package com.inception.storylens.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.inception.storylens.model.Task
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class TaskRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // Helper untuk mendapatkan ID pengguna yang sedang login
    private val userId: String
        get() = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("Pengguna tidak login")

    // Referensi ke koleksi 'tasks' milik pengguna
    private fun getTasksCollection() = firestore.collection("users").document(userId).collection("tasks")

    // Mengambil semua tugas untuk tanggal tertentu
    suspend fun getTasksForDate(date: LocalDate): List<Task> {
        return try {
            val snapshot = getTasksCollection()
                .whereEqualTo("date", date.toString()) // Query berdasarkan tanggal
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            // Jika terjadi error, kembalikan list kosong
            emptyList()
        }
    }

    // Menambah tugas baru ke tanggal tertentu
    suspend fun addTask(date: LocalDate, description: String): Result<Unit> {
        return try {
            val newTask = Task(description = description, isCompleted = false, date = date.toString())
            getTasksCollection().add(newTask).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Memperbarui tugas yang sudah ada
    suspend fun updateTask(updatedTask: Task): Result<Unit> {
        return try {
            getTasksCollection().document(updatedTask.id).set(updatedTask).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Menghapus tugas berdasarkan ID-nya
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            getTasksCollection().document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}