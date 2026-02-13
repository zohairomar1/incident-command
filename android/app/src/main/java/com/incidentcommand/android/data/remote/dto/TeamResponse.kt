package com.incidentcommand.android.data.remote.dto

data class TeamResponse(
    val id: Long,
    val name: String,
    val memberUsernames: Set<String>,
    val createdAt: String
)
