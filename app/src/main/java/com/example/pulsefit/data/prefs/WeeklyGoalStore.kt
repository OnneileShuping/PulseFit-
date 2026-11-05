package com.example.pulsefit.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.pulsefit.domain.WeeklyStats

interface WeeklyGoalStore {
    fun getWeeklyGoalMinutes(): Int
    fun setWeeklyGoalMinutes(minutes: Int)
}

class SharedPrefsWeeklyGoalStore(context: Context) : WeeklyGoalStore {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pulsefit_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_WEEKLY_GOAL = "weekly_goal_minutes"
        private const val DEFAULT_GOAL = 150
    }

    override fun getWeeklyGoalMinutes(): Int {
        val raw = prefs.getInt(KEY_WEEKLY_GOAL, DEFAULT_GOAL)
        return WeeklyStats.clampGoal(raw)
    }

    override fun setWeeklyGoalMinutes(minutes: Int) {
        val clamped = WeeklyStats.clampGoal(minutes)
        prefs.edit().putInt(KEY_WEEKLY_GOAL, clamped).apply()
    }
}
