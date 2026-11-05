package com.example.pulsefit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.local.AppDatabase
import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.model.Activity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val xp: Int = 0,
    val tier: String = "Bronze",
    val streak: Int = 0,
    val activities: List<Activity> = emptyList(),
    val aiTip: String = "Take it easy today — recovery is part of the grind."
)

class HomeViewModel(
    private val repo: PulseFitRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            repo.observeActivities(userId).collect { list ->
                val xp = list.sumOf { it.calories / 2 }
                val tier = when {
                    xp >= 5000 -> "Platinum"
                    xp >= 3000 -> "Gold"
                    xp >= 1000 -> "Silver"
                    else -> "Bronze"
                }
                _uiState.update {
                    it.copy(xp = xp, tier = tier, activities = list, streak = calculateStreak(list))
                }
            }
        }
    }

    private fun calculateStreak(activities: List<Activity>): Int {
        if (activities.isEmpty()) return 0
        // Simple streak: count consecutive days with activities from today backwards
        val days = activities.map { it.timestamp / 86_400_000L }.toSet()
        var streak = 0
        var day = System.currentTimeMillis() / 86_400_000L
        while (days.contains(day)) { streak++; day-- }
        return streak
    }
}