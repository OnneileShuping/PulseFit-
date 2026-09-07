package com.example.pulsefit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsefit.databinding.ActivityMainBinding

/**
 * MainActivity - Screen 3 (Home Dashboard)
 * Primary user hub displaying streaks, XP, action buttons, and bottom navigation.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding to access layout UI components
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Home Dashboard loaded successfully")

        // ----------------------------------------------------
        // DASHBOARD BUTTON ACTIONS
        // ----------------------------------------------------

        // Action button to navigate directly to Screen 4 (Activity Logger)
        binding.btnLogWorkout.setOnClickListener {
            Log.d(TAG, "Log Workout tapped - Navigating to Screen 4")

            // Intent to open Screen 4 (LogActivity)
            val intent = Intent(this, LogActivity::class.java)
            startActivity(intent)
        }

        // Action button for Social Squads (Kept intact)
        binding.btnJoinSquad.setOnClickListener {
            Log.d(TAG, "Join Squad tapped")
            Toast.makeText(this, "Navigating to Social Squads...", Toast.LENGTH_SHORT).show()
        }

        // ----------------------------------------------------
        // BOTTOM NAVIGATION ITEM LISTENERS
        // ----------------------------------------------------

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Log.d(TAG, "Home tab selected")
                    true
                }

                // Bottom Navigation item linking to Screen 4 (Activity Logger)
                R.id.nav_log -> {
                    Log.d(TAG, "Log tab selected - Navigating to Screen 4")

                    // Open Screen 4 (LogActivity) via bottom navigation
                    val intent = Intent(this, LogActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_squads -> {
                    Log.d(TAG, "Squads tab selected")
                    Toast.makeText(this, "Social Squads", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_progress -> {
                    Log.d(TAG, "Progress tab selected")
                    Toast.makeText(this, "Achievements & Progress", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_profile -> {
                    Log.d(TAG, "Profile tab selected")
                    Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }
}