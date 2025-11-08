package com.example.dietplanner.com.example.dietplanner.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val time: String, // Format: "HH:mm"
    val emoji: String,
    val type: ReminderType,
    val isEnabled: Boolean = true,
    val daysOfWeek: String = "1,2,3,4,5,6,7", // Comma-separated: 1=Monday, 7=Sunday
    val createdAt: Long = System.currentTimeMillis()
)

enum class ReminderType {
    MEAL,
    HYDRATION,
    EXERCISE,
    CUSTOM
}

data class Reminder(
    val id: Long = 0,
    val title: String,
    val time: String,
    val emoji: String,
    val type: ReminderType,
    val isEnabled: Boolean = true,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7)
) {
    fun toEntity(): ReminderEntity {
        return ReminderEntity(
            id = id,
            title = title,
            time = time,
            emoji = emoji,
            type = type,
            isEnabled = isEnabled,
            daysOfWeek = daysOfWeek.joinToString(",")
        )
    }
}

fun ReminderEntity.toReminder(): Reminder {
    return Reminder(
        id = id,
        title = title,
        time = time,
        emoji = emoji,
        type = type,
        isEnabled = isEnabled,
        daysOfWeek = daysOfWeek.split(",").mapNotNull { it.toIntOrNull() }
    )
}