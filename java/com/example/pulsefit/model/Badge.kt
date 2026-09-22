package com.example.pulsefit.model

data class Badge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val criteria: String = "",
    val earned: Boolean = false,
    val earnedAt: Long? = null
)