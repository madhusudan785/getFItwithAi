package com.example.dietplanner.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietplanner.com.example.dietplanner.data.local.database.DayPlanEntity
import com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlanEntity
import com.example.dietplanner.com.example.dietplanner.data.repository.DietPlanLocalRepository
import com.example.dietplanner.com.example.dietplanner.util.TextCleanupUtil
import com.example.dietplanner.data.local.UserPreferencesManager
import com.example.dietplanner.data.model.DietPlanState
import com.example.dietplanner.data.model.ParsedDietPlan
import com.example.dietplanner.data.model.UserProfile
import com.example.dietplanner.data.network.StreamEvent
import com.example.dietplanner.data.repository.DietRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class DietViewModel(
    private val repository: DietRepository,
    private val localRepository: DietPlanLocalRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    companion object {
        private const val TAG = "DietViewModel"
    }

    private val _dietPlanState = MutableStateFlow<DietPlanState>(DietPlanState.Idle)
    val dietPlanState: StateFlow<DietPlanState> = _dietPlanState.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // All saved diet plans
    val allDietPlans: StateFlow<List<DietPlanEntity>> = localRepository.getAllDietPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected diet plan for viewing
    private val _selectedDietPlan = MutableStateFlow<DietPlanEntity?>(null)
    val selectedDietPlan: StateFlow<DietPlanEntity?> = _selectedDietPlan.asStateFlow()

    // Day plans for selected diet
    private val _selectedDayPlans = MutableStateFlow<List<DayPlanEntity>>(emptyList())
    val selectedDayPlans: StateFlow<List<DayPlanEntity>> = _selectedDayPlans.asStateFlow()

    // Generated plan content (to be saved)
    private val _generatedPlanContent = MutableStateFlow<String>("")
    val generatedPlanContent: StateFlow<String> = _generatedPlanContent.asStateFlow()

    init {
        Log.d(TAG, "ViewModel initialized")
        viewModelScope.launch {
            preferencesManager.userProfileFlow.collect { profile ->
                Log.d(TAG, "User profile loaded: ${profile != null}")
                _userProfile.value = profile
            }
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        Log.d(TAG, "=== Saving User Profile ===")
        Log.d(TAG, "Profile: $profile")
        viewModelScope.launch {
            try {
                _userProfile.value = profile
                preferencesManager.saveUserProfile(profile)
                Log.i(TAG, "✅ Profile saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error saving profile", e)
            }
        }
    }

    fun generateDietPlan(userProfile: UserProfile) {
        Log.d(TAG, "=== Starting Diet Plan Generation ===")
        viewModelScope.launch {
            Log.d("DietViewModel", "Generating diet plan for ${userProfile.age}")
            try {
                _dietPlanState.value = DietPlanState.Loading
                Log.d(TAG, "State: Loading")

                val contentBuilder = StringBuilder()
                var chunkCount = 0

                repository.generateDietPlan(userProfile).collect { event ->
                    Log.d("DietViewModel", "DietPlanState updated: $event")
                    when (event) {
                        is StreamEvent.Connected -> {
                            Log.i(TAG, "✅ Connected to stream")
                            _dietPlanState.value = DietPlanState.Streaming("")
                        }
                        is StreamEvent.Data -> {
                            chunkCount++
                            contentBuilder.append(event.content)
                            val currentContent = contentBuilder.toString()
                            Log.d(TAG, "Chunk #$chunkCount - Total length: ${currentContent.length}")
                            _dietPlanState.value = DietPlanState.Streaming(currentContent)
                        }
                        is StreamEvent.Complete -> {
                            val finalContent = contentBuilder.toString().trim()
                            Log.d(TAG, "🟢 Stream complete, total length: ${finalContent.length}")

                            // 🧩 Handle both markers, no matter the order
                            val coachMarker = "---COACH MESSAGE---"
                            val jsonMarker = "---JSON PLAN---"

                            var coachMessage = ""
                            var jsonPart = ""

                            // Case 1: If both markers exist
                            if (finalContent.contains(coachMarker) && finalContent.contains(jsonMarker)) {
                                val coachIndex = finalContent.indexOf(coachMarker)
                                val jsonIndex = finalContent.indexOf(jsonMarker)

                                if (coachIndex < jsonIndex) {
                                    coachMessage = finalContent.substringAfter(coachMarker).substringBefore(jsonMarker).trim()
                                    jsonPart = finalContent.substringAfter(jsonMarker).trim()
                                } else {
                                    coachMessage = finalContent.substringAfter(coachMarker).trim()
                                    jsonPart = finalContent.substringAfter(jsonMarker).trim()
                                }
                            }
                            // Case 2: Only JSON found
                            else if (finalContent.contains(jsonMarker)) {
                                jsonPart = finalContent.substringAfter(jsonMarker).trim()
                            }
                            // Case 3: Only coach message found
                            else if (finalContent.contains(coachMarker)) {
                                coachMessage = finalContent.substringAfter(coachMarker).trim()
                            }
                            // Case 4: Fallback
                            else {
                                coachMessage = finalContent
                            }

                            // 🧹 Clean coach message markdown
                            val cleanCoachMessage = coachMessage
                                .replace(Regex("```json|```", RegexOption.IGNORE_CASE), "")
                                .replace(Regex("\\*+"), "")
                                .replace(Regex("(?m)^\\s*[-*+]\\s+"), "• ")
                                .replace(Regex("\\s+"), " ")
                                .trim()

                            val displayMessage = buildString {
                                appendLine(cleanCoachMessage)
                                appendLine()
                                appendLine("✅ Your personalized diet plan is ready!")
                            }

                            _generatedPlanContent.value = displayMessage
                            _dietPlanState.value = DietPlanState.Success(displayMessage)

                            // 🧩 Try parsing structured JSON
                            if (jsonPart.isNotBlank()) {
                                try {
                                    var cleanJson = jsonPart
                                        .replace("```json", "", ignoreCase = true)
                                        .replace("```", "")
                                        .trim()

                                    if (cleanJson.startsWith("\"") && cleanJson.endsWith("\"")) {
                                        cleanJson = cleanJson.removeSurrounding("\"")
                                            .replace("\\n", "")
                                            .replace("\\\"", "\"")
                                    }

                                    val jsonStart = cleanJson.indexOf('{')
                                    val jsonEnd = cleanJson.lastIndexOf('}')
                                    if (jsonStart != -1 && jsonEnd != -1) {
                                        cleanJson = cleanJson.substring(jsonStart, jsonEnd + 1)
                                    }

                                    val parsedPlan = Gson().fromJson(cleanJson, ParsedDietPlan::class.java)
                                    Log.i(TAG, "✅ Parsed structured plan for ${parsedPlan.days.size} days")

                                    saveParsedDietPlan(parsedPlan)
                                } catch (e: Exception) {
                                    Log.e(TAG, "❌ Failed to parse structured JSON (Ask Gemini)", e)
                                }
                            } else {
                                Log.w(TAG, "⚠️ No valid JSON detected in response.")
                            }
                        }


                        is StreamEvent.Error -> {
                            Log.e(TAG, "❌ Stream error: ${event.message}")
                            _dietPlanState.value = DietPlanState.Error(event.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception during generation", e)
                _dietPlanState.value = DietPlanState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun saveParsedDietPlan(parsedPlan: ParsedDietPlan) {
        viewModelScope.launch {
            try {
                val profile = _userProfile.value
                if (profile == null) {
                    Log.e(TAG, "❌ No user profile available to save structured plan")
                    return@launch
                }

                localRepository.saveParsedDietPlan(parsedPlan, profile)
                Log.i(TAG, "✅ Structured diet plan saved successfully")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to save parsed diet plan", e)
            }
        }
    }


    fun saveDietPlan(name: String = "My Diet Plan", planType: String = "weekly") {
        Log.d(TAG, "=== Saving Diet Plan to Database ===")
        viewModelScope.launch {
            try {
                val content = _generatedPlanContent.value
                val profile = _userProfile.value

                if (content.isBlank()) {
                    Log.e(TAG, "❌ No content to save")
                    return@launch
                }

                if (profile == null) {
                    Log.e(TAG, "❌ No user profile available")
                    return@launch
                }

                val planId = localRepository.saveDietPlan(
                    name = name,
                    rawContent = content,
                    planType = planType,
                    userProfile = profile
                )

                Log.i(TAG, "✅ Diet plan saved with ID: $planId")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error saving diet plan", e)
            }
        }
    }

    fun loadDietPlan(planId: Long) {
        viewModelScope.launch {
            val plan = localRepository.getDietPlanById(planId)
            _selectedDietPlan.value = plan
            Log.d("DietInDays", "Loaded plan: ${plan?.name}, cleaned length=${plan?.cleanedContent?.length}")

            plan?.let {
                val dayPlansMap = TextCleanupUtil.extractDayPlans(it.cleanedContent)
                Log.d("DietInDays", "Extracted ${dayPlansMap.size} days: ${dayPlansMap.keys}")

                val structuredPlans = TextCleanupUtil.toDayPlanEntities(it.id, dayPlansMap)
                Log.d("DietInDays", "Structured into ${structuredPlans.size} DayPlanEntity entries")

                _selectedDayPlans.value = structuredPlans
            }
        }
    }



    fun toggleFavorite(plan: DietPlanEntity) {
        viewModelScope.launch {
            try {
                localRepository.toggleFavorite(plan)
                Log.d(TAG, "Toggled favorite for plan ${plan.id}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error toggling favorite", e)
            }
        }
    }

    fun deleteDietPlan(plan: DietPlanEntity) {
        viewModelScope.launch {
            try {
                localRepository.deleteDietPlan(plan)
                Log.d(TAG, "Deleted diet plan ${plan.id}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error deleting plan", e)
            }
        }
    }

    fun resetState() {
        Log.d(TAG, "Resetting state to Idle")
        _dietPlanState.value = DietPlanState.Idle
        _generatedPlanContent.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared")
    }
}