package com.incidentcommand.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.incidentcommand.android.ui.theme.severityColor

@Composable
fun SeverityBadge(severity: String) {
    val color = severityColor(severity)
    SuggestionChip(
        onClick = { },
        label = {
            Text(
                text = severity,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.12f),
            labelColor = color
        ),
        border = BorderStroke(1.dp, color)
    )
}
