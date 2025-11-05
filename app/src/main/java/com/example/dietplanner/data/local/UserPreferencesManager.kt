package com.example.dietplanner.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.dietplanner.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")

class UserPreferencesManager(private val context: Context) {

    private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")

    suspend fun setFirstLaunchDone() {
        context.dataStore.edit { prefs ->
            prefs[FIRST_LAUNCH_KEY] = true
        }
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[FIRST_LAUNCH_KEY] ?: false }

    private object Keys {
        val HEIGHT = floatPreferencesKey("height")
        val WEIGHT = floatPreferencesKey("weight")
        val AGE = intPreferencesKey("age")
        val GENDER = stringPreferencesKey("gender")
        val LIKES = stringPreferencesKey("likes")
        val DISLIKES = stringPreferencesKey("dislikes")
        val DIETARY_RESTRICTIONS = stringPreferencesKey("dietary_restrictions")
        val ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
    }
    
    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HEIGHT] = profile.height
            prefs[Keys.WEIGHT] = profile.weight
            prefs[Keys.AGE] = profile.age
            prefs[Keys.GENDER] = profile.gender
            prefs[Keys.LIKES] = profile.likes.joinToString(",")
            prefs[Keys.DISLIKES] = profile.dislikes.joinToString(",")
            prefs[Keys.DIETARY_RESTRICTIONS] = profile.dietaryRestrictions
            prefs[Keys.ACTIVITY_LEVEL] = profile.activityLevel
        }
    }
    
    val userProfileFlow: Flow<UserProfile?> = context.dataStore.data.map { prefs ->
        val height = prefs[Keys.HEIGHT]
        val weight = prefs[Keys.WEIGHT]
        val age = prefs[Keys.AGE]
        val gender = prefs[Keys.GENDER]
        
        if (height != null && weight != null && age != null && gender != null) {
            UserProfile(
                height = height,
                weight = weight,
                age = age,
                gender = gender,
                likes = prefs[Keys.LIKES]?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
                dislikes = prefs[Keys.DISLIKES]?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
                dietaryRestrictions = prefs[Keys.DIETARY_RESTRICTIONS] ?: "",
                activityLevel = prefs[Keys.ACTIVITY_LEVEL] ?: "Moderate"
            )
        } else null
    }
}