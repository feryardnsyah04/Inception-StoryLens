// Lokasi: app/src/main/java/com/inception/storylens/model/Task.kt
package com.inception.storylens.model

import com.google.firebase.firestore.Exclude

data class Task(
    @get:Exclude var id: String = "", // ID akan diambil dari ID dokumen Firestore
    val description: String = "",
    val isCompleted: Boolean = false,
    val date: String = "" // Untuk menyimpan tanggal tugas (format YYYY-MM-DD)
)