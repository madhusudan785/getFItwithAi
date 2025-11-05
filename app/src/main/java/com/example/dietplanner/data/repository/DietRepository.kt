package com.example.dietplanner.data.repository

import com.example.dietplanner.data.model.DietRequest
import com.example.dietplanner.data.model.UserProfile
import com.example.dietplanner.data.network.SSEManager
import com.example.dietplanner.data.network.StreamEvent
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class DietRepository(
    private val sseManager: SSEManager,
    private val gson: Gson
) {

    fun generateDietPlan(userProfile: UserProfile): Flow<StreamEvent> {
        val request = DietRequest(
            height = userProfile.height,
            weight = userProfile.weight,
            age = userProfile.age,
            gender = userProfile.gender,
            likes = userProfile.likes,
            dislikes = userProfile.dislikes,
            dietaryRestrictions = userProfile.dietaryRestrictions,
            activityLevel = userProfile.activityLevel
        )

        val requestJson = gson.toJson(request)
        return sseManager.streamDietPlan(requestJson)
    }
}