package com.example.pulsefit.domain

import com.example.pulsefit.data.local.HabitCheckEntity
import com.example.pulsefit.data.local.HabitEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

class HabitStatsTest {

    private val now = 1_700_000_000_000L // Nov 14 2023 (Tuesday)
    private val tz = TimeZone.getTimeZone("UTC")
    private val today = DayUtil.dayIndex(now, tz)

    private fun habit(
        id: String = "h1",
        name: String = "Water",
        emoji: String = "💧",
        daysMask: Int = 127, // Every day
        createdAt: Long = now - 30 * DayUtil.DAY_MS
    ): HabitEntity {
        return HabitEntity(
            id = id,
            userId = "u1",
            name = name,
            emoji = emoji,
            colorHex = "#00B8F0",
            daysOfWeekMask = daysMask,
            createdAt = createdAt
        )
    }

    @Test
    fun `isScheduled returns true when bit in mask matches weekday`() {
        // Tuesday index in 0..6 (Mon=0, Tue=1) is 1. Mask bit 1 = (1 shl 1) = 2.
        val tuesdayHabit = habit(daysMask = 2) // Only Tuesday
        val mondayHabit = habit(daysMask = 1) // Only Monday

        assertTrue(HabitStats.isScheduled(tuesdayHabit, today))
        assertFalse(HabitStats.isScheduled(mondayHabit, today))
    }

    @Test
    fun `todayProgress calculates done and total scheduled habits correctly`() {
        val h1 = habit(id = "h1", daysMask = 127)
        val h2 = habit(id = "h2", daysMask = 127)
        val habits = listOf(h1, h2)
        val checks = listOf(
            HabitCheckEntity(habitId = "h1", dayIndex = today, userId = "u1")
        )

        val (done, total) = HabitStats.todayProgress(habits, checks, today)
        assertEquals(1, done)
        assertEquals(2, total)
    }

    @Test
    fun `streak handles pending today and skips unscheduled days`() {
        val h = habit(id = "h1", daysMask = 127) // All days
        // Checked yesterday (today - 1) and day before yesterday (today - 2), but not today yet
        val checked = setOf(today - 1, today - 2)

        val s = HabitStats.streak(h, checked, today)
        assertEquals(2, s.current)
    }

    @Test
    fun `streak is broken by a scheduled miss`() {
        val h = habit(id = "h1", daysMask = 127)
        // Checked today and 2 days ago, but missed yesterday (today - 1)
        val checked = setOf(today, today - 2)

        val s = HabitStats.streak(h, checked, today)
        assertEquals(1, s.current)
        assertEquals(1, s.best)
    }

    @Test
    fun `last7Days returns correct DayStatus sequence`() {
        val h = habit(id = "h1", daysMask = 127)
        val checked = setOf(today - 1, today - 3)

        val statuses = HabitStats.last7Days(h, checked, today)
        assertEquals(7, statuses.size)
        assertEquals(DayStatus.TODAY_PENDING, statuses.last())
        assertEquals(DayStatus.DONE, statuses[5]) // today - 1
        assertEquals(DayStatus.MISSED, statuses[4]) // today - 2
        assertEquals(DayStatus.DONE, statuses[3]) // today - 3
    }

    @Test
    fun `completionRate calculates correct 30 day percentage`() {
        val h = habit(id = "h1", daysMask = 127)
        // Checked 15 out of 30 days
        val checked = (0 until 15).map { today - it }.toSet()

        val rate = HabitStats.completionRate(h, checked, today, 30)
        assertEquals(0.5f, rate, 0.001f)
    }
}
