package com.example.dietplanner.data.network

import com.example.dietplanner.data.model.DietRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface DietApiService {
    @POST("/api/diet/generate")
    suspend fun generateDietPlan(@Body request: DietRequest)
}