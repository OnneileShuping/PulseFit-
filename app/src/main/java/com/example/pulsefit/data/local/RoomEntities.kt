package com.example.pulsefit.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val localId: String,
    val userId: String,
    val type: String,
    val durationSeconds: Int,
    val distanceKm: Double,
    val calories: Int,
    val notes: String,
    val timestamp: Long,
    val syncStatus: String = "pending"
)

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val email: String,
    val xp: Int,
    val tier: String
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val userId: String,
    val units: String,
    val theme: String,
    val language: String,
    val notificationsEnabled: Boolean
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val badgeId: String,
    val name: String,
    val description: String,
    val earned: Boolean,
    val earnedAt: Long?
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val daysOfWeekMask: Int, // Bit 0 = Mon .. Bit 6 = Sun
    val createdAt: Long,
    val archived: Boolean = false
)

@Entity(
    tableName = "habit_checks",
    primaryKeys = ["habitId", "dayIndex"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId"])]
)
data class HabitCheckEntity(
    val habitId: String,
    val dayIndex: Long,
    val userId: String
)
