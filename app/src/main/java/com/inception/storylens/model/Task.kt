package com.inception.storylens.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Task(
    @get:Exclude var id: String = "",
    val description: String = "",

    @get:PropertyName("isCompleted")
    val isCompleted: Boolean = false,

    val date: String = ""
)