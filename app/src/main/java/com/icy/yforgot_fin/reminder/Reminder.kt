package com.icy.yforgot_fin.reminder

import java.io.Serializable

data class Reminder(
    val id: Int,
    val title: String,
    val description: String,
    val dateTime: Long,
    val isActive: Boolean
) : Serializable