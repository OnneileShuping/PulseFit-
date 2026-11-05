package com.example.pulsefit.ui.weekly

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefit.ui.insights.InsightsViewModel
import com.example.pulsefit.ui.theme.PulseCard
import com.example.pulsefit.ui.theme.PulseGradient
import com.example.pulsefit.ui.theme.WarningAmber

@Composable
fun WeeklyGoalScreen(
    viewModel: InsightsViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val weekly = state.weekly

    var sliderValue by remember(weekly.goalMinutes) { mutableFloatStateOf(weekly.goalMinutes.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weekly Goal & Trends",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Track your weekly progress and adjust your activity goal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(24.dp))

        // Goal Ring Canvas
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(180.dp)
        ) {
            val trackColor = MaterialTheme.colorScheme.surfaceVariant
            val strokeWidth = 14.dp
            val density = LocalDensity.current
            val strokePx = with(density) { strokeWidth.toPx() }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val sweepAngle = 360f * weekly.fraction
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = PulseGradient,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${weekly.minutes}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/ ${weekly.goalMinutes} min",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Goal Status Message & Trend Comparison
        if (weekly.goalMet) {
            Text(
                text = "🎉 Weekly goal reached!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "${weekly.remaining} min to go",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(4.dp))

        val changeText = if (weekly.changeVsLastWeek >= 0) {
            "▲ +${weekly.changeVsLastWeek} min vs previous week"
        } else {
            "▼ ${weekly.changeVsLastWeek} min vs previous week"
        }

        Text(
            text = changeText,
            style = MaterialTheme.typography.bodyMedium,
            color = if (weekly.changeVsLastWeek >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(24.dp))

        // 7-Day Bar Chart Section
        PulseCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LAST 7 DAYS",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "Target: ${(weekly.dailyTarget).toInt()} m/day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(16.dp))

                val bars = weekly.bars
                val maxMins = maxOf(60, (bars.maxOfOrNull { it.minutes } ?: 0), weekly.dailyTarget.toInt())
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                val targetLineColor = WarningAmber

                Column(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        val width = size.width
                        val height = size.height
                        val barCount = bars.size
                        val barSpacing = width / (barCount * 2)
                        val barWidth = (width - (barSpacing * (barCount + 1))) / barCount

                        // Draw dailyTarget dashed line
                        val targetY = height - (height * (weekly.dailyTarget / maxMins.toFloat())).coerceIn(0f, height)
                        drawLine(
                            color = targetLineColor,
                            start = Offset(0f, targetY),
                            end = Offset(width, targetY),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Draw daily bars
                        bars.forEachIndexed { i, bar ->
                            val barHeight = (height * (bar.minutes.toFloat() / maxMins)).coerceIn(0f, height)
                            val left = barSpacing + i * (barWidth + barSpacing)
                            val top = height - barHeight
                            val color = if (bar.isToday) secondaryColor else primaryColor

                            if (barHeight > 0) {
                                drawRoundRect(
                                    color = color,
                                    topLeft = Offset(left, top),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        bars.forEach { bar ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${bar.minutes}m",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = bar.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (bar.isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (bar.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Goal Adjustment Slider Card
        PulseCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Adjust Weekly Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current goal: ${sliderValue.toInt()} minutes / week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 30f..600f,
                    steps = 37,
                    onValueChangeFinished = {
                        viewModel.setWeeklyGoal(sliderValue.toInt())
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("30 min", style = MaterialTheme.typography.bodySmall)
                    Text("600 min", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
