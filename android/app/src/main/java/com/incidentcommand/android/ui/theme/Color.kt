package com.incidentcommand.android.ui.theme

import androidx.compose.ui.graphics.Color

val BluePrimary = Color(0xFF1D4ED8)
val BluePrimaryContainer = Color(0xFFDCE6FF)
val SurfaceTint = Color(0xFFF7F9FC)
val ErrorRed = Color(0xFFB91C1C)
val SuccessGreen = Color(0xFF15803D)
val WarningYellow = Color(0xFFCA8A04)
val NeutralGray = Color(0xFF6B7280)
val SeverityOrange = Color(0xFFEA580C)

fun severityColor(severity: String): Color {
    return when (severity) {
        "P1" -> ErrorRed
        "P2" -> SeverityOrange
        "P3" -> WarningYellow
        "P4" -> BluePrimary
        else -> NeutralGray
    }
}

fun statusColor(status: String): Color {
    return when (status) {
        "OPEN" -> ErrorRed
        "ACKNOWLEDGED" -> WarningYellow
        "RESOLVED" -> SuccessGreen
        "CLOSED" -> NeutralGray
        else -> NeutralGray
    }
}
