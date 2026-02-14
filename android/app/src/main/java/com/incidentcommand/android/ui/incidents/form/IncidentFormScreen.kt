package com.incidentcommand.android.ui.incidents.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.incidentcommand.android.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentFormScreen(
    incidentId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: IncidentFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var severityExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var teamExpanded by remember { mutableStateOf(false) }

    val severityOptions = listOf("P1", "P2", "P3", "P4")
    val typeOptions = listOf("SECURITY", "INFRASTRUCTURE", "SERVICE_DEGRADATION")

    LaunchedEffect(incidentId) {
        viewModel.initialize(incidentId)
    }

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            viewModel.consumeSubmitSuccess()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Incident" else "Create Incident") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isInitializing) {
            LoadingIndicator()
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(
                expanded = severityExpanded,
                onExpandedChange = { severityExpanded = !severityExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.severity,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Severity") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = severityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = severityExpanded,
                    onDismissRequest = { severityExpanded = false }
                ) {
                    severityOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.onSeverityChanged(option)
                                severityExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.type,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    typeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.onTypeChanged(option)
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ExposedDropdownMenuBox(
                expanded = teamExpanded,
                onExpandedChange = { teamExpanded = !teamExpanded }
            ) {
                val selectedTeamName = uiState.teams
                    .firstOrNull { it.id == uiState.selectedTeamId }
                    ?.name
                    ?: "Unassigned"

                OutlinedTextField(
                    value = selectedTeamName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Team") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = teamExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                DropdownMenu(
                    expanded = teamExpanded,
                    onDismissRequest = { teamExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Unassigned") },
                        onClick = {
                            viewModel.onTeamChanged(null)
                            teamExpanded = false
                        }
                    )
                    uiState.teams.forEach { team ->
                        DropdownMenuItem(
                            text = { Text(team.name) },
                            onClick = {
                                viewModel.onTeamChanged(team.id)
                                teamExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.submit() },
                enabled = !uiState.isSaving && uiState.canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text(if (uiState.isEditMode) "Save Changes" else "Create Incident")
                }
            }

            if (!uiState.canSubmit) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You can view incidents but cannot create or edit them.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
