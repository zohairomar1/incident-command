package com.incidentcommand.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.incidentcommand.android.data.remote.dto.IncidentResponse

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val id: Long,
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

fun IncidentResponse.toEntity(): IncidentEntity {
    return IncidentEntity(
        id = id,
        title = title,
        description = description,
        severity = severity,
        status = status,
        type = type,
        assignedTeamName = assignedTeamName,
        assignedUsername = assignedUsername,
        createdByUsername = createdByUsername,
        createdAt = createdAt,
        updatedAt = updatedAt,
        resolvedAt = resolvedAt
    )
}

fun IncidentEntity.toResponse(): IncidentResponse {
    return IncidentResponse(
        id = id,
        title = title,
        description = description,
        severity = severity,
        status = status,
        type = type,
        assignedTeamName = assignedTeamName,
        assignedUsername = assignedUsername,
        createdByUsername = createdByUsername,
        createdAt = createdAt,
        updatedAt = updatedAt,
        resolvedAt = resolvedAt
    )
}
