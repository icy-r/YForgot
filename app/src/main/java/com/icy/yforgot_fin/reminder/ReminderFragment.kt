package com.icy.yforgot_fin.reminder

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.icy.yforgot_fin.R

class ReminderFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReminderAdapter
    private lateinit var addReminderFab: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)
        recyclerView = view.findViewById(R.id.reminderRecyclerView)
        addReminderFab = view.findViewById(R.id.addReminderFab)

        setupRecyclerView()
        setupAddReminderFab()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    private fun setupRecyclerView() {
        adapter = ReminderAdapter(emptyList()) { reminder ->
            val intent = Intent(requireContext(), ViewReminderActivity::class.java)
            intent.putExtra(EXTRA_REMINDER, reminder)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupAddReminderFab() {
        addReminderFab.setOnClickListener {
            val intent = Intent(requireContext(), AddEditReminderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadReminders() {
        val sharedPreferences = requireActivity().getSharedPreferences("reminders", MODE_PRIVATE)
        val gson = Gson()
        val remindersJson = sharedPreferences.getString("reminders", "[]")
        val type = object : TypeToken<List<Reminder>>() {}.type
        val reminders: List<Reminder> = gson.fromJson(remindersJson, type)
        adapter.updateReminders(reminders)
    }

    companion object {
        const val EXTRA_REMINDER = "extra_reminder"
    }
}