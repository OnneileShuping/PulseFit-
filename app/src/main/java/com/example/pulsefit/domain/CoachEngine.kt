package com.example.pulsefit.domain

import com.example.pulsefit.model.Activity
import java.util.TimeZone

enum class Level(val label: String) {
    LOW("Low"),
    MODERATE("Moderate"),
    HIGH("High")
}

data class CoachInsights(
    val trainingLoad: Level,
    val fatigue: Level,
    val headline: String,
    val tips: List<String>,
    val weeklyMinutes: Int,
    val weeklyCalories: Int,
    val sessions: Int,
    val activeDays: Int,
    val consecutiveActiveDays: Int,
    val daysSinceLastWorkout: Int?,
    val previousWeekMinutes: Int,
    val focusType: String = "General"
)

/**
 * Pure Kotlin engine for generating AI Coach recommendations based on user activity history.
 */
object CoachEngine {

    /**
     * Calculates consecutive active days back from today (or yesterday if today has no workout).
     */
    internal fun consecutiveActiveDays(
        activities: List<Activity>,
        nowMillis: Long = System.currentTimeMillis(),
        tz: TimeZone = TimeZone.getDefault()
    ): Int {
        val today = DayUtil.dayIndex(nowMillis, tz)
        val validActivities = activities.filter { DayUtil.dayIndex(it.timestamp, tz) <= today }
        if (validActivities.isEmpty()) return 0

        val activeDays = validActivities.map { DayUtil.dayIndex(it.timestamp, tz) }.toSet()

        var currentDay = when {
            today in activeDays -> today
            (today - 1) in activeDays -> today - 1
            else -> return 0
        }

        var count = 0
        while (currentDay in activeDays) {
            count++
            currentDay--
        }
        return count
    }

    /**
     * Analyzes user activity data to produce training load, fatigue level, headline, and personalized tips.
     */
    fun analyse(
        activities: List<Activity>,
        nowMillis: Long = System.currentTimeMillis(),
        tz: TimeZone = TimeZone.getDefault(),
        favouriteType: String? = null
    ): CoachInsights {
        val today = DayUtil.dayIndex(nowMillis, tz)
        val validActivities = activities.filter { DayUtil.dayIndex(it.timestamp, tz) <= today }

        val thisWeekRange = (today - 6)..today
        val prevWeekRange = (today - 13)..(today - 7)

        val thisWeekActivities = validActivities.filter { DayUtil.dayIndex(it.timestamp, tz) in thisWeekRange }
        val prevWeekActivities = validActivities.filter { DayUtil.dayIndex(it.timestamp, tz) in prevWeekRange }

        val weeklyMinutes = thisWeekActivities.sumOf { it.durationSeconds / 60 }
        val weeklyCalories = thisWeekActivities.sumOf { it.calories }
        val sessions = thisWeekActivities.size
        val activeDays = thisWeekActivities.map { DayUtil.dayIndex(it.timestamp, tz) }.toSet().size
        val previousWeekMinutes = prevWeekActivities.sumOf { it.durationSeconds / 60 }

        val consecutiveActive = consecutiveActiveDays(validActivities, nowMillis, tz)

        val lastWorkoutDay = validActivities.maxOfOrNull { DayUtil.dayIndex(it.timestamp, tz) }
        val daysSinceLastWorkout = lastWorkoutDay?.let { (today - it).toInt() }

        val determinedFocusType = when {
            !favouriteType.isNullOrBlank() -> favouriteType
            thisWeekActivities.isNotEmpty() -> thisWeekActivities.groupBy { it.type }.maxByOrNull { it.value.size }?.key ?: "General"
            validActivities.isNotEmpty() -> validActivities.maxByOrNull { it.timestamp }?.type ?: "General"
            else -> "General"
        }

        val trainingLoad = when {
            weeklyMinutes > 240 -> Level.HIGH
            weeklyMinutes > 120 -> Level.MODERATE
            else -> Level.LOW
        }

        val fatigue = when {
            consecutiveActive >= 5 || (sessions >= 6 && weeklyMinutes > 240) -> Level.HIGH
            consecutiveActive >= 3 || sessions >= 4 -> Level.MODERATE
            else -> Level.LOW
        }

        val headline = when {
            validActivities.isEmpty() -> "Welcome! Let's get your first workout logged"
            sessions == 0 -> "Time to ease back in"
            fatigue == Level.HIGH -> "Recovery day recommended"
            fatigue == Level.MODERATE -> "Keep today easy"
            trainingLoad == Level.LOW -> "Room to push a little"
            else -> "You're on track. Keep it up!"
        }

        val tips = mutableListOf<String>()

        when (headline) {
            "Welcome! Let's get your first workout logged" -> {
                tips.add("Start with a 15–20 min session")
                tips.add("Consistency is key — pick an activity you enjoy")
            }
            "Time to ease back in" -> {
                tips.add("Ease back with light cardio or a short walk")
                tips.add("Focus on building momentum step by step")
            }
            "Recovery day recommended" -> {
                tips.add("Prioritize rest/mobility and passive recovery")
                tips.add("Focus on sleep and hydration today")
            }
            "Keep today easy" -> {
                tips.add("Schedule an easy session tomorrow or take a rest day")
                tips.add("Stay hydrated and stretch well")
            }
            "Room to push a little" -> {
                tips.add("Great time for a higher intensity workout")
                tips.add("Consider adding a new activity type")
            }
            else -> {
                tips.add("Maintain your current pace and listen to your body")
                tips.add("Keep up the great work with balanced training")
            }
        }

        // Ramp-up warning
        if (previousWeekMinutes >= 30 && weeklyMinutes > 1.3 * previousWeekMinutes) {
            val pct = Math.round(((weeklyMinutes - previousWeekMinutes).toDouble() / previousWeekMinutes) * 100).toInt()
            tips.add("Volume is up ${pct}% on last week. Consider gradual 10–20% increases to avoid injury.")
        }

        // Variety tip
        if (sessions >= 3 && thisWeekActivities.map { it.type.lowercase() }.distinct().size == 1) {
            val typeName = thisWeekActivities.first().type
            tips.add("You've done $sessions sessions of $typeName this week. Try adding variety like Cycling or Strength.")
        }

        // Goal nudge (skip when fatigue is HIGH or there is nothing this week)
        if (fatigue != Level.HIGH && sessions > 0) {
            if (weeklyMinutes >= 150) {
                tips.add("You've reached the recommended 150 minutes of weekly activity!")
            } else {
                val remaining = 150 - weeklyMinutes
                tips.add("You're $remaining minutes away from the recommended 150 weekly active minutes.")
            }
        }

        return CoachInsights(
            trainingLoad = trainingLoad,
            fatigue = fatigue,
            headline = headline,
            tips = tips,
            weeklyMinutes = weeklyMinutes,
            weeklyCalories = weeklyCalories,
            sessions = sessions,
            activeDays = activeDays,
            consecutiveActiveDays = consecutiveActive,
            daysSinceLastWorkout = daysSinceLastWorkout,
            previousWeekMinutes = previousWeekMinutes,
            focusType = determinedFocusType
        )
    }
}
