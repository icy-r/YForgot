package com.icy.yforgot_fin.reminder

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.icy.yforgot_fin.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditReminderActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var saveButton: Button
    private var dateTime: Calendar = Calendar.getInstance()
    private var reminderId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_reminder)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateButton = findViewById(R.id.dateButton)
        timeButton = findViewById(R.id.timeButton)
        saveButton = findViewById(R.id.saveButton)

        val reminder = intent.getSerializableExtra(ReminderFragment.EXTRA_REMINDER) as? Reminder
        if (reminder != null) {
            reminderId = reminder.id
            titleEditText.setText(reminder.title)
            descriptionEditText.setText(reminder.description)
            dateTime.timeInMillis = reminder.dateTime
            updateDateTimeButtons()
        }

        dateButton.setOnClickListener { showDatePicker() }
        timeButton.setOnClickListener { showTimePicker() }
        saveButton.setOnClickListener { saveReminder() }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                dateTime.set(year, month, dayOfMonth)
                updateDateTimeButtons()
            },
            dateTime.get(Calendar.YEAR),
            dateTime.get(Calendar.MONTH),
            dateTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dateTime.set(Calendar.MINUTE, minute)
                updateDateTimeButtons()
            },
            dateTime.get(Calendar.HOUR_OF_DAY),
            dateTime.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun updateDateTimeButtons() {
        dateButton.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dateTime.time)
        timeButton.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateTime.time)
    }

    private fun saveReminder() {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()

        if (title.isBlank()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("reminders", MODE_PRIVATE)
        val gson = Gson()
        val remindersJson = sharedPreferences.getString("reminders", "[]")
        val type = object : TypeToken<MutableList<Reminder>>() {}.type
        val reminders: MutableList<Reminder> = gson.fromJson(remindersJson, type)

        if (reminderId == -1) {
            reminderId = (reminders.maxByOrNull { it.id }?.id ?: 0) + 1
        }

        val newReminder = Reminder(reminderId, title, description, dateTime.timeInMillis, true)

        val existingIndex = reminders.indexOfFirst { it.id == reminderId }
        if (existingIndex != -1) {
            reminders[existingIndex] = newReminder
        } else {
            reminders.add(newReminder)
        }

        val updatedRemindersJson = gson.toJson(reminders)
        sharedPreferences.edit().putString("reminders", updatedRemindersJson).apply()

        scheduleReminder(newReminder)

        finish()
    }

private fun scheduleReminder(reminder: Reminder) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, ReminderReceiver::class.java).apply {
        putExtra(ReminderFragment.EXTRA_REMINDER, reminder)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        reminder.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.dateTime,
                    pendingIntent
                )
            } else {
                // Request the SCHEDULE_EXACT_ALARM permission
                val intent2 = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent2)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.dateTime,
                pendingIntent
            )
        }
    } catch (e: SecurityException) {
        // Handle the exception gracefully
        Toast.makeText(this, "Exact alarm scheduling permission not granted", Toast.LENGTH_SHORT).show()
    }
}}
