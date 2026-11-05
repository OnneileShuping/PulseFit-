package com.example.pulsefit.ui.squads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pulsefit.ui.theme.PulseButton
import com.example.pulsefit.ui.theme.PulseCard
import com.example.pulsefit.ui.theme.PulseProgressIndicator

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
        Text("5 / 6 members  •  Active challenge", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        PulseCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("CURRENT CHALLENGE", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Text("100 km This Week", style = MaterialTheme.typography.titleLarge)
                Text("Ends Sunday midnight — Squad total: 138 km", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                PulseProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                Text("138 / 100 km ✅ Complete!", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("LEADERBOARD", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(leaders) { entry ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(entry.name, style = MaterialTheme.typography.bodyLarge)
                    Text("${entry.distance} km", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PulseButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Invite Member") }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f).height(52.dp)) { Text("Leave Squad") }
        }
    }
}
