package com.incidentcommand.android.data.remote.dto

data class UpdateIncidentRequest(
    val title: String? = null,
    val description: String? = null,
    val severity: String? = null,
    val status: String? = null,
    val type: String? = null,
    val assignedTeamId: Long? = null,
    val assignedUserId: Long? = null
)
