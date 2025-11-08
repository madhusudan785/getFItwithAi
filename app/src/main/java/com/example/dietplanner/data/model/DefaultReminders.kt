package com.example.dietplanner.com.example.dietplanner.data.model

object DefaultReminders {

    fun getDefaults(): List<Reminder> = listOf(
        Reminder(
            title = "Morning Water",
            time = "07:00",
            emoji = "💧",
            type = ReminderType.HYDRATION,
            isEnabled = true
        ),
        Reminder(
            title = "Breakfast Time",
            time = "08:00",
            emoji = "🍳",
            type = ReminderType.MEAL,
            isEnabled = true
        ),
        Reminder(
            title = "Mid-Morning Snack",
            time = "10:30",
            emoji = "🍎",
            type = ReminderType.MEAL,
            isEnabled = false
        ),
        Reminder(
            title = "Lunch Time",
            time = "13:00",
            emoji = "🍱",
            type = ReminderType.MEAL,
            isEnabled = true
        ),
        Reminder(
            title = "Afternoon Water",
            time = "15:00",
            emoji = "💧",
            type = ReminderType.HYDRATION,
            isEnabled = true
        ),
        Reminder(
            title = "Evening Snack",
            time = "17:00",
            emoji = "🥤",
            type = ReminderType.MEAL,
            isEnabled = false
        ),
        Reminder(
            title = "Evening Exercise",
            time = "18:00",
            emoji = "🏃",
            type = ReminderType.EXERCISE,
            isEnabled = true
        ),
        Reminder(
            title = "Dinner Time",
            time = "20:00",
            emoji = "🍽️",
            type = ReminderType.MEAL,
            isEnabled = true
        ),
        Reminder(
            title = "Night Water",
            time = "22:00",
            emoji = "💧",
            type = ReminderType.HYDRATION,
            isEnabled = true
        )
    )
}