package com.example.pulsefit.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = currentUser != null

    // ---------------- EMAIL / PASSWORD ----------------
    suspend fun register(
        email: String,
        username: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user
                ?: return Result.failure(Exception("Registration failed: null user"))
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(username.trim())
                .build()
            user.updateProfile(updates).await()
            Result.success(auth.currentUser ?: user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user
                ?: return Result.failure(Exception("Login failed: null user"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- GOOGLE ----------------
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
                ?: return Result.failure(Exception("Google Sign-In failed: null user"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- CHANGE PASSWORD ----------------
    /**
     * Re-authenticates with the current password, then updates to the new one.
     * Required by Firebase for security-sensitive operations.
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No user signed in"))
            val email = user.email
                ?: return Result.failure(Exception("User has no email"))

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- DELETE ACCOUNT ----------------
    /**
     * Re-authenticates before deletion since Firebase requires a fresh session
     * for account removal.
     */
    suspend fun deleteAccount(currentPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No user signed in"))
            val email = user.email
                ?: return Result.failure(Exception("User has no email"))

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- EXPORT DATA ----------------
    /**
     * Returns a formatted string with the user's data. In a real app this
     * would be written to a file via MediaStore or SAF.
     */
    fun exportUserData(): String {
        val u = auth.currentUser ?: return "No user signed in"
        return buildString {
            appendLine("PulseFit Data Export")
            appendLine("====================")
            appendLine("Generated: ${java.util.Date()}")
            appendLine()
            appendLine("User ID:   ${u.uid}")
            appendLine("Email:     ${u.email ?: "-"}")
            appendLine("Name:      ${u.displayName ?: "-"}")
            appendLine("Verified:  ${u.isEmailVerified}")
            appendLine("Created:   ${u.metadata?.creationTimestamp?.let { java.util.Date(it) } ?: "-"}")
            appendLine("Last Login:${u.metadata?.lastSignInTimestamp?.let { java.util.Date(it) } ?: "-"}")
            appendLine()
            appendLine("Activities, badges, and squad data are synced to the")
            appendLine("PulseFit backend and can be requested via support.")
        }
    }

    // ---------------- SHARED ----------------
    fun logout() = auth.signOut()

    fun displayName(): String =
        currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: currentUser?.email?.substringBefore("@")
            ?: "User"

    fun email(): String = currentUser?.email ?: ""
    fun userId(): String = currentUser?.uid ?: ""
    fun isEmailVerified(): Boolean = currentUser?.isEmailVerified ?: false
    fun providerId(): String =
        currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
}