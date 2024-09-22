// ReminderReceiver.kt
package com.icy.yforgot_fin.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.icy.yforgot_fin.R

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminder = intent.getSerializableExtra(ReminderFragment.EXTRA_REMINDER) as? Reminder
        if (reminder != null) {
            showNotification(context, reminder)
        }

        //open app automatically
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        context.startActivity(launchIntent)
    }

    private fun showNotification(context: Context, reminder: Reminder) {
        val channelId = "reminder_channel"
        val notificationId = reminder.id

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.yforgotlogo)
            .setContentTitle(reminder.title)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentText(reminder.description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setAutoCancel(true)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    notificationId,
                    Intent(context, ViewReminderActivity::class.java).apply {
                        putExtra(ReminderFragment.EXTRA_REMINDER, reminder)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                R.drawable.ic_baseline_delete_24,
                "Dismiss",
                createDeleteIntent(context, notificationId)
            )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminder Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for reminders"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())

        // Play a custom alarm sound
        mediaPlayer = MediaPlayer.create(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        mediaPlayer?.start()

        // Stop sound when notification is dismissed
        notificationBuilder.setDeleteIntent(createDeleteIntent(context, notificationId))
    }

    private fun createDeleteIntent(context: Context, notificationId: Int): PendingIntent {
        val intent = Intent(context, NotificationDismissedReceiver::class.java).apply {
            putExtra("notification_id", notificationId)
        }
        return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}