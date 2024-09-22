package com.icy.yforgot_fin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icy.yforgot_fin.reminder.Reminder
import com.icy.yforgot_fin.task.TasksFragment
import com.icy.yforgot_fin.reminder.ReminderFragment
import com.icy.yforgot_fin.reminder.ViewReminderActivity
import com.icy.yforgot_fin.timer.TimerFragment

private const val REQUEST_NOTIFICATION_PERMISSION = 100

class MainActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val tasksFragment = TasksFragment()
    private val timerFragment = TimerFragment()
    private val reminderFragment = ReminderFragment()
    private lateinit var currentFragment: Fragment
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Initialize the current fragment to home fragment
        currentFragment = homeFragment
        loadHome()

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        // Each navigation item click will load the corresponding fragment
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    currentFragment = homeFragment
                    loadHome()
                    true
                }

                R.id.tasks -> {
                    currentFragment = tasksFragment
                    loadTasks()
                    true
                }
                R.id.timer -> {
                    currentFragment = timerFragment
                    loadTimer()
                    true
                }
                R.id.reminder -> {
                    currentFragment = reminderFragment
                    loadReminder()
                    true
                }

                else -> false
            }
        }

        // Check notification permission on start
        checkNotificationPermission()
    }

    private fun loadReminder() {
        if (supportFragmentManager.findFragmentByTag(ReminderFragment::class.java.simpleName) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, reminderFragment, ReminderFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadTasks() {
        if (supportFragmentManager.findFragmentByTag(TasksFragment::class.java.simpleName) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, tasksFragment, TasksFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadHome() {
        if (supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment, HomeFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadTimer() {
        if (supportFragmentManager.findFragmentByTag(TimerFragment::class.java.simpleName) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, timerFragment, TimerFragment::class.java.simpleName)
                .commit()
        }
    }

    // Method to check and request notification permission if needed
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else {
                // Permission already granted
//                NotificationUtil().createNotification(this)
            }
        } else {
            // For Android versions below 13
//            NotificationUtil().createNotification(this)
        }
    }

    // Handle the permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}