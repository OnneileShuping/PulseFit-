package com.example.pulsefit.domain

import com.example.pulsefit.data.local.HabitCheckEntity
import com.example.pulsefit.data.local.HabitEntity
import java.util.TimeZone

enum class DayStatus {
    DONE,
    MISSED,
    NOT_SCHEDULED,
    TODAY_PENDING
}

data class HabitStreak(
    val current: Int,
    val best: Int
)

/**
 * Pure Kotlin engine for daily habit statistics, scheduling, streaks, and completion rates.
 */
object HabitStats {

    /**
     * Checks if a habit is scheduled for the given day index.
     * Bit 0 = Mon, Bit 1 = Tue, Bit 2 = Wed, Bit 3 = Thu, Bit 4 = Fri, Bit 5 = Sat, Bit 6 = Sun.
     */
    fun isScheduled(habit: HabitEntity, dayIndex: Long): Boolean {
        if (habit.daysOfWeekMask == 0) return false
        val weekdayIndex = Math.floorMod(dayIndex + 3, 7).toInt()
        return (habit.daysOfWeekMask and (1 shl weekdayIndex)) != 0
    }

    /**
     * Calculates done / total progress of habits scheduled today.
     */
    fun todayProgress(
        habits: List<HabitEntity>,
        checks: List<HabitCheckEntity>,
        today: Long
    ): Pair<Int, Int> {
        val activeHabits = habits.filter { !it.archived }
        val scheduledToday = activeHabits.filter { isScheduled(it, today) }
        val total = scheduledToday.size
        val checkedIds = checks.filter { it.dayIndex == today }.map { it.habitId }.toSet()
        val done = scheduledToday.count { it.id in checkedIds }
        return Pair(done, total)
    }

    /**
     * Calculates consecutive SCHEDULED days completed, ending today or yesterday.
     * Unscheduled days are skipped and do not break or extend the streak.
     */
    fun streak(
        habit: HabitEntity,
        checkedDays: Set<Long>,
        today: Long
    ): HabitStreak {
        if (habit.daysOfWeekMask == 0) return HabitStreak(0, 0)

        // Calculate current streak backwards from today
        var current = 0
        var d = today

        // If today is unchecked, skip today without breaking the streak
        if (d == today && (d !in checkedDays || !isScheduled(habit, d))) {
            d = today - 1
        }

        while (true) {
            if (!isScheduled(habit, d)) {
                d--
                // Avoid infinite loop before habit creation
                if (d < today - 3650) break
            } else if (d in checkedDays) {
                current++
                d--
            } else {
                break
            }
        }

        // Calculate best streak by walking forward from creation or earliest check
        val createdDay = DayUtil.dayIndex(habit.createdAt)
        val earliestCheck = checkedDays.minOrNull() ?: today
        val startDay = minOf(createdDay, earliestCheck)

        var running = 0
        var best = 0

        for (day in startDay..today) {
            if (!isScheduled(habit, day)) {
                continue
            } else if (day in checkedDays) {
                running++
                best = maxOf(best, running)
            } else if (day == today) {
                // Today is pending
                continue
            } else {
                running = 0
            }
        }

        best = maxOf(best, current)
        return HabitStreak(current = current, best = best)
    }

    /**
     * Returns 7 statuses for the last 7 days (oldest today - 6 to newest today).
     */
    fun last7Days(
        habit: HabitEntity,
        checkedDays: Set<Long>,
        today: Long
    ): List<DayStatus> {
        val range = (today - 6)..today
        return range.map { d ->
            val scheduled = isScheduled(habit, d)
            val checked = d in checkedDays
            when {
                !scheduled -> DayStatus.NOT_SCHEDULED
                checked -> DayStatus.DONE
                d == today -> DayStatus.TODAY_PENDING
                else -> DayStatus.MISSED
            }
        }
    }

    /**
     * Calculates completion rate over the last `days` (default 30).
     */
    fun completionRate(
        habit: HabitEntity,
        checkedDays: Set<Long>,
        today: Long,
        days: Int = 30
    ): Float {
        val startDay = today - days + 1
        val scheduledDays = (startDay..today).filter { isScheduled(habit, it) }
        if (scheduledDays.isEmpty()) return 0f
        val completedCount = scheduledDays.count { it in checkedDays }
        return (completedCount.toFloat() / scheduledDays.size).coerceIn(0f, 1f)
    }

    /**
     * Counts the number of distinct days where all scheduled active habits were completed.
     */
    fun perfectHabitDaysCount(
        habits: List<HabitEntity>,
        checks: List<HabitCheckEntity>,
        untilToday: Long
    ): Int {
        val activeHabits = habits.filter { !it.archived }
        if (activeHabits.isEmpty()) return 0

        val earliestCreated = activeHabits.minOfOrNull { DayUtil.dayIndex(it.createdAt) } ?: untilToday
        val checksByDay = checks.groupBy { it.dayIndex }

        var perfectCount = 0
        for (day in earliestCreated..untilToday) {
            val scheduled = activeHabits.filter { isScheduled(it, day) }
            if (scheduled.isNotEmpty()) {
                val dayCheckIds = checksByDay[day]?.map { it.habitId }?.toSet() ?: emptySet()
                if (scheduled.all { it.id in dayCheckIds }) {
                    perfectCount++
                }
            }
        }
        return perfectCount
    }
}
