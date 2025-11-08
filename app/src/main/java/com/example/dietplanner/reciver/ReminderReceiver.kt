package com.example.dietplanner.com.example.dietplanner.reciver
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dietplanner.MainActivity
import com.example.dietplanner.R

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "diet_reminders"
        private const val CHANNEL_NAME = "Diet Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("REMINDER_ID", -1)
        val title = intent.getStringExtra("REMINDER_TITLE") ?: "Reminder"
        val emoji = intent.getStringExtra("REMINDER_EMOJI") ?: "🔔"
        val type = intent.getStringExtra("REMINDER_TYPE") ?: "CUSTOM"

        createNotificationChannel(context)
        showNotification(context, reminderId, emoji, title, type)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for meal times, hydration, and exercise"
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        context: Context,
        reminderId: Long,
        emoji: String,
        title: String,
        type: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = when (type) {
            "MEAL" -> "Time for your meal!"
            "HYDRATION" -> "Stay hydrated! Drink some water 💧"
            "EXERCISE" -> "Time to move! Your workout awaits 🏃"
            else -> "Don't forget!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.bell_01) // Create this drawable
            .setContentTitle("$emoji $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("ReminderReceiver", "❌ Notification permission not granted")
            return
        }
        NotificationManagerCompat.from(context).notify(reminderId.toInt(), notification)
    }
}