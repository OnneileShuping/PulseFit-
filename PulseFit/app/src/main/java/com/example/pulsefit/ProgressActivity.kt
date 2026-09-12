package com.example.pulsefit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsefit.databinding.ActivityProgressBinding

/**
 * ProgressActivity - Screen 5
 * Displays earned badges, XP progress, streak shields, and past activity history.
 */
class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private val TAG = "ProgressActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Progress/Achievements screen initialized")

        // Back button functionality if you add a top toolbar, otherwise bottom nav handles routing
        binding.btnBack?.setOnClickListener {
            finish()
        }

        // Example dynamic progress update matching the PDF mockup
        binding.progressBarPlatinum.progress = 70
    }
}