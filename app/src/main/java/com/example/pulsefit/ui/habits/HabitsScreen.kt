package com.example.pulsefit.ui.habits

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefit.data.local.HabitEntity
import com.example.pulsefit.domain.DayStatus
import com.example.pulsefit.domain.HabitStats
import com.example.pulsefit.ui.theme.PulseButton
import com.example.pulsefit.ui.theme.PulseCard
import com.example.pulsefit.ui.theme.PulseGradient
import com.example.pulsefit.ui.theme.PulseProgressIndicator

private val PRESET_EMOJIS = listOf(
    "💧", "🧘", "🏃", "😴", "🥗", "📚", "🚴", "🏋️",
    "🚶", "🍏", "💊", "🧠", "☀️", "🌊", "📱", "🎯",
    "✍️", "🧹", "🎨", "🎵", "🐕", "🥑", "🧗", "🔥"
)

private val PRESET_COLORS = listOf(
    Color(0xFF00B8F0), // Cyan
    Color(0xFF28B878), // Green
    Color(0xFF7CF2C0), // Mint
    Color(0xFFFFB347), // Amber
    Color(0xFFFF6B6B), // Coral
    Color(0xFFA88BFA)  // Violet
)

private val WEEKDAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val today = viewModel.todayIndex

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var deletingHabitId by remember { mutableStateOf<String?>(null) }
    var showNotTodaySection by remember { mutableStateOf(false) }

    val activeHabits = state.habits.filter { !it.archived }
    val (todayScheduled, notTodayScheduled) = activeHabits.partition { HabitStats.isScheduled(it, today) }

    val checkedDaysMap = remember(state.checks) {
        state.checks.groupBy { it.habitId }
            .mapValues { entry -> entry.value.map { it.dayIndex }.toSet() }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingHabit = null
                    showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Habit Tracker", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Build positive routines every day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // Progress Header Card
            PulseCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S PROGRESS",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "${state.todayDone} / ${state.todayTotal} done",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    val progress = if (state.todayTotal > 0) state.todayDone.toFloat() / state.todayTotal else 0f
                    PulseProgressIndicator(progress = progress)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (activeHabits.isEmpty()) {
                // Empty state with starter habit recommendations
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No habits tracked yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Add custom habits or pick a recommended starter below:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))

                    PulseButton(
                        onClick = { viewModel.addStarterHabits() },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("✨ Add 3 Starter Habits")
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text("TODAY'S HABITS", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                    }

                    if (todayScheduled.isEmpty()) {
                        item {
                            Text(
                                text = "No habits scheduled for today 🎉",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        items(todayScheduled, key = { it.id }) { habit ->
                            val checks = checkedDaysMap[habit.id] ?: emptySet()
                            val isChecked = today in checks
                            val streak = HabitStats.streak(habit, checks, today)
                            val last7 = HabitStats.last7Days(habit, checks, today)

                            HabitItemCard(
                                habit = habit,
                                isChecked = isChecked,
                                streak = streak.current,
                                last7 = last7,
                                onToggle = { viewModel.toggleTodayCheck(habit.id, !isChecked) },
                                onEdit = {
                                    editingHabit = habit
                                    showBottomSheet = true
                                },
                                onDelete = { deletingHabitId = habit.id }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    if (notTodayScheduled.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showNotTodaySection = !showNotTodaySection }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "NOT SCHEDULED TODAY (${notTodayScheduled.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (showNotTodaySection) "▲ Hide" else "▼ Show",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (showNotTodaySection) {
                            items(notTodayScheduled, key = { it.id }) { habit ->
                                val checks = checkedDaysMap[habit.id] ?: emptySet()
                                val streak = HabitStats.streak(habit, checks, today)
                                val last7 = HabitStats.last7Days(habit, checks, today)

                                HabitItemCard(
                                    habit = habit,
                                    isChecked = false,
                                    streak = streak.current,
                                    last7 = last7,
                                    isTodayScheduled = false,
                                    onToggle = {},
                                    onEdit = {
                                        editingHabit = habit
                                        showBottomSheet = true
                                    },
                                    onDelete = { deletingHabitId = habit.id }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet for Create / Edit Habit
    if (showBottomSheet) {
        HabitEditorBottomSheet(
            habit = editingHabit,
            onDismiss = { showBottomSheet = false },
            onSave = { name, emoji, colorHex, daysMask ->
                val err = viewModel.saveHabit(name, emoji, colorHex, daysMask, editingHabit?.id)
                if (err == null) {
                    showBottomSheet = false
                }
                err
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingHabitId != null) {
        AlertDialog(
            onDismissRequest = { deletingHabitId = null },
            title = { Text("Delete Habit?") },
            text = { Text("This will permanently remove the habit and all history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletingHabitId?.let { viewModel.deleteHabit(it) }
                        deletingHabitId = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingHabitId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HabitItemCard(
    habit: HabitEntity,
    isChecked: Boolean,
    streak: Int,
    last7: List<DayStatus>,
    isTodayScheduled: Boolean = true,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isChecked) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "scale"
    )

    val habitColor = parseColor(habit.colorHex)

    PulseCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(habitColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = habit.emoji, fontSize = 22.sp)
            }

            Spacer(Modifier.width(12.dp))

            // Info column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (streak > 0) {
                        Text("🔥 $streak streak  •  ", style = MaterialTheme.typography.bodySmall)
                    }
                    // Mini 7-day dot row
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        last7.forEachIndexed { i, status ->
                            val dotColor = when (status) {
                                DayStatus.DONE -> Color(0xFF28B878)
                                DayStatus.MISSED -> Color(0xFFFF6B6B)
                                DayStatus.NOT_SCHEDULED -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                DayStatus.TODAY_PENDING -> MaterialTheme.colorScheme.primary
                            }
                            val isTodayDot = i == 6
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                                    .then(
                                        if (isTodayDot && status == DayStatus.TODAY_PENDING)
                                            Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        else Modifier
                                    )
                            )
                        }
                    }
                }
            }

            // Checkbox
            if (isTodayScheduled) {
                Box(
                    modifier = Modifier
                        .scale(scale)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isChecked) Color(0xFF28B878) else Color.Transparent)
                        .border(
                            border = BorderStroke(
                                2.dp,
                                if (isChecked) Color(0xFF28B878) else MaterialTheme.colorScheme.outline
                            ),
                            shape = CircleShape
                        )
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isChecked) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checked",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Overflow Menu
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HabitEditorBottomSheet(
    habit: HabitEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int) -> String?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(habit?.name ?: "") }
    var selectedEmoji by remember { mutableStateOf(habit?.emoji ?: "💧") }
    var selectedColor by remember { mutableStateOf(habit?.colorHex ?: "#00B8F0") }
    var daysMask by remember { mutableIntStateOf(habit?.daysOfWeekMask ?: 127) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = if (habit == null) "Create Habit" else "Edit Habit",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit Name (e.g., Drink 2L water)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Emoji Picker
            Text("CHOOSE EMOJI", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PRESET_EMOJIS.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (selectedEmoji == emoji) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Color Picker
            Text("CHOOSE COLOR", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PRESET_COLORS.forEach { color ->
                    val colorHex = "#" + Integer.toHexString(color.value.toInt()).takeLast(6).uppercase()
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (selectedColor.equals(colorHex, ignoreCase = true))
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = colorHex }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Weekday Selector
            Text("SCHEDULE DAYS", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WEEKDAY_LABELS.forEachIndexed { bit, label ->
                    val isSelected = (daysMask and (1 shl bit)) != 0
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            daysMask = if (isSelected) daysMask and (1 shl bit).inv()
                            else daysMask or (1 shl bit)
                        },
                        label = { Text(label, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val err = onSave(name, selectedEmoji, selectedColor, daysMask)
                        errorMessage = err
                    }
                ) {
                    Text("Save Habit")
                }
            }
        }
    }
}

private fun parseColor(colorHex: String): Color {
    return try {
        val clean = colorHex.removePrefix("#")
        val colorInt = android.graphics.Color.parseColor("#$clean")
        Color(colorInt)
    } catch (e: Exception) {
        Color(0xFF00B8F0)
    }
}
