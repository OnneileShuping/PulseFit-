package com.example.pulsefit.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pulsefit.ui.theme.PulseButton
import com.example.pulsefit.ui.theme.PulseCard
import com.example.pulsefit.ui.theme.PulseProgressIndicator

@Composable
fun HomeScreen(
    userName: String,
    onLogWorkout: () -> Unit,
    onOpenSquads: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenAiCoach: () -> Unit,
    onOpenWeekly: () -> Unit,
    onOpenHabits: () -> Unit = {},
    viewModel: HomeViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Good morning 👋", style = MaterialTheme.typography.titleMedium)
        Text(userName, style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        PulseCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("XP Progress: ${state.xp} XP", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                PulseProgressIndicator(
                    progress = (state.xp % 3000) / 3000f,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text("🔥 ${state.streak}-day streak  •  🛡️ ${state.shields} shield(s)  •  🏅 ${state.tier} Tier")
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PulseButton(onClick = onLogWorkout, modifier = Modifier.weight(1f)) {
                Text("+ Log Workout")
            }
            OutlinedButton(
                onClick = onOpenSquads,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Join Squad")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("TODAY'S ACTIVITY", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))

        if (state.activities.isEmpty()) {
            Box(modifier = Modifier.weight(1f)) {
                Text("No activities yet — log your first workout!", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.activities.take(5)) { act ->
                    PulseCard(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(act.type, style = MaterialTheme.typography.titleMedium)
                            Text("${act.distanceKm} km • ${act.durationSeconds / 60} min • ${act.calories} kcal", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        PulseCard(
            onClick = onOpenAiCoach,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("AI Coach", style = MaterialTheme.typography.titleMedium)
                Text(state.aiTipHeadline, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(state.aiTipOfTheDay, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tap for your full analysis →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onOpenHabits) { Text("Habits →") }
            TextButton(onClick = onOpenAchievements) { Text("Achievements →") }
            TextButton(onClick = onOpenWeekly) { Text("Weekly Goal →") }
        }
    }
}
