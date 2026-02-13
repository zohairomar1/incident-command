package com.incidentcommand.android.data.remote.dto

data class CreateIncidentRequest(
    val title: String,
    val description: String?,
    val severity: String,
    val type: String,
    val assignedTeamId: Long?,
    val assignedUserId: Long?
)
