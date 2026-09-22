package com.example.pulsefit.ui.logger

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
        Text("Log Activity", style = MaterialTheme.typography.headlineSmall)
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
        Button(
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
        Text("Saved offline if no network connection",
            style = MaterialTheme.typography.bodySmall)
    }
}