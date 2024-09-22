package com.icy.yforgot_fin.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.icy.yforgot_fin.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ViewReminderActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var reminder: Reminder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reminder)

        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        dateTimeTextView = findViewById(R.id.dateTimeTextView)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)

        reminder = intent.getSerializableExtra(ReminderFragment.EXTRA_REMINDER) as Reminder

        titleTextView.text = reminder.title
        descriptionTextView.text = reminder.description
        dateTimeTextView.text = formatDateTime(reminder.dateTime)

        editButton.setOnClickListener {
            val intent = Intent(this, AddEditReminderActivity::class.java)
            intent.putExtra(ReminderFragment.EXTRA_REMINDER, reminder)
            startActivity(intent)
            finish()
        }

        deleteButton.setOnClickListener {
            deleteReminder()
            finish()
        }
    }

    private fun formatDateTime(dateTime: Long): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(dateTime))
    }

    private fun deleteReminder() {
        val sharedPreferences = getSharedPreferences("reminders", MODE_PRIVATE)
        val gson = Gson()
        val remindersJson = sharedPreferences.getString("reminders", "[]")
        val type = object : TypeToken<MutableList<Reminder>>() {}.type
        val reminders: MutableList<Reminder> = gson.fromJson(remindersJson, type)

        reminders.removeAll { it.id == reminder.id }

        val updatedRemindersJson = gson.toJson(reminders)
        sharedPreferences.edit().putString("reminders", updatedRemindersJson).apply()

        cancelReminder(reminder)
    }

    private fun cancelReminder(reminder: Reminder) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}