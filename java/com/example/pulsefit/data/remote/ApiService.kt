package com.example.pulsefit.data.remote

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("api/users/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserDto>

    @PUT("api/users/me/settings")
    suspend fun updateSettings(
        @Header("Authorization") token: String,
        @Body body: SettingsDto
    ): Response<SettingsDto>

    @GET("api/activities")
    suspend fun getActivities(
        @Header("Authorization") token: String,
        @Query("since") since: String
    ): Response<List<ActivityDto>>

    @POST("api/activities")
    suspend fun logActivity(
        @Header("Authorization") token: String,
        @Body body: ActivityDto
    ): Response<ActivityResponse>

    @POST("api/activities/sync")
    suspend fun syncActivities(
        @Header("Authorization") token: String,
        @Body body: SyncRequest
    ): Response<SyncResponse>

    @GET("api/ai/insights")
    suspend fun getAiInsights(
        @Header("Authorization") token: String
    ): Response<AiInsightsDto>
}