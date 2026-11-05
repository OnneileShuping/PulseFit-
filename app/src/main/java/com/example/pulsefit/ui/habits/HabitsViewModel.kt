package com.example.pulsefit.ui.habits

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.local.HabitCheckEntity
import com.example.pulsefit.data.local.HabitEntity
import com.example.pulsefit.data.repository.PulseFitRepository
import com.example.pulsefit.domain.DayUtil
import com.example.pulsefit.domain.HabitStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.TimeZone
import java.util.UUID

data class HabitsUiState(
    val habits: List<HabitEntity> = emptyList(),
    val checks: List<HabitCheckEntity> = emptyList(),
    val todayDone: Int = 0,
    val todayTotal: Int = 0,
    val loading: Boolean = false
)

/**
 * ViewModel for managing daily habit tracking, creation, editing, deletion, and checks.
 */
class HabitsViewModel(
    private val repo: PulseFitRepository,
    private val userId: String,
    private val clock: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val now = clock()
    private val tz = TimeZone.getDefault()
    val todayIndex = DayUtil.dayIndex(now, tz)

    // Observe last 30 days of checks
    val uiState: StateFlow<HabitsUiState> = combine(
        repo.observeHabits(userId),
        repo.observeHabitChecks(userId, startDay = todayIndex - 30, endDay = todayIndex)
    ) { habits, checks ->
        val (done, total) = HabitStats.todayProgress(habits, checks, todayIndex)
        HabitsUiState(
            habits = habits,
            checks = checks,
            todayDone = done,
            todayTotal = total,
            loading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HabitsUiState(loading = true)
    )

    /**
     * Toggles a habit check for today.
     */
    fun toggleTodayCheck(habitId: String, isChecked: Boolean) {
        Log.d(TAG, "toggleTodayCheck: habitId=$habitId, dayIndex=$todayIndex, isChecked=$isChecked")
        viewModelScope.launch {
            repo.toggleHabitCheck(
                userId = userId,
                habitId = habitId,
                dayIndex = todayIndex,
                isChecked = isChecked
            )
        }
    }

    /**
     * Saves a new or edited habit with validation. Returns an error message or null if valid.
     */
    fun saveHabit(
        name: String,
        emoji: String,
        colorHex: String,
        daysMask: Int,
        editingHabitId: String? = null
    ): String? {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return "Habit name cannot be empty"
        if (trimmed.length > 40) return "Habit name must be 40 characters or fewer"
        if (daysMask == 0) return "Select at least one day of the week"

        val currentHabits = uiState.value.habits
        val duplicate = currentHabits.any {
            it.name.equals(trimmed, ignoreCase = true) && it.id != editingHabitId && !it.archived
        }
        if (duplicate) return "A habit with this name already exists"

        val habit = if (editingHabitId != null) {
            val existing = currentHabits.firstOrNull { it.id == editingHabitId }
            existing?.copy(
                name = trimmed,
                emoji = emoji,
                colorHex = colorHex,
                daysOfWeekMask = daysMask
            ) ?: HabitEntity(
                id = editingHabitId,
                userId = userId,
                name = trimmed,
                emoji = emoji,
                colorHex = colorHex,
                daysOfWeekMask = daysMask,
                createdAt = clock()
            )
        } else {
            HabitEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = trimmed,
                emoji = emoji,
                colorHex = colorHex,
                daysOfWeekMask = daysMask,
                createdAt = clock()
            )
        }

        Log.d(TAG, "saveHabit: ${habit.name} (id: ${habit.id})")
        viewModelScope.launch {
            repo.saveHabit(habit)
        }
        return null
    }

    /**
     * Deletes a habit and its checks.
     */
    fun deleteHabit(habitId: String) {
        Log.d(TAG, "deleteHabit: $habitId")
        viewModelScope.launch {
            repo.deleteHabit(habitId)
        }
    }

    /**
     * One-tap addition of 3 recommended starter habits.
     */
    fun addStarterHabits() {
        Log.d(TAG, "addStarterHabits requested")
        val starters = listOf(
            HabitEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "Drink 2L water",
                emoji = "💧",
                colorHex = "#00B8F0",
                daysOfWeekMask = 127,
                createdAt = clock()
            ),
            HabitEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "Stretch 10 min",
                emoji = "🧘",
                colorHex = "#28B878",
                daysOfWeekMask = 127,
                createdAt = clock()
            ),
            HabitEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = "10-minute walk",
                emoji = "🚶",
                colorHex = "#7CF2C0",
                daysOfWeekMask = 127,
                createdAt = clock()
            )
        )
        viewModelScope.launch {
            starters.forEach { repo.saveHabit(it) }
        }
    }

    companion object {
        private const val TAG = "HabitsViewModel"
    }
}
