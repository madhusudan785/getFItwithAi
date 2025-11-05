package com.example.dietplanner.com.example.dietplanner.data.repository

import android.util.Log
import com.example.dietplanner.com.example.dietplanner.data.local.database.DayPlanEntity
import com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlanDao
import com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlanEntity
import com.example.dietplanner.com.example.dietplanner.util.TextCleanupUtil
import com.example.dietplanner.data.model.UserProfile

import kotlinx.coroutines.flow.Flow

class DietPlanLocalRepository(private val dao: DietPlanDao) {

    companion object {
        private const val TAG = "DietPlanLocalRepo"
    }

    /**
     * Saves a diet plan with cleaned content and day-wise breakdown
     */
    suspend fun saveDietPlan(
        name: String,
        rawContent: String,
        planType: String,
        userProfile: UserProfile
    ): Long {
        Log.d(TAG, "Saving diet plan: $name")

        // Clean the content
        val cleanedContent = TextCleanupUtil.cleanDietPlanText(rawContent)

        // Create diet plan entity
        val dietPlan = DietPlanEntity(
            name = name,
            content = rawContent,
            cleanedContent = cleanedContent,
            planType = planType,
            userHeight = userProfile.height,
            userWeight = userProfile.weight,
            userAge = userProfile.age,
            userGender = userProfile.gender
        )

        // Insert and get the ID
        val planId = dao.insertDietPlan(dietPlan)
        Log.d(TAG, "Diet plan saved with ID: $planId")

        // Extract and save day plans
        val dayPlans = extractAndCreateDayPlans(planId, cleanedContent)
        if (dayPlans.isNotEmpty()) {
            dao.insertDayPlans(dayPlans)
            Log.d(TAG, "Saved ${dayPlans.size} day plans")
        }

        return planId
    }

    private fun extractAndCreateDayPlans(dietPlanId: Long, cleanedContent: String): List<DayPlanEntity> {
        val dayPlansMap = TextCleanupUtil.extractDayPlans(cleanedContent)
        val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        return dayPlansMap.entries.mapIndexed { index, entry ->
            val dayPlan = TextCleanupUtil.parseDayContent(entry.value)

            DayPlanEntity(
                dietPlanId = dietPlanId,
                dayName = entry.key,
                dayNumber = dayNames.indexOf(entry.key) + 1,
                breakfast = dayPlan.breakfast,
                lunch = dayPlan.lunch,
                dinner = dayPlan.dinner,
                snacks = dayPlan.snacks,
                exercise = dayPlan.exercise,
                hydration = dayPlan.hydration
            )
        }
    }

    fun getAllDietPlans(): Flow<List<DietPlanEntity>> = dao.getAllDietPlans()

    suspend fun getDietPlanById(planId: Long): DietPlanEntity? = dao.getDietPlanById(planId)

    fun getDietPlanByIdFlow(planId: Long): Flow<DietPlanEntity?> = dao.getDietPlanByIdFlow(planId)

    fun getDayPlansForDiet(planId: Long): Flow<List<DayPlanEntity>> = dao.getDayPlansForDiet(planId)

    fun getFavoritePlans(): Flow<List<DietPlanEntity>> = dao.getFavoritePlans()

    suspend fun toggleFavorite(plan: DietPlanEntity) {
        val updated = plan.copy(isFavorite = !plan.isFavorite)
        dao.updateDietPlan(updated)
        Log.d(TAG, "Toggled favorite for plan ${plan.id}")
    }

    suspend fun deleteDietPlan(plan: DietPlanEntity) {
        dao.deleteDayPlansForDiet(plan.id)
        dao.deleteDietPlan(plan)
        Log.d(TAG, "Deleted diet plan ${plan.id}")
    }

    suspend fun getDietPlansCount(): Int = dao.getDietPlansCount()
}