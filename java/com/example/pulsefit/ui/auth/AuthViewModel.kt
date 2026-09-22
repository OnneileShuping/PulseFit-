package com.example.pulsefit.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(isLoggedIn = repo.isLoggedIn))
    val state: StateFlow<AuthUiState> = _state

    // ---------------- EMAIL LOGIN ----------------
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Fields cannot be empty")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repo.login(email, password)
            _state.value = if (result.isSuccess) {
                _state.value.copy(isLoading = false, isLoggedIn = true)
            } else {
                _state.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    // ---------------- EMAIL REGISTER ----------------
    fun register(email: String, username: String, password: String) {
        if (email.isBlank() || username.isBlank()) {
            _state.value = _state.value.copy(error = "Email and username are required")
            return
        }
        if (password.length < 6) {
            _state.value = _state.value.copy(error = "Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repo.register(email, username, password)
            _state.value = if (result.isSuccess) {
                _state.value.copy(isLoading = false, isLoggedIn = true)
            } else {
                _state.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    // ---------------- GOOGLE SIGN-IN ----------------
    fun signInWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val authResult = repo.signInWithGoogle(idToken)

                    _state.value = if (authResult.isSuccess) {
                        _state.value.copy(isLoading = false, isLoggedIn = true)
                    } else {
                        _state.value.copy(
                            isLoading = false,
                            error = authResult.exceptionOrNull()?.message
                                ?: "Google Sign-In failed"
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Unexpected credential type returned"
                    )
                }
            } catch (e: GetCredentialException) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Google Sign-In cancelled"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Google Sign-In error"
                )
            }
        }
    }

    // ---------------- LOGOUT ----------------
    fun logout() {
        repo.logout()
        _state.value = AuthUiState(isLoggedIn = false)
    }
}