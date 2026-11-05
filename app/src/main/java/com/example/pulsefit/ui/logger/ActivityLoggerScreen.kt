package com.example.pulsefit.ui.logger

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pulsefit.ui.theme.PulseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLoggerScreen(
    viewModel: ActivityViewModel,
    onSaved: () -> Unit
) {
    var type by remember { mutableStateOf("Running") }
    var duration by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val activityTypes = listOf("Running", "Cycling", "Weightlifting", "Walking")
    val saved by viewModel.saved.collectAsState()

    LaunchedEffect(saved) { if (saved) onSaved() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Log Activity", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = type,
                onValueChange = {},
                readOnly = true,
                label = { Text("Activity Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                activityTypes.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { type = it; expanded = false })
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = duration, onValueChange = { duration = it.filter { c -> c.isDigit() } },
            label = { Text("Duration (seconds)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = distance, onValueChange = { distance = it },
            label = { Text("Distance (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = calories, onValueChange = { calories = it.filter { c -> c.isDigit() } },
            label = { Text("Calories") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = notes, onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        val estimatedXp = (calories.toIntOrNull() ?: 0) / 2
        Text("XP you'll earn: +$estimatedXp XP ⚡", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(16.dp))
        PulseButton(
            onClick = {
                viewModel.save(
                    type,
                    duration.toIntOrNull() ?: 0,
                    distance.toDoubleOrNull() ?: 0.0,
                    calories.toIntOrNull() ?: 0,
                    notes
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Activity") }

        Spacer(Modifier.height(8.dp))
        Text(
            "Saved offline if no network connection",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
