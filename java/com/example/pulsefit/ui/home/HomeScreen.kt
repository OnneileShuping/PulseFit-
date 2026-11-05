package com.example.pulsefit.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    userName: String,
    onLogWorkout: () -> Unit,
    onOpenSquads: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenAiCoach: () -> Unit,
    viewModel: HomeViewModel
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Good morning 👋", style = MaterialTheme.typography.titleMedium)
        // 👇 Real user name now shown here
        Text(userName, style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("XP Progress: ${state.xp} XP", style = MaterialTheme.typography.bodyLarge)
                LinearProgressIndicator(
                    progress = { (state.xp % 3000) / 3000f },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("🔥 ${state.streak}-day streak  •  🏅 ${state.tier} Tier")
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onLogWorkout, modifier = Modifier.weight(1f)) {
                Text("+ Log Workout")
            }
            OutlinedButton(onClick = onOpenSquads, modifier = Modifier.weight(1f)) {
                Text("Join Squad")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("TODAY'S ACTIVITY", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))

        if (state.activities.isEmpty()) {
            Text("No activities yet — log your first workout!")
        } else {
            LazyColumn {
                items(state.activities.take(5)) { act ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(act.type, style = MaterialTheme.typography.titleMedium)
                            Text("${act.distanceKm} km • ${act.durationSeconds / 60} min • ${act.calories} kcal")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedCard(
            onClick = onOpenAiCoach,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text("AI Coach", style = MaterialTheme.typography.titleMedium)
                Text(state.aiTip)
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onOpenAchievements) { Text("View Achievements →") }
    }
}