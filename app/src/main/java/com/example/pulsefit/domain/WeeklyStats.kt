package com.example.pulsefit.domain

import com.example.pulsefit.model.Activity
import java.util.TimeZone

data class DayBar(
    val dayIndex: Long,
    val label: String,
    val minutes: Int,
    val isToday: Boolean
)

data class WeeklyProgress(
    val goalMinutes: Int,
    val minutes: Int,
    val fraction: Float,
    val remaining: Int,
    val goalMet: Boolean,
    val dailyTarget: Float = goalMinutes / 7f,
    val bars: List<DayBar>,
    val previousWeekMinutes: Int,
    val changeVsLastWeek: Int = minutes - previousWeekMinutes
)

/**
 * Pure Kotlin engine for computing weekly workout statistics, trends, and daily bar data.
 */
object WeeklyStats {

    const val DEFAULT_GOAL = 150
    const val MIN_GOAL = 30
    const val MAX_GOAL = 600
    const val STEP_GOAL = 15

    fun clampGoal(goal: Int): Int = goal.coerceIn(MIN_GOAL, MAX_GOAL)

    fun compute(
        activities: List<Activity>,
        goalMinutes: Int = DEFAULT_GOAL,
        nowMillis: Long = System.currentTimeMillis(),
        tz: TimeZone = TimeZone.getDefault()
    ): WeeklyProgress {
        val clampedGoal = clampGoal(goalMinutes)
        val today = DayUtil.dayIndex(nowMillis, tz)
        val validActivities = activities.filter { DayUtil.dayIndex(it.timestamp, tz) <= today }

        val thisWeekRange = (today - 6)..today
        val prevWeekRange = (today - 13)..(today - 7)

        val activitiesByDay = validActivities.groupBy { DayUtil.dayIndex(it.timestamp, tz) }

        val bars = thisWeekRange.map { d ->
            val dayActs = activitiesByDay[d] ?: emptyList()
            val dayMins = dayActs.sumOf { it.durationSeconds / 60 }
            DayBar(
                dayIndex = d,
                label = DayUtil.weekdayLabel(d),
                minutes = dayMins,
                isToday = (d == today)
            )
        }

        val totalMinutes = bars.sumOf { it.minutes }
        val prevWeekActivities = prevWeekRange.flatMap { d -> activitiesByDay[d] ?: emptyList() }
        val previousWeekMinutes = prevWeekActivities.sumOf { it.durationSeconds / 60 }

        val fraction = (totalMinutes.toFloat() / clampedGoal).coerceIn(0f, 1f)
        val remaining = maxOf(0, clampedGoal - totalMinutes)
        val goalMet = totalMinutes >= clampedGoal
        val dailyTarget = clampedGoal / 7f
        val changeVsLastWeek = totalMinutes - previousWeekMinutes

        return WeeklyProgress(
            goalMinutes = clampedGoal,
            minutes = totalMinutes,
            fraction = fraction,
            remaining = remaining,
            goalMet = goalMet,
            dailyTarget = dailyTarget,
            bars = bars,
            previousWeekMinutes = previousWeekMinutes,
            changeVsLastWeek = changeVsLastWeek
        )
    }
}
