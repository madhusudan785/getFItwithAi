package com.example.dietplanner.com.example.dietplanner.data.local.database

import androidx.room.*
import com.example.dietplanner.com.example.dietplanner.data.model.ReminderEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders ORDER BY time ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY time ASC")
    fun getEnabledReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): ReminderEntity?

    @Query("UPDATE reminders SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleReminder(id: Long, enabled: Boolean)
}
