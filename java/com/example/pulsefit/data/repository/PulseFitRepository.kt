package com.example.pulsefit.data.repository

import com.example.pulsefit.data.local.AppDatabase
import com.example.pulsefit.data.local.ActivityEntity
import com.example.pulsefit.data.local.SettingsEntity
import com.example.pulsefit.data.remote.ApiService
import com.example.pulsefit.data.remote.ActivityDto
import com.example.pulsefit.data.remote.SettingsDto
import com.example.pulsefit.data.remote.SyncRequest
import com.example.pulsefit.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PulseFitRepository(
    private val db: AppDatabase,
    private val api: ApiService
) {
    // ---------- Activities ----------
    fun observeActivities(userId: String): Flow<List<Activity>> =
        db.activityDao().getActivities(userId).map { list ->
            list.map {
                Activity(
                    id = it.localId, userId = it.userId, type = it.type,
                    durationSeconds = it.durationSeconds, distanceKm = it.distanceKm,
                    calories = it.calories, notes = it.notes,
                    timestamp = it.timestamp, syncStatus = it.syncStatus
                )
            }
        }

    /** Save locally (offline-first), then attempt sync if network available. */
    suspend fun saveActivity(userId: String, activity: Activity) {
        val entity = ActivityEntity(
            localId = UUID.randomUUID().toString(),
            userId = userId,
            type = activity.type,
            durationSeconds = activity.durationSeconds,
            distanceKm = activity.distanceKm,
            calories = activity.calories,
            notes = activity.notes,
            timestamp = activity.timestamp,
            syncStatus = "pending"
        )
        db.activityDao().insert(entity)
    }

    /** Background sync - called when connectivity restored. */
    suspend fun syncPending(token: String, deviceId: String) {
        val pending = db.activityDao().getPendingActivities()
        if (pending.isEmpty()) return

        val dtos = pending.map {
            ActivityDto(
                id = it.localId, type = it.type,
                durationSeconds = it.durationSeconds,
                distanceKm = it.distanceKm,
                calories = it.calories, timestamp = it.timestamp
            )
        }
        val request = SyncRequest(
            deviceId = deviceId,
            lastSync = "1970-01-01T00:00:00Z",
            activities = dtos,
            deletedIds = emptyList()
        )
        val response = api.syncActivities(token, request)
        if (response.isSuccessful) {
            pending.forEach {
                db.activityDao().update(it.copy(syncStatus = "synced"))
            }
        }
    }

    // ---------- Settings ----------
    fun observeSettings(userId: String): Flow<SettingsEntity?> =
        db.settingsDao().getSettings(userId)

    suspend fun saveSettings(userId: String, settings: SettingsEntity) {
        db.settingsDao().insert(settings)
    }

    suspend fun pushSettingsToServer(token: String, settings: SettingsEntity) {
        api.updateSettings(
            token,
            SettingsDto(
                settings.units, settings.theme,
                settings.language, settings.notificationsEnabled
            )
        )
    }
}