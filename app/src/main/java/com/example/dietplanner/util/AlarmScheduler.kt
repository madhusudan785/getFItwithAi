package com.example.dietplanner.com.example.dietplanner.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.dietplanner.com.example.dietplanner.data.model.Reminder
import com.example.dietplanner.com.example.dietplanner.reciver.ReminderReceiver
import java.util.Calendar

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"

    fun scheduleReminder(context: Context, reminder: Reminder) {
        if (!reminder.isEnabled) {
            Log.d(TAG, "Reminder ${reminder.id} is disabled, skipping")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Parse time
        val timeParts = reminder.time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        // Schedule for each day of week
        reminder.daysOfWeek.forEach { dayOfWeek ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_WEEK, dayOfWeek)

                // If time has passed today, schedule for next week
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("REMINDER_ID", reminder.id)
                putExtra("REMINDER_TITLE", reminder.title)
                putExtra("REMINDER_EMOJI", reminder.emoji)
                putExtra("REMINDER_TYPE", reminder.type.name)
            }

            val requestCode = (reminder.id.toInt() * 10) + dayOfWeek
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule repeating alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7, // Weekly
                pendingIntent
            )

            Log.d(TAG, "Scheduled reminder ${reminder.id} for day $dayOfWeek at ${reminder.time}")
        }
    }

    fun cancelReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        reminder.daysOfWeek.forEach { dayOfWeek ->
            val intent = Intent(context, ReminderReceiver::class.java)
            val requestCode = (reminder.id.toInt() * 10) + dayOfWeek
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        Log.d(TAG, "Cancelled reminder ${reminder.id}")
    }
}