package com.incidentcommand.android.data.remote.dto

data class MetricsResponse(
    val meanTimeToResolveMinutes: Double,
    val totalIncidents: Long,
    val openIncidents: Long,
    val resolvedIncidents: Long,
    val countBySeverity: Map<String, Long>,
    val countByStatus: Map<String, Long>,
    val countByTeam: Map<String, Long>
)
