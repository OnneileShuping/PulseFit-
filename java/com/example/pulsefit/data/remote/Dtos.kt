package com.example.pulsefit.data.remote

// Auth
data class RegisterRequest(val email: String, val username: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class AuthResponse(val token: String, val user: UserDto)
data class UserDto(
    val id: String, val username: String, val email: String,
    val xp: Int, val tier: String
)

// Settings
data class SettingsDto(
    val units: String, val theme: String,
    val language: String, val notificationsEnabled: Boolean
)

// Activities
data class ActivityDto(
    val id: String,
    val type: String,
    val durationSeconds: Int,
    val distanceKm: Double,
    val calories: Int,
    val timestamp: Long
)
data class ActivityResponse(
    val activity: ActivityDto,
    val xpGained: Int,
    val newBadges: List<String>
)

// Offline Sync
data class SyncRequest(
    val deviceId: String,
    val lastSync: String,
    val activities: List<ActivityDto>,
    val deletedIds: List<String>
)
data class SyncResponse(
    val serverActivities: List<ActivityDto>,
    val conflicts: List<ActivityDto>,
    val xpGained: Int,
    val newBadges: List<String>
)

// AI Insights
data class AiInsightsDto(
    val trainingLoad: String,
    val fatigue: String,
    val recommendation: String
)