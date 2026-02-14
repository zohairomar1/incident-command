package com.incidentcommand.android.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.incidentcommand.android.ui.components.LoadingIndicator
import com.incidentcommand.android.ui.theme.severityColor
import com.incidentcommand.android.ui.theme.statusColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToIncidents: () -> Unit,
    onNavigateToTeams: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingIndicator()
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome, ${uiState.username}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total",
                    value = uiState.metrics?.totalIncidents?.toString().orEmpty(),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Open",
                    value = uiState.metrics?.openIncidents?.toString().orEmpty(),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Resolved",
                    value = uiState.metrics?.resolvedIncidents?.toString().orEmpty(),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "MTTR (min)",
                    value = "%.1f".format(uiState.metrics?.meanTimeToResolveMinutes ?: 0.0),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Severity Breakdown", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            BreakdownSection(
                counts = uiState.metrics?.countBySeverity.orEmpty(),
                colorPicker = { severity -> severityColor(severity) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Status Breakdown", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            BreakdownSection(
                counts = uiState.metrics?.countByStatus.orEmpty(),
                colorPicker = { status -> statusColor(status) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onNavigateToIncidents) {
                    Text("View Incidents")
                }
                if (uiState.isAdmin) {
                    TextButton(onClick = onNavigateToTeams) {
                        Text("Manage Teams")
                    }
                }
                TextButton(onClick = { viewModel.loadMetrics() }) {
                    Text("Refresh")
                }
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BreakdownSection(counts: Map<String, Long>, colorPicker: (String) -> Color) {
    val total = counts.values.sum().coerceAtLeast(1)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        counts.toSortedMap().forEach { (label, value) ->
            Column {
                Text("$label: $value")
                LinearProgressIndicator(
                    progress = value.toFloat() / total.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = colorPicker(label)
                )
            }
        }
    }
}
