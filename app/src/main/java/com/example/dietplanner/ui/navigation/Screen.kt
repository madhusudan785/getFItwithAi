package com.example.dietplanner.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Welcome : Screen("welcome")
    object Input : Screen("input")
    object DietPlan : Screen("diet_plan")
    object DietInDays : Screen("diet_in_days/{planId}") {
        fun createRoute(planId: Long) = "diet_in_days/$planId"
    }
    object Feedback : Screen("feedback")
    object Reminder : Screen("reminder")
}