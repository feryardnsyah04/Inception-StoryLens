package com.inception.storylens.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
    return sdf.format(timestamp.toDate())
}