package com.example.pulsefit.model

data class Squad(
    val id: String = "",
    val name: String = "",
    val maxMembers: Int = 6,
    val ownerId: String = "",
    val memberCount: Int = 0,
    val currentChallenge: String = "",
    val challengeProgress: Int = 0,
    val challengeGoal: Int = 100
)