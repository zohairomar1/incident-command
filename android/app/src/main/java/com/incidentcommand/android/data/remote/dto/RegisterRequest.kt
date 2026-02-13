package com.incidentcommand.android.data.remote.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val role: String
)
