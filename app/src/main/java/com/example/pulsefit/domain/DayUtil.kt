package com.example.pulsefit.domain

import java.util.TimeZone

/**
 * Utility functions for date calculations without Android or java.time dependencies.
 */
object DayUtil {
    const val DAY_MS: Long = 86_400_000L

    private val WEEKDAY_LABELS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    /**
     * Converts an epoch timestamp in milliseconds to a day index representing days since epoch (1970-01-01 UTC).
     */
    fun dayIndex(timestamp: Long, tz: TimeZone = TimeZone.getDefault()): Long {
        val offset = tz.getOffset(timestamp).toLong()
        return Math.floorDiv(timestamp + offset, DAY_MS)
    }

    /**
     * Returns the 3-letter weekday label (Mon..Sun) for a given day index.
     * 1970-01-01 (dayIndex 0) was a Thursday.
     * floorMod(0 + 3, 7) = 3 -> "Thu".
     */
    fun weekdayLabel(dayIndex: Long): String {
        val index = Math.floorMod(dayIndex + 3, 7).toInt()
        return WEEKDAY_LABELS[index]
    }
}
