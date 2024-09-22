package com.icy.yforgot_fin.task

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.icy.yforgot_fin.R
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var showDatePickerButton: Button
    private lateinit var showTimePickerButton: Button
    private var taskToEdit: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        titleEditText = findViewById(R.id.titleEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveButton = findViewById(R.id.saveButton)
        showDatePickerButton = findViewById(R.id.showDatePickerButton)
        showTimePickerButton = findViewById(R.id.showTimePickerButton)

        // Check if we're editing an existing task
        taskToEdit = intent.getSerializableExtra(TaskDetailsActivity.EXTRA_TASK) as? Task
        if (taskToEdit != null) {
            // Populate fields with existing task data
            titleEditText.setText(taskToEdit?.title)
            descriptionEditText.setText(taskToEdit?.description)
            showDatePickerButton.text = taskToEdit?.dueDate
            showTimePickerButton.text = taskToEdit?.dueTime
        }

        setupDatePicker()
        setupTimePicker()

        // Set system bar color
        window.statusBarColor = getColor(R.color.black)

        // Set click listener for saving the task
        saveButton.setOnClickListener {
            saveTask()
        }
    }

    private fun setupDatePicker() {
        showDatePickerButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                showDatePickerButton.text = selectedDate
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun setupTimePicker() {
        showTimePickerButton.setOnClickListener {
            val timer = TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                showTimePickerButton.text = selectedTime
            }, 12, 0, false)

            timer.show()
        }
    }

    private fun saveTask() {
    val title = titleEditText.text.toString()
    val description = descriptionEditText.text.toString()
    val dueDate = showDatePickerButton.text.toString()
    val dueTime = showTimePickerButton.text.toString()

    val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
    val gson = Gson()

    val tasksJson = sharedPreferences.getString("tasks", "[]")
    val taskListType = object : TypeToken<MutableList<Task>>() {}.type
    val tasks: MutableList<Task> = gson.fromJson(tasksJson, taskListType)

    if (taskToEdit != null) {
        // Update existing task
        val updatedTask = taskToEdit?.copy(
            title = title,
            description = description,
            dueDate = dueDate,
            dueTime = dueTime
        )
        val index = tasks.indexOfFirst { it.id == taskToEdit?.id }
        if (index != -1) {
            tasks[index] = updatedTask!!
        }
        taskToEdit = updatedTask
    } else {
        // Create new task
        val lastTaskId = sharedPreferences.getInt("lastTaskId", 0)
        val newTaskId = lastTaskId + 1
        val newTask = Task(newTaskId, title, description, false, dueDate, dueTime)
        tasks.add(newTask)
        sharedPreferences.edit().putInt("lastTaskId", newTaskId).apply()
        taskToEdit = newTask
    }

    // Save the updated task list
    val updatedTasksJson = gson.toJson(tasks)
    sharedPreferences.edit().putString("tasks", updatedTasksJson).apply()

    // Return the task to the calling activity
    val resultIntent = Intent().apply {
        putExtra(TaskDetailsActivity.EXTRA_TASK, taskToEdit)
    }
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
}
}