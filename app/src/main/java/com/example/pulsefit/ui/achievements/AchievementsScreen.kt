package com.example.pulsefit.ui.achievements

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pulsefit.ui.insights.InsightsViewModel
import com.example.pulsefit.ui.theme.PulseButton
import com.example.pulsefit.ui.theme.PulseCard
import com.example.pulsefit.ui.theme.PulseProgressIndicator

@Composable
fun AchievementsScreen(
    viewModel: InsightsViewModel,
    onOpenWeekly: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val g = state.gamification

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Achievements & Badges", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "${g.tier} Tier • ${g.xp} XP • ${g.earnedCount} of ${g.badges.size} Badges Earned",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        // Tier Progress Card
        PulseCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Progress to ${g.nextTier ?: "Max Tier"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                PulseProgressIndicator(
                    progress = g.tierFraction,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                if (g.nextTier != null) {
                    Text(
                        text = "${g.xpToNextTier ?: 0} XP remaining for ${g.nextTier}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Maximum Tier Achieved!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Streak & Shields Card
        PulseCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🔥 ${g.streak.current}-day Streak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Best: ${g.streak.best} days", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🛡️ Shields: ${g.streak.shields}/2", style = MaterialTheme.typography.bodyMedium)
                    Text("Shields Used: ${g.streak.shieldsUsed}", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Streak shields automatically protect your streak if you miss a single workout day.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        PulseButton(
            onClick = onOpenWeekly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Weekly Goal & Trends →")
        }

        Spacer(Modifier.height(16.dp))
        Text("BADGES", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(g.badges) { badge ->
                PulseCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(badge.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(badge.description, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                text = if (badge.earned) "✅" else "🔒",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        PulseProgressIndicator(
                            progress = badge.fraction,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${badge.current} / ${badge.target}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
