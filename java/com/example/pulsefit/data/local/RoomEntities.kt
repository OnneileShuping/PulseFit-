package com.example.pulsefit.data.local

import androidx.room.Entity
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