package com.incidentcommand.android.data.remote.dto

data class AuthResponse(
    val token: String,
    val username: String,
    val roles: Set<String>
)
