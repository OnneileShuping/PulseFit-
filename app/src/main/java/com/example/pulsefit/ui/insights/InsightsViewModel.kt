package com.example.pulsefit.ui.insights

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.prefs.WeeklyGoalStore
import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.domain.CoachEngine
import com.example.pulsefit.domain.CoachInsights
import com.example.pulsefit.domain.CoachTips
import com.example.pulsefit.domain.GamificationEngine
import com.example.pulsefit.domain.GamificationState
import com.example.pulsefit.domain.WeeklyStats
import com.example.pulsefit.domain.WeeklyProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.TimeZone

data class InsightsUiState(
    val coach: CoachInsights,
    val gamification: GamificationState,
    val weekly: WeeklyProgress,
    val loading: Boolean = false,
    val selectedType: String = "Running",
    val availableTypes: List<String> = CoachTips.library.keys.filter { it != "General" } + "General",
    val randomTips: List<String> = emptyList()
)

/**
 * ViewModel for managing AI Coach insights, Gamification state, and Weekly goals/trends.
 * Combines Room activity data flow with user-configured weekly goal store.
 */
class InsightsViewModel(
    private val repo: PulseFitRepository,
    private val userId: String,
    private val goalStore: WeeklyGoalStore,
    private val clock: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val goalMinutesFlow = MutableStateFlow(goalStore.getWeeklyGoalMinutes())
    private val selectedTypeFlow = MutableStateFlow<String?>(null)
    private val shuffleCounterFlow = MutableStateFlow(0)

    val uiState: StateFlow<InsightsUiState> = combine(
        repo.observeActivities(userId),
        goalMinutesFlow,
        selectedTypeFlow,
        shuffleCounterFlow
    ) { activities, goalMinutes, userSelectedType, shuffleCounter ->
        val now = clock()
        val tz = TimeZone.getDefault()

        Log.d(TAG, "Updating insights for userId: $userId with ${activities.size} activities, goal: $goalMinutes, type: $userSelectedType, shuffle: $shuffleCounter")

        val coach = CoachEngine.analyse(activities, nowMillis = now, tz = tz, favouriteType = userSelectedType)
        val effectiveSelectedType = userSelectedType ?: coach.focusType
        val randomTips = CoachTips.randomTips(type = effectiveSelectedType, count = 3)
        val gamification = GamificationEngine.compute(activities, weeklyGoalMinutes = goalMinutes, nowMillis = now, tz = tz)
        val weekly = WeeklyStats.compute(activities, goalMinutes = goalMinutes, nowMillis = now, tz = tz)

        InsightsUiState(
            coach = coach,
            gamification = gamification,
            weekly = weekly,
            loading = false,
            selectedType = effectiveSelectedType,
            availableTypes = CoachTips.library.keys.filter { it != "General" } + "General",
            randomTips = randomTips
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = createDefaultState(goalStore.getWeeklyGoalMinutes(), clock())
    )

    /**
     * Updates the weekly workout goal in minutes, clamping it between MIN_GOAL (30) and MAX_GOAL (600).
     */
    fun setWeeklyGoal(minutes: Int) {
        val clamped = WeeklyStats.clampGoal(minutes)
        Log.d(TAG, "setWeeklyGoal: requested=$minutes, clamped=$clamped")
        goalStore.setWeeklyGoalMinutes(clamped)
        goalMinutesFlow.value = clamped
    }

    /**
     * Selects an activity type for AI Coach tips library exploration.
     */
    fun selectType(type: String) {
        Log.d(TAG, "selectType: $type")
        selectedTypeFlow.value = type
    }

    /**
     * Regenerates random tips for the currently selected activity type.
     */
    fun shuffleTips() {
        Log.d(TAG, "shuffleTips requested")
        shuffleCounterFlow.value++
    }

    companion object {
        private const val TAG = "InsightsViewModel"

        private fun createDefaultState(goal: Int, now: Long): InsightsUiState {
            val tz = TimeZone.getDefault()
            val coach = CoachEngine.analyse(emptyList(), nowMillis = now, tz = tz)
            val focusType = coach.focusType
            return InsightsUiState(
                coach = coach,
                gamification = GamificationEngine.compute(emptyList(), weeklyGoalMinutes = goal, nowMillis = now, tz = tz),
                weekly = WeeklyStats.compute(emptyList(), goalMinutes = goal, nowMillis = now, tz = tz),
                loading = true,
                selectedType = focusType,
                availableTypes = CoachTips.library.keys.filter { it != "General" } + "General",
                randomTips = CoachTips.randomTips(focusType, 3)
            )
        }
    }
}
