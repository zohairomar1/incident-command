package com.incidentcommand.android.ui.incidents.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.incidentcommand.android.data.remote.dto.IncidentResponse
import com.incidentcommand.android.ui.components.LoadingIndicator
import com.incidentcommand.android.ui.components.SeverityBadge
import com.incidentcommand.android.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentListScreen(
    onBack: () -> Unit,
    onOpenIncident: (Long) -> Unit,
    onCreateIncident: () -> Unit,
    viewModel: IncidentListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val filtered = uiState.incidents.filter { incident ->
        (uiState.statusFilter == null || incident.status == uiState.statusFilter) &&
            (uiState.severityFilter == null || incident.severity == uiState.severityFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidents") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.canCreate) {
                FloatingActionButton(onClick = onCreateIncident) {
                    Icon(Icons.Default.Add, contentDescription = "Create incident")
                }
            }
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
                .padding(12.dp)
        ) {
            FilterRow(
                statusFilter = uiState.statusFilter,
                severityFilter = uiState.severityFilter,
                onStatusFilterChanged = viewModel::setStatusFilter,
                onSeverityFilterChanged = viewModel::setSeverityFilter
            )
            Spacer(modifier = Modifier.height(8.dp))

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered, key = { it.id }) { incident ->
                    IncidentRow(
                        incident = incident,
                        onOpen = { onOpenIncident(incident.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    statusFilter: String?,
    severityFilter: String?,
    onStatusFilterChanged: (String?) -> Unit,
    onSeverityFilterChanged: (String?) -> Unit
) {
    val statuses = listOf("OPEN", "ACKNOWLEDGED", "RESOLVED", "CLOSED")
    val severities = listOf("P1", "P2", "P3", "P4")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == null,
                onClick = { onStatusFilterChanged(null) },
                label = { Text("All Status") }
            )
            statuses.forEach { status ->
                FilterChip(
                    selected = statusFilter == status,
                    onClick = {
                        onStatusFilterChanged(if (statusFilter == status) null else status)
                    },
                    label = { Text(status) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = severityFilter == null,
                onClick = { onSeverityFilterChanged(null) },
                label = { Text("All Severity") }
            )
            severities.forEach { severity ->
                FilterChip(
                    selected = severityFilter == severity,
                    onClick = {
                        onSeverityFilterChanged(if (severityFilter == severity) null else severity)
                    },
                    label = { Text(severity) }
                )
            }
        }
    }
}

@Composable
private fun IncidentRow(incident: IncidentResponse, onOpen: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = incident.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SeverityBadge(severity = incident.severity)
                StatusBadge(status = incident.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Team: ${incident.assignedTeamName ?: "Unassigned"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Updated: ${incident.updatedAt}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
