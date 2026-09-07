package com.example.pulsefit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsefit.databinding.ActivitySplashBinding

/**
 * SplashActivity - Screen 1
 * Handles initial onboarding and navigation to LoginActivity.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Splash screen initialized successfully")

        binding.btnGetStarted.setOnClickListener {
            Log.d(TAG, "Navigating from Splash to LoginActivity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}