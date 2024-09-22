package com.icy.yforgot_fin.task

import java.io.Serializable

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    var isCompleted: Boolean,
    val dueDate: String,
    val dueTime: String
) : Serializable
