package com.icy.yforgot_fin.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.util.concurrent.TimeUnit

class TimerService : Service() {
    private val binder = LocalBinder()
    var isRunning = false
    private var elapsedTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var lastNotificationUpdateTime: Long = 0

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createNotificationChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NotificationUtil.NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    fun startTimer() {
        if (!isRunning) {
            isRunning = true
            val startTime = System.currentTimeMillis() - elapsedTime
            runnable = object : Runnable {
                override fun run() {
                    elapsedTime = System.currentTimeMillis() - startTime
                    updateNotificationIfNeeded()
                    handler.postDelayed(this, 10)
                }
            }
            handler.post(runnable)
            startForeground(NotificationUtil.NOTIFICATION_ID, createNotification())
        }
    }

    fun stopTimer() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(runnable)
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    fun resetTimer() {
        stopTimer()
        elapsedTime = 0
    }

    fun getElapsedTime(): Long = elapsedTime

    private fun updateNotificationIfNeeded() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNotificationUpdateTime >= 1000) {
            updateNotification()
            lastNotificationUpdateTime = currentTime
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        NotificationUtil.updateNotification(this, notification)
    }

    private fun createNotification(): android.app.Notification {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        return NotificationUtil.createNotification(
            this,
            "Timer Running",
            "Elapsed time: $timeString"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}