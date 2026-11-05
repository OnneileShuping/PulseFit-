package com.example.pulsefit.model

data class Activity(
    val id: String = "",
    val userId: String = "",
    val type: String = "",           // Running, Cycling, Weightlifting, Walking
    val durationSeconds: Int = 0,
    val distanceKm: Double = 0.0,
    val calories: Int = 0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: String = "pending" // pending | synced
)