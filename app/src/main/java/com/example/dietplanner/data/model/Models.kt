package com.example.dietplanner.data.model


data class ParsedDietPlan(
    val planType: String,
    val caloriesTarget: Int,
    val notes: String,
    val days: List<DayPlan>
) {
    data class DayPlan(
        val day: String,
        val exercise: String,
        val hydration: String,
        val meals: Meals
    )

    data class Meals(
        val breakfast: String,
        val snack1: String,
        val lunch: String,
        val snack2: String,
        val dinner: String
    )
}

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