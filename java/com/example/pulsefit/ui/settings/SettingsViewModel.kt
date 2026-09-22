package com.example.pulsefit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.local.SettingsEntity
import com.example.pulsefit.data.repository.AuthRepository
import com.example.pulsefit.data.repository.PulseFitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class SettingsViewModel(
    private val repo: PulseFitRepository,
    private val authRepo: AuthRepository,
    private val userId: String
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsEntity(userId, "Metric", "Light", "English", true)
    )
    val state: StateFlow<SettingsEntity> = _state

    // Generic action result (message + success flag)
    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState

    sealed class ActionState {
        object Idle : ActionState()
        object Loading : ActionState()
        data class Success(val message: String) : ActionState()
        data class Error(val message: String) : ActionState()
    }

    init {
        viewModelScope.launch {
            repo.observeSettings(userId).collect { s ->
                if (s != null) _state.value = s
            }
        }
    }

    fun update(
        units: String? = null,
        theme: String? = null,
        language: String? = null,
        notifications: Boolean? = null
    ) {
        val current = _state.value
        val updated = current.copy(
            units = units ?: current.units,
            theme = theme ?: current.theme,
            language = language ?: current.language,
            notificationsEnabled = notifications ?: current.notificationsEnabled
        )
        _state.value = updated
        viewModelScope.launch { repo.saveSettings(userId, updated) }
    }

    fun clearAction() { _actionState.value = ActionState.Idle }

    // ---------------- CHANGE PASSWORD ----------------
    fun changePassword(current: String, newPass: String, confirm: String) {
        if (current.isBlank() || newPass.isBlank()) {
            _actionState.value = ActionState.Error("All fields are required")
            return
        }
        if (newPass.length < 6) {
            _actionState.value = ActionState.Error("New password must be at least 6 characters")
            return
        }
        if (newPass != confirm) {
            _actionState.value = ActionState.Error("Passwords do not match")
            return
        }
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            val result = authRepo.changePassword(current, newPass)
            _actionState.value = if (result.isSuccess)
                ActionState.Success("Password updated successfully")
            else
                ActionState.Error(result.exceptionOrNull()?.message ?: "Failed to update password")
        }
    }

    // ---------------- DELETE ACCOUNT ----------------
    fun deleteAccount(password: String) {
        if (password.isBlank()) {
            _actionState.value = ActionState.Error("Password is required")
            return
        }
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            val result = authRepo.deleteAccount(password)
            _actionState.value = if (result.isSuccess)
                ActionState.Success("Account deleted")
            else
                ActionState.Error(result.exceptionOrNull()?.message ?: "Failed to delete account")
        }
    }

    // ---------------- EXPORT DATA ----------------
    fun exportData(): String = authRepo.exportUserData()
}