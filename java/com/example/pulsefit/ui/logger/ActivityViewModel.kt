package com.example.pulsefit.ui.logger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.model.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val repo: PulseFitRepository,
    private val userId: String
) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    fun save(
        type: String, durationSec: Int, distanceKm: Double,
        calories: Int, notes: String
    ) {
        viewModelScope.launch {
            val activity = Activity(
                type = type,
                durationSeconds = durationSec,
                distanceKm = distanceKm,
                calories = calories,
                notes = notes,
                timestamp = System.currentTimeMillis()
            )
            repo.saveActivity(userId, activity)
            _saved.value = true
        }
    }
}