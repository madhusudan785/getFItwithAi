package com.example.dietplanner.com.example.dietplanner.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "diet_plans")
data class DietPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val content: String,
    val cleanedContent: String,
    val createdAt: Long = System.currentTimeMillis(),
    val planType: String = "weekly",
    val userHeight: Float = 0f,
    val userWeight: Float = 0f,
    val userAge: Int = 0,
    val userGender: String = "",
    val isFavorite: Boolean = false
)