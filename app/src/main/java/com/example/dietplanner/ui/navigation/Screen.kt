package com.example.dietplanner.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Welcome : Screen("welcome")
    object Input : Screen("input")
    object DietPlan : Screen("diet_plan")
    object DietInDays : Screen("dietInDays/{planId}") {
        fun createRoute(planId: Long) = "dietInDays/$planId"
    }
    object Feedback : Screen("feedback/{planId}") {
        fun createRoute(planId: Long) = "feedback/$planId"
    }

    object Reminder : Screen("reminder")
}