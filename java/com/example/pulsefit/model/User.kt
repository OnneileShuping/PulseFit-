package com.example.pulsefit.model

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val xp: Int = 0,
    val tier: String = "Bronze"
)