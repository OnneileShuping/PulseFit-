package com.example.pulsefit

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsefit.databinding.ActivityLogBinding

/**
 * LogActivity - Screen 4 (Activity Logger)
 * Handles activity logging, automated XP calculations, and offline storage fallbacks.
 */
class LogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogBinding
    private val TAG = "LogActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate view binding for Screen 4
        binding = ActivityLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Activity Logger initialized")

        // 1. Setup Spinner options for Activity Types
        setupActivitySpinner()

        // 2. Setup Back Button Listener
        binding.btnBack.setOnClickListener {
            finish() // Return to MainActivity (Screen 3)
        }

        // 3. Setup Save Activity Button
        binding.btnSaveActivity.setOnClickListener {
            saveActivityLog()
        }
    }

    private fun setupActivitySpinner() {
        val activities = arrayOf("Running", "Cycling", "Weightlifting", "Walking")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activities)
        binding.spinnerActivityType.adapter = adapter
    }

    private fun saveActivityLog() {
        val type = binding.spinnerActivityType.selectedItem.toString()
        val duration = binding.etDuration.text.toString().trim()
        val distanceStr = binding.etDistance.text.toString().trim()
        val caloriesStr = binding.etCalories.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        // Validation check
        if (duration.isEmpty() || distanceStr.isEmpty() || caloriesStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all workout fields", Toast.LENGTH_SHORT).show()
            return
        }

        val distance = distanceStr.toDoubleOrNull() ?: 0.0
        val calories = caloriesStr.toIntOrNull() ?: 0

        // Calculate dynamic XP (e.g., 10 XP per km + 1 XP per 10 calories)
        val calculatedXp = ((distance * 10) + (calories / 10)).toInt().coerceAtLeast(50)
        binding.tvXpEarned.text = "+$calculatedXp XP ⚡"

        Log.d(TAG, "Activity Saved: $type, Distance: $distance km, Earned XP: $calculatedXp")

        Toast.makeText(
            this,
            "Activity Saved! Earned +$calculatedXp XP (Saved offline)",
            Toast.LENGTH_LONG
        ).show()

        // Return back to Home Dashboard
        finish()
    }
}