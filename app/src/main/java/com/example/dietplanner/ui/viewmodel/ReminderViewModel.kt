package com.example.dietplanner.com.example.dietplanner.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlannerDatabase
import com.example.dietplanner.com.example.dietplanner.data.model.DefaultReminders
import com.example.dietplanner.com.example.dietplanner.data.model.Reminder
import com.example.dietplanner.com.example.dietplanner.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ReminderViewModel"
    }

    private val database = DietPlannerDatabase.getDatabase(application)
    private val repository = ReminderRepository(
        reminderDao = database.reminderDao(),
        context = application.applicationContext
    )

    // All reminders
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    init {
        Log.d(TAG, "ReminderViewModel initialized")
        loadReminders()
    }

    /**
     * Load all reminders from database
     */
    private fun loadReminders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllReminders().collect { reminderList ->
                    _reminders.value = reminderList
                    Log.d(TAG, "Loaded ${reminderList.size} reminders")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading reminders", e)
                _error.value = "Failed to load reminders: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Add a new reminder
     */
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Adding reminder: ${reminder.title}")
                repository.addReminder(reminder)
                Log.i(TAG, "✅ Reminder added successfully: ${reminder.title}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error adding reminder", e)
                _error.value = "Failed to add reminder: ${e.message}"
            }
        }
    }

    /**
     * Update an existing reminder
     */
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Updating reminder: ${reminder.title}")
                repository.updateReminder(reminder)
                Log.i(TAG, "✅ Reminder updated successfully")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error updating reminder", e)
                _error.value = "Failed to update reminder: ${e.message}"
            }
        }
    }

    /**
     * Delete a reminder
     */
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Deleting reminder: ${reminder.title}")
                repository.deleteReminder(reminder)
                Log.i(TAG, "✅ Reminder deleted successfully")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error deleting reminder", e)
                _error.value = "Failed to delete reminder: ${e.message}"
            }
        }
    }

    /**
     * Toggle reminder enabled/disabled state
     */
    fun toggleReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                val action = if (reminder.isEnabled) "Disabling" else "Enabling"
                Log.d(TAG, "$action reminder: ${reminder.title}")

                repository.toggleReminder(reminder)

                Log.i(TAG, "✅ Reminder ${if (!reminder.isEnabled) "enabled" else "disabled"}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error toggling reminder", e)
                _error.value = "Failed to toggle reminder: ${e.message}"
            }
        }
    }

    /**
     * Load default reminders (reset to defaults)
     */
    fun loadDefaultReminders() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading default reminders")
                _isLoading.value = true

                // Delete all existing reminders first
                _reminders.value.forEach { reminder ->
                    repository.deleteReminder(reminder)
                }

                // Add default reminders
                val defaults = DefaultReminders.getDefaults()
                defaults.forEach { reminder ->
                    repository.addReminder(reminder)
                }

                Log.i(TAG, "✅ Loaded ${defaults.size} default reminders")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading default reminders", e)
                _error.value = "Failed to load defaults: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Enable all reminders
     */
    fun enableAllReminders() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Enabling all reminders")
                _reminders.value.forEach { reminder ->
                    if (!reminder.isEnabled) {
                        repository.toggleReminder(reminder)
                    }
                }
                Log.i(TAG, "✅ All reminders enabled")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error enabling all reminders", e)
                _error.value = "Failed to enable all: ${e.message}"
            }
        }
    }

    /**
     * Disable all reminders
     */
    fun disableAllReminders() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Disabling all reminders")
                _reminders.value.forEach { reminder ->
                    if (reminder.isEnabled) {
                        repository.toggleReminder(reminder)
                    }
                }
                Log.i(TAG, "✅ All reminders disabled")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error disabling all reminders", e)
                _error.value = "Failed to disable all: ${e.message}"
            }
        }
    }

    /**
     * Get reminders by type
     */
    fun getRemindersByType(type: String): List<Reminder> {
        return _reminders.value.filter { it.type.name == type }
    }

    /**
     * Get active (enabled) reminders count
     */
    fun getActiveRemindersCount(): Int {
        return _reminders.value.count { it.isEnabled }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Check if reminders are empty (first time setup)
     */
    fun isFirstTimeSetup(): Boolean {
        return _reminders.value.isEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ReminderViewModel cleared")
    }
}