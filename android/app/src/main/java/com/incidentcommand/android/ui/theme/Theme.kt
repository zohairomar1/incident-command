package com.incidentcommand.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = BluePrimaryContainer,
    surfaceVariant = SurfaceTint,
    error = ErrorRed
)

@Composable
fun IncidentCommandTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
