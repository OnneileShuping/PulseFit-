package com.example.pulsefit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsefit.databinding.ActivityProfileBinding

/**
 * ProfileActivity - Screen 8
 * Allows users to manage profile settings, including measurement units, app theme,
 * language preferences, notification toggles, and logout.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Profile settings loaded")

        setupSpinners()

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "Notifications toggled: $isChecked")
        }

        binding.btnLogOut.setOnClickListener {
            Log.d(TAG, "User logged out - Navigating to LoginActivity")
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            // Clear the backstack so the user can't press back to return to the profile
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupSpinners() {
        // Units Spinner
        val units = arrayOf("Metric", "Imperial")
        val unitsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        binding.spinnerUnits.adapter = unitsAdapter

        // Language Spinner (Supporting localization plan)
        val languages = arrayOf("English", "isiZulu", "Setswana")
        val langAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        binding.spinnerLanguage.adapter = langAdapter
    }
}