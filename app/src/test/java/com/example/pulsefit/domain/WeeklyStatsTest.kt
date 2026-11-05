package com.example.pulsefit.domain

import com.example.pulsefit.model.Activity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

class WeeklyStatsTest {

    private val now = 1_700_000_000_000L // Nov 14 2023 (Tuesday)
    private val tz = TimeZone.getTimeZone("UTC")

    private fun act(
        daysAgo: Int,
        minutes: Int,
        type: String = "Running",
        calories: Int = minutes * 10,
        km: Double = minutes * 0.1
    ): Activity {
        return Activity(
            id = "test_${daysAgo}_${minutes}_$type",
            userId = "test_user",
            type = type,
            durationSeconds = minutes * 60,
            distanceKm = km,
            calories = calories,
            timestamp = now - daysAgo * DayUtil.DAY_MS
        )
    }

    @Test
    fun `compute returns 7 bars ending today Tuesday`() {
        val progress = WeeklyStats.compute(emptyList(), 150, now, tz)
        assertEquals(7, progress.bars.size)

        val todayBar = progress.bars.last()
        assertTrue(todayBar.isToday)
        assertEquals("Tue", todayBar.label)

        val firstBar = progress.bars.first()
        assertFalse(firstBar.isToday)
        assertEquals("Wed", firstBar.label) // 6 days before Tue is Wed
    }

    @Test
    fun `per-day bucketing aggregates minutes on correct days`() {
        val activities = listOf(
            act(0, 30), // Today (Tue)
            act(0, 15), // Today (Tue)
            act(1, 45)  // Yesterday (Mon)
        )
        val progress = WeeklyStats.compute(activities, 150, now, tz)
        assertEquals(90, progress.minutes)

        val todayBar = progress.bars.last()
        assertEquals(45, todayBar.minutes)

        val monBar = progress.bars[5] // Index 5 is yesterday (Mon)
        assertEquals(45, monBar.minutes)
    }

    @Test
    fun `fraction is capped at 1 when goal is exceeded`() {
        val activities = listOf(
            act(0, 200)
        )
        val progress = WeeklyStats.compute(activities, 150, now, tz)
        assertEquals(200, progress.minutes)
        assertEquals(1f, progress.fraction, 0.001f)
        assertTrue(progress.goalMet)
        assertEquals(0, progress.remaining)
    }

    @Test
    fun `previous week comparison calculates changeVsLastWeek correctly`() {
        val activities = listOf(
            act(1, 100), // This week: 100 min
            act(8, 60)   // Previous week: 60 min
        )
        val progress = WeeklyStats.compute(activities, 150, now, tz)
        assertEquals(100, progress.minutes)
        assertEquals(60, progress.previousWeekMinutes)
        assertEquals(40, progress.changeVsLastWeek)
    }

    @Test
    fun `clampGoal restricts goal to MIN and MAX limits`() {
        assertEquals(30, WeeklyStats.clampGoal(10))
        assertEquals(600, WeeklyStats.clampGoal(700))
        assertEquals(150, WeeklyStats.clampGoal(150))
    }

    @Test
    fun `dailyTarget is computed as goal divided by 7`() {
        val progress = WeeklyStats.compute(emptyList(), 140, now, tz)
        assertEquals(20f, progress.dailyTarget, 0.001f)
    }
}
