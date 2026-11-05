package com.example.pulsefit.ui.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CoachScreen(
    viewModel: com.example.pulsefit.ui.insights.InsightsViewModel,
    onLogWorkout: () -> Unit,
    onOpenWeekly: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val coach = state.coach

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI Coach",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Personalized insights based on your recent activity",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Headline Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = coach.headline,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Load & Fatigue Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LevelCard(
                title = "Training Load",
                level = coach.trainingLoad,
                modifier = Modifier.weight(1f)
            )
            LevelCard(
                title = "Fatigue",
                level = coach.fatigue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Stats Summary Row
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Minutes", value = "${coach.weeklyMinutes}")
                StatItem(label = "Sessions", value = "${coach.sessions}")
                StatItem(label = "Active Days", value = "${coach.activeDays}")
                StatItem(label = "Calories", value = "${coach.weeklyCalories}")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "RECOMMENDED TIPS",
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(Modifier.height(8.dp))

        coach.tips.forEach { tip ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onLogWorkout,
                modifier = Modifier.weight(1f)
            ) {
                Text("+ Log Workout")
            }
            OutlinedButton(
                onClick = onOpenWeekly,
                modifier = Modifier.weight(1f)
            ) {
                Text("Weekly Goal")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "General guidance only, not medical advice.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun LevelCard(
    title: String,
    level: com.example.pulsefit.domain.Level,
    modifier: Modifier = Modifier
) {
    val levelColor = when (level) {
        com.example.pulsefit.domain.Level.LOW -> Color(0xFF2E7D32)
        com.example.pulsefit.domain.Level.MODERATE -> Color(0xFFEF6C00)
        com.example.pulsefit.domain.Level.HIGH -> MaterialTheme.colorScheme.error
    }

    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = level.label,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = levelColor
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
