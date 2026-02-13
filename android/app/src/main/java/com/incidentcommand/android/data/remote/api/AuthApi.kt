package com.incidentcommand.android.data.remote.api

import com.incidentcommand.android.data.remote.dto.AuthResponse
import com.incidentcommand.android.data.remote.dto.LoginRequest
import com.incidentcommand.android.data.remote.dto.RegisterRequest
import com.incidentcommand.android.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse
}
