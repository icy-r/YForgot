package com.icy.yforgot_fin.reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.icy.yforgot_fin.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderAdapter(
    private var reminders: List<Reminder>,
    private val onItemClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.reminderTitleTextView)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.reminderDateTimeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.titleTextView.text = reminder.title
        holder.dateTimeTextView.text = formatDateTime(reminder.dateTime)
        holder.itemView.setOnClickListener { onItemClick(reminder) }
    }

    override fun getItemCount() = reminders.size

    fun updateReminders(newReminders: List<Reminder>) {
        reminders = newReminders
        notifyDataSetChanged()
    }

    private fun formatDateTime(dateTime: Long): String {
        // Implement date formatting logic here
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(dateTime))
    }
}

