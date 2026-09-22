package com.example.pulsefit.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity)

    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY timestamp DESC")
    fun getActivities(userId: String): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE syncStatus = 'pending'")
    suspend fun getPendingActivities(): List<ActivityEntity>

    @Update
    suspend fun update(activity: ActivityEntity)

    @Delete
    suspend fun delete(activity: ActivityEntity)
}

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Query("SELECT * FROM profile WHERE userId = :userId LIMIT 1")
    fun getProfile(userId: String): Flow<ProfileEntity?>
}

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: SettingsEntity)

    @Query("SELECT * FROM settings WHERE userId = :userId LIMIT 1")
    fun getSettings(userId: String): Flow<SettingsEntity?>
}

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(badges: List<BadgeEntity>)

    @Query("SELECT * FROM badges")
    fun getBadges(): Flow<List<BadgeEntity>>
}