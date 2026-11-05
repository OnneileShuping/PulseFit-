package com.example.pulsefit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pulsefit.databinding.ActivityLoginBinding

/**
 * LoginActivity - Screen 2
 * Handles authentication validation before entering the main dashboard.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            Log.d(TAG, "Attempting login for email: $email")

            if (email.isEmpty() || password.isEmpty()) {
                Log.w(TAG, "Validation failed: Empty fields detected")
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Authentication successful for user: $email")
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnRegister.setOnClickListener {
            Log.d(TAG, "Register flow triggered")
            Toast.makeText(this, "Register button clicked", Toast.LENGTH_SHORT).show()
        }
    }
}