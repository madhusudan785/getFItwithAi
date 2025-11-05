package com.example.dietplanner.data.model

data class UserProfile(
    val height: Float,
    val weight: Float,
    val age: Int,
    val gender: String,
    val likes: List<String>,
    val dislikes: List<String>,
    val dietaryRestrictions: String = "",
    val activityLevel: String = "Moderate"
)

data class DietRequest(
    val height: Float,
    val weight: Float,
    val age: Int,
    val gender: String,
    val likes: List<String>,
    val dislikes: List<String>,
    val dietaryRestrictions: String,
    val activityLevel: String,
    val planType: String = "weekly"
)

sealed class DietPlanState {
    object Idle : DietPlanState()
    object Loading : DietPlanState()
    data class Streaming(val content: String) : DietPlanState()
    data class Success(val content: String) : DietPlanState()
    data class Error(val message: String) : DietPlanState()
}