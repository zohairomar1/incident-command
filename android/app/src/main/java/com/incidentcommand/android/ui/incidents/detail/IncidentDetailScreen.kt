package com.incidentcommand.android.ui.incidents.detail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.incidentcommand.android.ui.components.LoadingIndicator
import com.incidentcommand.android.ui.components.SeverityBadge
import com.incidentcommand.android.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    incidentId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: IncidentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(incidentId) {
        viewModel.loadIncident(incidentId)
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incident Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingIndicator()
            return@Scaffold
        }

        val incident = uiState.incident
        if (incident == null) {
            Text(
                text = uiState.errorMessage ?: "Incident not found",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = incident.title, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SeverityBadge(severity = incident.severity)
                        StatusBadge(status = incident.status)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = incident.description ?: "No description")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Type: ${incident.type}")
                    Text(text = "Team: ${incident.assignedTeamName ?: "Unassigned"}")
                    Text(text = "Assignee: ${incident.assignedUsername ?: "Unassigned"}")
                    Text(text = "Created by: ${incident.createdByUsername}")
                    Text(text = "Created at: ${incident.createdAt}")
                    Text(text = "Updated at: ${incident.updatedAt}")
                    if (incident.resolvedAt != null) {
                        Text(text = "Resolved at: ${incident.resolvedAt}")
                    }
                }
            }

            if (uiState.canEdit && uiState.availableTransitions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Status transitions", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.availableTransitions.forEach { status ->
                        OutlinedButton(onClick = { viewModel.updateStatus(status) }) {
                            Text(status)
                        }
                    }
                }
            }

            if (uiState.canEdit) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onEdit(incident.id) }) {
                        Text("Edit")
                    }
                    if (uiState.canDelete) {
                        OutlinedButton(onClick = { viewModel.deleteIncident() }) {
                            Text("Delete")
                        }
                    }
                }
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
