package com.example.dietplanner.com.example.dietplanner.data.repository

import android.content.Context
import com.example.dietplanner.com.example.dietplanner.data.local.database.ReminderDao
import com.example.dietplanner.com.example.dietplanner.data.model.Reminder
import com.example.dietplanner.com.example.dietplanner.data.model.toReminder
import com.example.dietplanner.com.example.dietplanner.util.AlarmScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val context: Context
) {

    fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { entities ->
            entities.map { it.toReminder() }
        }
    }

    suspend fun addReminder(reminder: Reminder) {
        val id = reminderDao.insertReminder(reminder.toEntity())
        val savedReminder = reminder.copy(id = id)
        AlarmScheduler.scheduleReminder(context, savedReminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder.toEntity())
        AlarmScheduler.cancelReminder(context, reminder)
        if (reminder.isEnabled) {
            AlarmScheduler.scheduleReminder(context, reminder)
        }
    }

    suspend fun deleteReminder(reminder: Reminder) {
        AlarmScheduler.cancelReminder(context, reminder)
        reminderDao.deleteReminder(reminder.toEntity())
    }

    suspend fun toggleReminder(reminder: Reminder) {
        val newState = !reminder.isEnabled
        reminderDao.toggleReminder(reminder.id, newState)

        if (newState) {
            AlarmScheduler.scheduleReminder(context, reminder.copy(isEnabled = true))
        } else {
            AlarmScheduler.cancelReminder(context, reminder)
        }
    }
}