package com.example.pulsefit.ui.achievements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Achievement(val name: String, val description: String, val earned: Boolean)

@Composable
fun AchievementsScreen(xp: Int = 2450, tier: String = "Gold") {
    val badges = listOf(
        Achievement("Iron Runner", "Complete 50 runs", true),
        Achievement("Consistency Master", "10-day streak", true),
        Achievement("Streak Shield x2", "Protect a streak twice", true),
        Achievement("Century Cyclist", "Cycle 100 km total", false),
        Achievement("Squad Champion", "Win a squad challenge", false)
    )

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Achievements", style = MaterialTheme.typography.headlineMedium)
        Text("$tier Tier • $xp XP earned")
        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(badges) { badge ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(badge.name, style = MaterialTheme.typography.titleMedium)
                            Text(badge.description, style = MaterialTheme.typography.bodySmall)
                        }
                        if (badge.earned) Text("✅") else Text("🔒")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Progress to Platinum", style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(
            progress = { (xp % 3000) / 3000f },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Text("${3000 - (xp % 3000)} XP remaining — 70%")
    }
}