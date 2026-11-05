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

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE userId = :userId AND archived = 0 ORDER BY createdAt ASC")
    fun observeHabits(userId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_checks WHERE userId = :userId AND dayIndex BETWEEN :startDay AND :endDay")
    fun observeChecksInRange(userId: String, startDay: Long, endDay: Long): Flow<List<HabitCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: HabitCheckEntity)

    @Query("DELETE FROM habit_checks WHERE habitId = :habitId AND dayIndex = :dayIndex")
    suspend fun deleteCheck(habitId: String, dayIndex: Long)
}
