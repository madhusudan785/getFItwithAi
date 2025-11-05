package com.example.dietplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dietplanner.com.example.dietplanner.data.repository.DietPlanLocalRepository
import com.example.dietplanner.data.local.UserPreferencesManager
import com.example.dietplanner.data.repository.DietRepository

class DietViewModelFactory(
    private val repository: DietRepository,
    private val localRepository: DietPlanLocalRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietViewModel::class.java)) {
            return DietViewModel(repository, localRepository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}