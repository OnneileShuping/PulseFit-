package com.example.pulsefit.ui.squads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class LeaderEntry(val name: String, val distance: Int)

@Composable
fun SquadsScreen() {
    val leaders = listOf(
        LeaderEntry("Alex (you)", 38),
        LeaderEntry("Thandi", 31),
        LeaderEntry("Sipho", 29),
        LeaderEntry("Lerato", 22),
        LeaderEntry("Kagiso", 18)
    )

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hill Chasers", style = MaterialTheme.typography.headlineMedium)
        Text("5 / 6 members  •  Active challenge")
        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("CURRENT CHALLENGE", style = MaterialTheme.typography.labelMedium)
                Text("100 km This Week", style = MaterialTheme.typography.titleLarge)
                Text("Ends Sunday midnight — Squad total: 138 km")
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("138 / 100 km ✅ Complete!")
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("LEADERBOARD", style = MaterialTheme.typography.labelMedium)
        LazyColumn(Modifier.weight(1f)) {
            items(leaders) { entry ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(entry.name)
                    Text("${entry.distance} km")
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("Invite Member") }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Leave Squad") }
        }
    }
}