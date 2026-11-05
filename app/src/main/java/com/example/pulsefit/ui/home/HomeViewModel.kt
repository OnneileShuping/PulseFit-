package com.example.pulsefit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.domain.CoachEngine
import com.example.pulsefit.domain.CoachTips
import com.example.pulsefit.domain.GamificationEngine
import com.example.pulsefit.model.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val xp: Int = 0,
    val tier: String = "Bronze",
    val streak: Int = 0,
    val shields: Int = 0,
    val activities: List<Activity> = emptyList(),
    val aiTipHeadline: String = "Welcome! Let's get your first workout logged",
    val aiTipOfTheDay: String = "Stay active and listen to your body today!"
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
                val now = System.currentTimeMillis()
                val gamification = GamificationEngine.compute(list, nowMillis = now)
                val coach = CoachEngine.analyse(list, nowMillis = now)
                val tipOfDay = CoachTips.tipOfTheDay(coach.focusType, now)
                _uiState.update {
                    it.copy(
                        xp = gamification.xp,
                        tier = gamification.tier,
                        streak = gamification.streak.current,
                        shields = gamification.streak.shields,
                        activities = list,
                        aiTipHeadline = coach.headline,
                        aiTipOfTheDay = tipOfDay
                    )
                }
            }
        }
    }
}
