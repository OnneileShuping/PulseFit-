package com.example.pulsefit.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone
import kotlin.random.Random

class CoachTipsTest {

    @Test
    fun `every type in library has exactly 50 distinct non-blank tips`() {
        val expectedTypes = listOf("Running", "Cycling", "Weightlifting", "Walking", "General")
        assertEquals(expectedTypes.sorted(), CoachTips.library.keys.sorted())

        CoachTips.library.forEach { (type, tips) ->
            assertEquals("Type $type should have exactly 50 tips", 50, tips.size)
            tips.forEach { tip ->
                assertTrue("Tip in $type should not be blank", tip.isNotBlank())
            }
            val uniqueCount = tips.toSet().size
            assertEquals("All 50 tips for $type must be unique", 50, uniqueCount)
        }
    }

    @Test
    fun `randomTips returns requested count with no duplicates`() {
        val tips = CoachTips.randomTips("Running", count = 3, random = Random(42))
        assertEquals(3, tips.size)
        assertEquals(3, tips.toSet().size)
    }

    @Test
    fun `randomTips is deterministic with same seed and produces different orders with different seeds`() {
        val seedA1 = CoachTips.randomTips("Cycling", count = 5, random = Random(12345))
        val seedA2 = CoachTips.randomTips("Cycling", count = 5, random = Random(12345))
        val seedB = CoachTips.randomTips("Cycling", count = 5, random = Random(99999))

        assertEquals(seedA1, seedA2)
        assertNotEquals(seedA1, seedB)
    }

    @Test
    fun `unknown activity type falls back to General tips`() {
        val unknownTips = CoachTips.randomTips("Skydiving", count = 3, random = Random(1))
        val generalTips = CoachTips.library["General"]!!

        assertEquals(3, unknownTips.size)
        assertTrue(unknownTips.all { generalTips.contains(it) })
    }

    @Test
    fun `tipOfTheDay is stable for same day and changes on next day`() {
        val day1 = 1_700_000_000_000L // Nov 14 2023
        val sameDayLater = day1 + 3_600_000L // +1 hour
        val nextDay = day1 + DayUtil.DAY_MS // Nov 15 2023
        val tz = TimeZone.getTimeZone("UTC")

        val tipDay1 = CoachTips.tipOfTheDay("Running", day1, tz)
        val tipSameDayLater = CoachTips.tipOfTheDay("Running", sameDayLater, tz)
        val tipNextDay = CoachTips.tipOfTheDay("Running", nextDay, tz)

        assertEquals(tipDay1, tipSameDayLater)
        assertNotEquals(tipDay1, tipNextDay)
    }
}
