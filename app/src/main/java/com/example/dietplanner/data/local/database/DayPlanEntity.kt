package com.example.dietplanner.com.example.dietplanner.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_plans")
data class DayPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dietPlanId: Long, // Foreign key to DietPlanEntity
    val dayName: String, // Monday, Tuesday, etc.
    val dayNumber: Int, // 1-7
    val breakfast: String = "",
    val lunch: String = "",//differentiates between breakfast and lunch
    val dinner: String = "",//dd
    val snacks: String = "",
    val exercise: String = "",
    val hydration: String = "",
    val notes: String = ""
)