package com.example.pulsefit.domain

import com.example.pulsefit.model.Activity
import java.util.TimeZone

data class StreakResult(
    val current: Int,
    val best: Int,
    val shields: Int,
    val shieldsUsed: Int
)

data class BadgeProgress(
    val id: String,
    val name: String,
    val description: String,
    val current: Int,
    val target: Int
) {
    val earned: Boolean get() = current >= target
    val fraction: Float get() = if (target <= 0) 1f else (current.toFloat() / target).coerceIn(0f, 1f)
}

data class GamificationState(
    val xp: Int,
    val tier: String,
    val nextTier: String?,
    val xpToNextTier: Int?,
    val tierFraction: Float,
    val streak: StreakResult,
    val badges: List<BadgeProgress>,
    val earnedCount: Int
)

/**
 * Pure Kotlin engine for computing XP, Tiers, Streaks (with Streak Shields), and Badges.
 */
object GamificationEngine {

    private const val MAX_SHIELDS = 2

    /**
     * Walks every day from the first active day to today to compute current streak, best streak, and streak shields.
     */
    internal fun streak(activeDays: Set<Long>, today: Long): StreakResult {
        if (activeDays.isEmpty()) {
            return StreakResult(current = 0, best = 0, shields = 0, shieldsUsed = 0)
        }

        val firstDay = activeDays.minOrNull() ?: today
        var current = 0
        var best = 0
        var shields = 0
        var shieldsUsed = 0
        var lastWasProtectedMiss = false

        for (d in firstDay..today) {
            val isActive = d in activeDays
            if (isActive) {
                current++
                lastWasProtectedMiss = false
                if (current % 7 == 0 && shields < MAX_SHIELDS) {
                    shields++
                }
            } else if (d == today) {
                // Today isn't over yet
            } else if (shields > 0 && !lastWasProtectedMiss && current > 0) {
                shields--
                shieldsUsed++
                lastWasProtectedMiss = true
            } else {
                current = 0
                lastWasProtectedMiss = false
            }
            best = maxOf(best, current)
        }

        return StreakResult(
            current = current,
            best = best,
            shields = shields,
            shieldsUsed = shieldsUsed
        )
    }

    /**
     * Computes the complete gamification state (XP, Tier, Streak, Badges).
     */
    fun compute(
        activities: List<Activity>,
        weeklyGoalMinutes: Int = 150,
        nowMillis: Long = System.currentTimeMillis(),
        tz: TimeZone = TimeZone.getDefault(),
        perfectHabitDays: Int = 0
    ): GamificationState {
        val today = DayUtil.dayIndex(nowMillis, tz)
        val validActivities = activities.filter { DayUtil.dayIndex(it.timestamp, tz) <= today }

        val xp = validActivities.sumOf { it.calories / 2 }

        val (tier, nextTier, xpToNextTier, tierFraction) = when {
            xp < 1000 -> Quadrutple("Bronze", "Silver", 1000 - xp, xp / 1000f)
            xp < 3000 -> Quadrutple("Silver", "Gold", 3000 - xp, (xp - 1000) / 2000f)
            xp < 5000 -> Quadrutple("Gold", "Platinum", 5000 - xp, (xp - 3000) / 2000f)
            else -> Quadrutple("Platinum", null, null, 1f)
        }

        val activeDaysSet = validActivities.map { DayUtil.dayIndex(it.timestamp, tz) }.toSet()
        val streakResult = streak(activeDaysSet, today)

        val thisWeekMinutes = validActivities
            .filter { DayUtil.dayIndex(it.timestamp, tz) in (today - 6..today) }
            .sumOf { it.durationSeconds / 60 }

        val badges = listOf(
            BadgeProgress(
                id = "first_steps",
                name = "First Steps",
                description = "Complete your first workout",
                current = validActivities.size,
                target = 1
            ),
            BadgeProgress(
                id = "iron_runner",
                name = "Iron Runner",
                description = "Complete 50 running sessions",
                current = validActivities.count { it.type.equals("Running", ignoreCase = true) || it.type.equals("Run", ignoreCase = true) },
                target = 50
            ),
            BadgeProgress(
                id = "consistency_master",
                name = "Consistency Master",
                description = "Achieve a 10-day streak",
                current = streakResult.best,
                target = 10
            ),
            BadgeProgress(
                id = "streak_saver",
                name = "Streak Saver",
                description = "Use a streak shield to protect your streak",
                current = streakResult.shieldsUsed,
                target = 1
            ),
            BadgeProgress(
                id = "century_cyclist",
                name = "Century Cyclist",
                description = "Cover 100 km cycling",
                current = validActivities.filter { it.type.equals("Cycling", ignoreCase = true) || it.type.equals("Cycle", ignoreCase = true) }.sumOf { it.distanceKm }.toInt(),
                target = 100
            ),
            BadgeProgress(
                id = "goal_getter",
                name = "Goal Getter",
                description = "Hit your weekly workout goal",
                current = minOf(thisWeekMinutes, weeklyGoalMinutes),
                target = weeklyGoalMinutes
            ),
            BadgeProgress(
                id = "habit_hero",
                name = "Habit Hero",
                description = "Complete all scheduled habits on 7 different days",
                current = minOf(perfectHabitDays, 7),
                target = 7
            )
        )

        val earnedCount = badges.count { it.earned }

        return GamificationState(
            xp = xp,
            tier = tier,
            nextTier = nextTier,
            xpToNextTier = xpToNextTier,
            tierFraction = tierFraction,
            streak = streakResult,
            badges = badges,
            earnedCount = earnedCount
        )
    }

    private data class Quadrutple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}
