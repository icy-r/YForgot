package com.icy.yforgot_fin.task

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.icy.yforgot_fin.R

class TaskDetailsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TASK = "task"
        const val REQUEST_EDIT_TASK = 1
    }

    private lateinit var task: Task
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dueDateView: TextView
    private lateinit var deleteButton: Button
    private lateinit var editButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        // Get the task details from the intent
        task = intent.getSerializableExtra(EXTRA_TASK) as Task

        // Set system bar color
        window.statusBarColor = getColor(R.color.moderate_black)

        // Initialize the views
        titleTextView = findViewById(R.id.taskTitle)
        descriptionTextView = findViewById(R.id.taskDescription)
        dueDateView = findViewById(R.id.taskDueDate)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)

        updateTaskDetails()

        // Set click listener for deleting the task
        deleteButton.setOnClickListener {
            deleteTask(task)
            finish()
        }

        // Set click listener for editing the task
        editButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java).apply {
                putExtra(EXTRA_TASK, task)
            }
            startActivityForResult(intent, REQUEST_EDIT_TASK)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTaskDetails() {
        titleTextView.text = task.title
        descriptionTextView.text = task.description
        dueDateView.text = "${task.dueDate} ${task.dueTime}"
    }

    private fun deleteTask(task: Task) {
        val sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE)
        val gson = Gson()

        val tasksJson = sharedPreferences.getString("tasks", "[]")
        val taskListType = object : TypeToken<MutableList<Task>>() {}.type
        val tasks: MutableList<Task> = gson.fromJson(tasksJson, taskListType)

        tasks.remove(task)

        val updatedTasksJson = gson.toJson(tasks)
        sharedPreferences.edit()
            .putString("tasks", updatedTasksJson)
            .apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_TASK && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra(EXTRA_TASK)?.let {
                task = it as Task
                updateTaskDetails()
            }
        }
    }
}
