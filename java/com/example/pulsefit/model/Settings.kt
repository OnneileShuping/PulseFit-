package com.example.pulsefit.model

data class Settings(
    val units: String = "Metric",       // Metric | Imperial
    val theme: String = "Light",        // Light | Dark
    val language: String = "English",   // English | isiZulu | Setswana
    val notificationsEnabled: Boolean = true
)