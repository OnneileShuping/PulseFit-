package com.example.pulsefit.domain

import com.example.pulsefit.model.Activity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

class GamificationEngineTest {

    private val now = 1_700_000_000_000L
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
    fun `streak 7 days active produces 7 streak and 1 shield`() {
        val activeDays = setOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
        val res = GamificationEngine.streak(activeDays, 7L)
        assertEquals(7, res.current)
        assertEquals(7, res.best)
        assertEquals(1, res.shields)
        assertEquals(0, res.shieldsUsed)
    }

    @Test
    fun `streak 1 to 7 and 9 with today 9 uses 1 shield and keeps streak`() {
        val activeDays = setOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 9L)
        val res = GamificationEngine.streak(activeDays, 9L)
        assertEquals(8, res.current)
        assertEquals(8, res.best)
        assertEquals(0, res.shields)
        assertEquals(1, res.shieldsUsed)
    }

    @Test
    fun `streak 1 to 3 and 5 without shield resets current streak to 1`() {
        val activeDays = setOf(1L, 2L, 3L, 5L)
        val res = GamificationEngine.streak(activeDays, 5L)
        assertEquals(1, res.current)
        assertEquals(3, res.best)
        assertEquals(0, res.shields)
        assertEquals(0, res.shieldsUsed)
    }

    @Test
    fun `two consecutive misses break streak despite shields`() {
        // Active days 1..14 (generates 2 shields), missed 15 (uses 1 shield), missed 16 (resets streak), active 17
        val activeDays = (1L..14L).toSet() + 17L
        val res = GamificationEngine.streak(activeDays, 17L)
        assertEquals(1, res.current)
        assertEquals(14, res.best)
        assertEquals(1, res.shields)
        assertEquals(1, res.shieldsUsed)
    }

    @Test
    fun `today not logged yet does not break current streak`() {
        val activeDays = setOf(1L, 2L, 3L)
        val res = GamificationEngine.streak(activeDays, 4L)
        assertEquals(3, res.current)
        assertEquals(3, res.best)
    }

    @Test
    fun `shields are capped at 2`() {
        val activeDays = (1L..21L).toSet()
        val res = GamificationEngine.streak(activeDays, 21L)
        assertEquals(2, res.shields)
    }

    @Test
    fun `compute 2000 kcal yields 1000 XP Silver tier and 2000 XP to Gold`() {
        val activities = listOf(
            act(0, 100, calories = 2000)
        )
        val state = GamificationEngine.compute(activities, 150, now, tz)
        assertEquals(1000, state.xp)
        assertEquals("Silver", state.tier)
        assertEquals("Gold", state.nextTier)
        assertEquals(2000, state.xpToNextTier)
    }

    @Test
    fun `compute 10000 kcal yields 5000 XP Platinum tier with no next tier`() {
        val activities = listOf(
            act(0, 500, calories = 10000)
        )
        val state = GamificationEngine.compute(activities, 150, now, tz)
        assertEquals(5000, state.xp)
        assertEquals("Platinum", state.tier)
        assertNull(state.nextTier)
        assertNull(state.xpToNextTier)
        assertEquals(1f, state.tierFraction, 0.001f)
    }

    @Test
    fun `badge progress correctly reflects workout activities`() {
        val activities = listOf(
            act(0, 30, type = "Running", km = 5.0),
            act(1, 45, type = "Cycling", km = 25.0)
        )
        val state = GamificationEngine.compute(activities, 150, now, tz)

        val firstSteps = state.badges.first { it.id == "first_steps" }
        assertTrue(firstSteps.earned)
        assertEquals(2, firstSteps.current)

        val runner = state.badges.first { it.id == "iron_runner" }
        assertEquals(1, runner.current)

        val cyclist = state.badges.first { it.id == "century_cyclist" }
        assertEquals(25, cyclist.current)
    }
}
