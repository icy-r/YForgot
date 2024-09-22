// TaskAdapter.kt
package com.icy.yforgot_fin.task

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.icy.yforgot_fin.R
import com.icy.yforgot_fin.databinding.ItemTaskBinding

class TaskAdapter(private val context: Context, private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.task = task // Bind the task object to the layout
        holder.binding.executePendingBindings() // Ensure the bindings are executed

        Log.d("TaskAdapter", "Task bound: $task")

        // Set the completed state
        updateItemBackground(holder, task.isCompleted)

        // OnClickListener for the task to open the task details
        holder.itemView.setOnClickListener {
            val intent = Intent(context, TaskDetailsActivity::class.java).apply {
                putExtra(TaskDetailsActivity.EXTRA_TASK, task)
            }
            context.startActivity(intent)
        }

        // OnClickListener for the checkbox
        holder.binding.taskStatus.setOnClickListener {
            task.isCompleted = holder.binding.taskStatus.isChecked
            updateItemBackground(holder, task.isCompleted)
        }
    }

    private fun updateItemBackground(holder: TaskViewHolder, isCompleted: Boolean) {
        val backgroundColor = if (isCompleted) {
            ContextCompat.getColor(context, R.color.moderate_white)
        } else {
            ContextCompat.getColor(context, R.color.moderate_black)
        }
        holder.binding.root.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount(): Int = tasks.size
}