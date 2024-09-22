// TasksFragment.kt
package com.icy.yforgot_fin.task

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log
import com.icy.yforgot_fin.R

class TasksFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter: TaskAdapter? = null
    private var fab: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tasks, container, false)

        fab = rootView.findViewById(R.id.fab)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        loadTasks()

        fab?.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        val sharedPreferences = requireActivity().getSharedPreferences("tasks", MODE_PRIVATE)
        val gson = Gson()
        val tasksJson = sharedPreferences.getString("tasks", "[]")
        val type = object : TypeToken<List<Task>>() {}.type
        val tasks: List<Task>? = gson.fromJson(tasksJson, type)

        if (tasks == null) {
            Log.e("TasksFragment", "Failed to load tasks: tasks is null")
            return
        }

        Log.d("TasksFragment", "Tasks loaded: $tasks")

        adapter = TaskAdapter(requireContext(), tasks.toMutableList())
        recyclerView?.adapter = adapter
    }
}