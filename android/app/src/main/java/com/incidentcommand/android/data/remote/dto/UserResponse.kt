package com.incidentcommand.android.data.remote.dto

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val roles: Set<String>,
    val createdAt: String
)
