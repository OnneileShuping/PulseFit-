package com.example.pulsefit.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefit.R

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117)), // Dark charcoal background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // The PulseFit logo
            Image(
                painter = painterResource(id = R.drawable.pulsefit_logo),
                contentDescription = "PulseFit Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(220.dp) // adjust size as needed
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "PulseFit",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "TRAIN. EARN. RISE.",
                fontSize = 14.sp,
                color = Color(0xFF4CAF50), // emerald green
                letterSpacing = 4.sp
            )
        }

        // Get Started button at the bottom
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E7D32) // emerald green
            )
        ) {
            Text("Get Started", fontSize = 18.sp, color = Color.White)
        }
    }
}