package com.example.dietplanner.com.example.dietplanner.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DietPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietPlan(plan: DietPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayPlans(dayPlans: List<DayPlanEntity>)

    @Query("SELECT * FROM diet_plans ORDER BY createdAt DESC")
    fun getAllDietPlans(): Flow<List<DietPlanEntity>>

    @Query("SELECT * FROM diet_plans WHERE id = :planId")
    suspend fun getDietPlanById(planId: Long): DietPlanEntity?

    @Query("SELECT * FROM diet_plans WHERE id = :planId")
    fun getDietPlanByIdFlow(planId: Long): Flow<DietPlanEntity?>

    @Query("SELECT * FROM day_plans WHERE dietPlanId = :planId ORDER BY dayNumber")
    fun getDayPlansForDiet(planId: Long): Flow<List<DayPlanEntity>>

    @Query("SELECT * FROM diet_plans WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritePlans(): Flow<List<DietPlanEntity>>

    @Update
    suspend fun updateDietPlan(plan: DietPlanEntity)

    @Delete
    suspend fun deleteDietPlan(plan: DietPlanEntity)

    @Query("DELETE FROM day_plans WHERE dietPlanId = :planId")
    suspend fun deleteDayPlansForDiet(planId: Long)

    @Query("SELECT COUNT(*) FROM diet_plans")
    suspend fun getDietPlansCount(): Int
}
