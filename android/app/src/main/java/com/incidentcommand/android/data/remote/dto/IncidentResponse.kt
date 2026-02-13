package com.incidentcommand.android.data.remote.dto

data class IncidentResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val severity: String,
    val status: String,
    val type: String,
    val assignedTeamName: String?,
    val assignedUsername: String?,
    val createdByUsername: String,
    val createdAt: String,
    val updatedAt: String,
    val resolvedAt: String?
)
