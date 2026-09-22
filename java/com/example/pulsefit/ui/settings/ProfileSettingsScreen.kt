package com.example.pulsefit.ui.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefit.R
import com.example.pulsefit.util.LocaleHelper

// ============================================================
//  TOP-LEVEL HELPER — must be outside the @Composable function
// ============================================================
private fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    userEmail: String,
    userName: String,
    viewModel: SettingsViewModel,
    onLogout: () -> Unit
) {
    val settings by viewModel.state.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // ------- Dialog visibility flags -------
    var showChangePassword by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var showPersonalInfo by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // ------- React to action results -------
    LaunchedEffect(actionState) {
        when (val s = actionState) {
            is SettingsViewModel.ActionState.Success -> {
                snackbarHostState.showSnackbar(s.message)
                if (s.message.contains("deleted", ignoreCase = true)) {
                    onLogout()
                }
                viewModel.clearAction()
            }
            is SettingsViewModel.ActionState.Error -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.clearAction()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F8F3))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ---------------- Header ----------------
            Text(
                stringResource(R.string.profile_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            // ---------------- Avatar card ----------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                userName.firstOrNull()?.uppercase() ?: "U",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(userName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            userEmail.ifBlank { "Not signed in" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ---------------- Preferences ----------------
            SectionHeader(stringResource(R.string.pref_header))
            SettingsRow(
                label = stringResource(R.string.pref_units),
                value = settings.units
            )
            SettingsSwitchRow(
                label = stringResource(R.string.pref_theme),
                checked = settings.theme == "Dark",
                onCheckedChange = { viewModel.update(theme = if (it) "Dark" else "Light") }
            )

            // Language row — CLICKABLE, opens picker
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.pref_language),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    settings.language,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SettingsSwitchRow(
                label = stringResource(R.string.pref_notifications),
                checked = settings.notificationsEnabled,
                onCheckedChange = { viewModel.update(notifications = it) }
            )

            Spacer(Modifier.height(24.dp))

            // ---------------- Personal Information ----------------
            SectionHeader(stringResource(R.string.personal_header))
            ActionRow(
                icon = Icons.Default.Person,
                label = stringResource(R.string.personal_details),
                onClick = { showPersonalInfo = true }
            )
            ActionRow(
                icon = Icons.Default.Email,
                label = stringResource(R.string.personal_email),
                trailing = userEmail.ifBlank { "-" },
                onClick = { showPersonalInfo = true }
            )

            Spacer(Modifier.height(24.dp))

            // ---------------- Account ----------------
            SectionHeader(stringResource(R.string.account_header))
            ActionRow(
                icon = Icons.Default.Lock,
                label = stringResource(R.string.account_change_password),
                onClick = { showChangePassword = true }
            )
            ActionRow(
                icon = Icons.Default.Share,
                label = stringResource(R.string.account_export),
                onClick = { showExportDialog = true }
            )
            ActionRow(
                icon = Icons.Default.Info,
                label = stringResource(R.string.account_terms),
                onClick = { showTerms = true }
            )
            ActionRow(
                icon = Icons.Default.Delete,
                label = stringResource(R.string.account_delete),
                tint = Color(0xFFD32F2F),
                onClick = { showDeleteAccount = true }
            )

            Spacer(Modifier.height(24.dp))

            // ---------------- Logout ----------------
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(stringResource(R.string.account_logout), fontSize = 16.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // ============================================================
    //                      DIALOGS
    // ============================================================

    // ---------------- Change Password ----------------
    if (showChangePassword) {
        ChangePasswordDialog(
            onDismiss = { showChangePassword = false },
            onSubmit = { current, newPass, confirm ->
                viewModel.changePassword(current, newPass, confirm)
                showChangePassword = false
            }
        )
    }

    // ---------------- Delete Account ----------------
    if (showDeleteAccount) {
        DeleteAccountDialog(
            onDismiss = { showDeleteAccount = false },
            onConfirm = { password ->
                viewModel.deleteAccount(password)
                showDeleteAccount = false
            }
        )
    }

    // ---------------- Personal Info ----------------
    if (showPersonalInfo) {
        PersonalInfoDialog(
            userName = userName,
            userEmail = userEmail,
            onDismiss = { showPersonalInfo = false }
        )
    }

    // ---------------- Terms & Conditions ----------------
    if (showTerms) {
        TermsDialog(onDismiss = { showTerms = false })
    }

    // ---------------- Export Data ----------------
    if (showExportDialog) {
        ExportDataDialog(
            dataProvider = { viewModel.exportData() },
            onShare = { data ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "PulseFit Data Export")
                    putExtra(Intent.EXTRA_TEXT, data)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Export PulseFit data"))
                showExportDialog = false
            },
            onDismiss = { showExportDialog = false }
        )
    }

    // ---------------- Language Picker ----------------
    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentCode = LocaleHelper.getSavedLanguage(context),
            onDismiss = { showLanguageDialog = false },
            onSelect = { option ->
                LocaleHelper.setLanguage(context, option.code)
                viewModel.update(language = option.nativeName)
                showLanguageDialog = false
                // Recreate the Activity so all strings re-resolve with the new locale
                context.findActivity()?.recreate()
            }
        )
    }
}

// ============================================================
//                   Small helper composables
// ============================================================

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    trailing: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint)
        Spacer(Modifier.width(16.dp))
        Text(label, color = tint, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.weight(1f))
        if (trailing != null) {
            Text(
                trailing,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(4.dp))
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================
//                        Dialogs
// ============================================================

@Composable
private fun LanguagePickerDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onSelect: (LocaleHelper.LanguageOption) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_title)) },
        text = {
            Column {
                LocaleHelper.supportedLanguages.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentCode == option.code,
                            onClick = { onSelect(option) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                option.nativeName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (option.nativeName != option.displayName) {
                                Text(
                                    option.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.language_cancel))
            }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (current: String, newPass: String, confirm: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Current password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirm new password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Minimum 6 characters. You will be signed out on other devices.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(current, newPass, confirm) }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (password: String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account?") },
        text = {
            Column {
                Text(
                    "This will permanently delete your PulseFit account and all " +
                            "associated data (activities, badges, squads). This action " +
                            "cannot be undone.",
                    color = Color(0xFFD32F2F)
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Confirm with your password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(password) }) {
                Text("Delete Forever", color = Color(0xFFD32F2F))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun PersonalInfoDialog(
    userName: String,
    userEmail: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Personal Information") },
        text = {
            Column {
                Text("Display Name", style = MaterialTheme.typography.labelMedium)
                Text(userName, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))
                Text("Email", style = MaterialTheme.typography.labelMedium)
                Text(userEmail.ifBlank { "-" }, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Your email is managed by your sign-in provider (Firebase or Google). " +
                            "To change it, contact support.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun TermsDialog(onDismiss: () -> Unit) {
    val terms = """
        PulseFit — Terms & Conditions
        Last updated: August 2026

        1. ACCEPTANCE
        By using PulseFit you agree to these terms. If you disagree, do not use the app.

        2. ACCOUNT
        You are responsible for keeping your password secure. You must not share
        your credentials or one-time codes with anyone, including support staff.

        3. HEALTH DISCLAIMER
        PulseFit is a fitness tracking tool. It does not provide medical advice.
        Consult a qualified healthcare professional before starting any exercise
        programme. Stop immediately if you feel unwell.

        4. DATA & PRIVACY
        • Activity data is stored locally (RoomDB) and synced to our secure backend.
        • Passwords are hashed and never stored in plain text.
        • Communication uses HTTPS / TLS 1.3 with JWT authentication.
        • You may export your data at any time from the Profile screen.
        • Deleting your account removes all associated data within 30 days.

        5. ACCEPTABLE USE
        You agree not to misuse PulseFit, reverse-engineer it, or submit false data.

        6. CHANGES
        We may update these terms. Continued use after changes constitutes acceptance.

        7. CONTACT
        Questions? Reach us at support@pulsefit.app.
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Terms & Conditions") },
        text = {
            Column(
                Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(terms, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("I Understand") }
        }
    )
}

@Composable
private fun ExportDataDialog(
    dataProvider: () -> String,
    onShare: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val data = remember { dataProvider() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export My Data") },
        text = {
            Column(
                Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Preview of the data we will export:",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    data,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onShare(data) }) {
                Text("Share / Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}