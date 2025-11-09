package com.example.dietplanner.com.example.dietplanner.reciver

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dietplanner.MainActivity
import com.example.dietplanner.R
import com.example.dietplanner.com.example.dietplanner.data.model.Reminder
import com.example.dietplanner.com.example.dietplanner.data.model.ReminderType
import com.example.dietplanner.ui.theme.DietPlannerTheme

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
        private const val CH_MEAL = "diet_meal"
        private const val CH_WATER = "diet_water"
        private const val CH_EXERCISE = "diet_exercise"
        private const val CH_DEFAULT = "diet_default"

        private fun soundUriForType(context: Context, type: String): Uri {
            val pkg = context.packageName
            return Uri.parse("android.resource://$pkg/raw/diet_reminder")
        }

        private fun channelIdForType(type: String) = when (type) {
            "MEAL" -> CH_MEAL
            "HYDRATION" -> CH_WATER
            "EXERCISE" -> CH_EXERCISE
            else -> CH_DEFAULT
        }

        private fun channelNameForType(type: String) = when (type) {
            "MEAL" -> "Meal Reminders"
            "HYDRATION" -> "Hydration Reminders"
            "EXERCISE" -> "Exercise Reminders"
            else -> "Diet Reminders"
        }

        /**
         * Creates a channel once if it doesn't exist. Never deletes it at runtime.
         */
        private fun ensureChannel(context: Context, type: String) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

            val id = channelIdForType(type)
            val name = channelNameForType(type)
            val sound = soundUriForType(context, type)
            val mgr = context.getSystemService(NotificationManager::class.java)
            val existing = mgr.getNotificationChannel(id)

            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            if (existing != null) {
                if (existing.sound == null) {
                    Log.w("ReminderReceiver", "⚠️ Channel '$id' has null sound, deleting to recreate.")
                    mgr.deleteNotificationChannel(id)
                } else {
                    Log.d("ReminderReceiver", "✅ Channel '$id' already has valid sound ${existing.sound}")
                    return
                }
            }

            val ch = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for $name"
                setSound(sound, attrs)
                enableVibration(true)
                enableLights(true)
                lightColor = Color.GREEN
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            mgr.createNotificationChannel(ch)

            // Re-verify the sound after creation
            val recheck = mgr.getNotificationChannel(id)
            if (recheck?.sound == null) {
                Log.e("ReminderReceiver", "❌ Channel '$id' still missing sound! Realme may override.")
            } else {
                Log.i("ReminderReceiver", "🔔 Channel '$id' successfully created with sound: $sound")
            }
        }

        fun showSetupConfirmation(context: Context) {
                val channelId = "diet_default"
                ensureChannel(context, "DEFAULT")

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    9999, // unique dummy ID
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val soundUri = Uri.parse("android.resource://${context.packageName}/raw/diet_reminder")

                val builder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.bell_01)
                    .setContentTitle("🎯 Reminder Set Successfully")
                    .setContentText("Your reminders are now active — stay consistent and healthy! 🥗")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_STATUS)
                    .setContentIntent(pendingIntent)
                    .setVibrate(longArrayOf(0, 300, 200, 300))
                    .setSound(soundUri)

                val nm = NotificationManagerCompat.from(context)
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    nm.notify(9999, builder.build())
                }
            }


    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("REMINDER_ID", -1)
        val title = intent.getStringExtra("REMINDER_TITLE") ?: "Reminder"
        val emoji = intent.getStringExtra("REMINDER_EMOJI") ?: "🔔"
        val type = intent.getStringExtra("REMINDER_TYPE") ?: "CUSTOM"
        showNotification(context, reminderId, emoji, title, type)
    }

    private fun showNotification(
        context: Context,
        reminderId: Long,
        emoji: String,
        title: String,
        type: String
    ) {
        val toMain = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            toMain,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = when (type) {
            "MEAL" -> "Time for your meal! 🍱"
            "HYDRATION" -> "Stay hydrated! 💧"
            "EXERCISE" -> "Time to move! 🏋️"
            else -> "Don't forget!"
        }

        val soundUri = soundUriForType(context, type)
        val channelId = channelIdForType(type)
        ensureChannel(context, type)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bell_01)
            .setContentTitle("$emoji $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(false) // ✅ ensures notification stays visible until tapped
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setSound(soundUri)
        }

        val nm = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return

        nm.notify(reminderId.toInt(), builder.build())
    }


}





