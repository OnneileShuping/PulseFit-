package com.example.pulsefit.domain

import com.example.pulsefit.model.Activity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

class CoachEngineTest {

    private val now = 1_700_000_000_000L // Nov 14 2023 22:13:20 UTC
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
    fun `empty activities returns welcome headline`() {
        val insights = CoachEngine.analyse(emptyList(), now, tz)
        assertEquals("Welcome! Let's get your first workout logged", insights.headline)
        assertEquals(Level.LOW, insights.trainingLoad)
        assertEquals(Level.LOW, insights.fatigue)
        assertNull(insights.daysSinceLastWorkout)
    }

    @Test
    fun `3 consecutive days results in MODERATE fatigue`() {
        val activities = listOf(
            act(0, 30),
            act(1, 30),
            act(2, 30)
        )
        val insights = CoachEngine.analyse(activities, now, tz)
        assertEquals(3, insights.consecutiveActiveDays)
        assertEquals(Level.MODERATE, insights.fatigue)
        assertEquals("Keep today easy", insights.headline)
    }

    @Test
    fun `5 consecutive days results in HIGH fatigue and Recovery headline`() {
        val activities = listOf(
            act(0, 30),
            act(1, 30),
            act(2, 30),
            act(3, 30),
            act(4, 30)
        )
        val insights = CoachEngine.analyse(activities, now, tz)
        assertEquals(5, insights.consecutiveActiveDays)
        assertEquals(Level.HIGH, insights.fatigue)
        assertEquals("Recovery day recommended", insights.headline)
    }

    @Test
    fun `streak counted back from yesterday when today has no workout yet`() {
        val activities = listOf(
            act(1, 30),
            act(2, 30),
            act(3, 30)
        )
        val consecutive = CoachEngine.consecutiveActiveDays(activities, now, tz)
        assertEquals(3, consecutive)
    }

    @Test
    fun `nothing this week returns ease back in headline`() {
        val activities = listOf(
            act(10, 45)
        )
        val insights = CoachEngine.analyse(activities, now, tz)
        assertEquals("Time to ease back in", insights.headline)
        assertEquals(0, insights.weeklyMinutes)
    }

    @Test
    fun `future activities are ignored`() {
        val activities = listOf(
            act(-1, 60), // Future workout
            act(-2, 60)  // Future workout
        )
        val insights = CoachEngine.analyse(activities, now, tz)
        assertEquals("Welcome! Let's get your first workout logged", insights.headline)
        assertEquals(0, insights.weeklyMinutes)
    }

    @Test
    fun `ramp-up warning triggered when volume increases significantly over last week`() {
        // Prev week (days 7..13 ago): 60 min
        // This week (days 0..6 ago): 120 min (> 1.3x 60)
        val activities = listOf(
            act(8, 60),  // Prev week
            act(1, 120)  // This week
        )
        val insights = CoachEngine.analyse(activities, now, tz)
        assertTrue(insights.tips.any { it.contains("Volume is up") && it.contains("100%") })
    }

    @Test
    fun `270 minutes weekly results in HIGH training load`() {
        val activities = listOf(
            act(0, 90),
            act(1, 90),
            act(2, 90)
        )
        val insights = CoachEngine.analyse(activities, now, tz)
        assertEquals(270, insights.weeklyMinutes)
        assertEquals(Level.HIGH, insights.trainingLoad)
    }
}
