package com.incidentcommand.android.ui.incidents.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incidentcommand.android.data.remote.dto.CreateIncidentRequest
import com.incidentcommand.android.data.remote.dto.TeamResponse
import com.incidentcommand.android.data.remote.dto.UpdateIncidentRequest
import com.incidentcommand.android.data.repository.AuthRepository
import com.incidentcommand.android.data.repository.IncidentRepository
import com.incidentcommand.android.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncidentFormUiState(
    val isInitializing: Boolean = true,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val incidentId: Long? = null,
    val title: String = "",
    val description: String = "",
    val severity: String = "P2",
    val type: String = "SECURITY",
    val selectedTeamId: Long? = null,
    val teams: List<TeamResponse> = emptyList(),
    val canSubmit: Boolean = false,
    val errorMessage: String? = null,
    val submitSuccess: Boolean = false
)

@HiltViewModel
class IncidentFormViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val teamRepository: TeamRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val editable = authRepository.hasRole("ADMIN") || authRepository.hasRole("RESPONDER")

    private val _uiState = MutableStateFlow(IncidentFormUiState(canSubmit = editable))
    val uiState: StateFlow<IncidentFormUiState> = _uiState.asStateFlow()

    private var initializedKey: Long? = null

    fun initialize(incidentId: Long?) {
        if (initializedKey == incidentId && !_uiState.value.isInitializing) {
            return
        }
        initializedKey = incidentId

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isInitializing = true,
                    isEditMode = incidentId != null,
                    incidentId = incidentId,
                    submitSuccess = false,
                    errorMessage = null
                )
            }

            val teamsDeferred = async { teamRepository.getAll() }
            val incidentDeferred = if (incidentId != null) {
                async { incidentRepository.getById(incidentId) }
            } else {
                null
            }

            val teamsResult = teamsDeferred.await()
            val teams = teamsResult.getOrDefault(emptyList())

            if (incidentDeferred == null) {
                _uiState.update {
                    it.copy(
                        isInitializing = false,
                        teams = teams,
                        errorMessage = teamsResult.exceptionOrNull()?.message
                    )
                }
                return@launch
            }

            val incidentResult = incidentDeferred.await()
            _uiState.update {
                incidentResult.fold(
                    onSuccess = { incident ->
                        it.copy(
                            isInitializing = false,
                            teams = teams,
                            title = incident.title,
                            description = incident.description.orEmpty(),
                            severity = incident.severity,
                            type = incident.type,
                            selectedTeamId = teams.firstOrNull { team ->
                                team.name == incident.assignedTeamName
                            }?.id,
                            errorMessage = teamsResult.exceptionOrNull()?.message
                        )
                    },
                    onFailure = { throwable ->
                        it.copy(
                            isInitializing = false,
                            teams = teams,
                            errorMessage = throwable.message ?: "Failed to load incident"
                        )
                    }
                )
            }
        }
    }

    fun onTitleChanged(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onSeverityChanged(value: String) {
        _uiState.update { it.copy(severity = value) }
    }

    fun onTypeChanged(value: String) {
        _uiState.update { it.copy(type = value) }
    }

    fun onTeamChanged(value: Long?) {
        _uiState.update { it.copy(selectedTeamId = value) }
    }

    fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) {
            _uiState.update { it.copy(errorMessage = "You do not have permission to modify incidents") }
            return
        }
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Title is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val result = if (state.isEditMode && state.incidentId != null) {
                incidentRepository.update(
                    id = state.incidentId,
                    request = UpdateIncidentRequest(
                        title = state.title.trim(),
                        description = state.description.ifBlank { null },
                        severity = state.severity,
                        status = null,
                        type = state.type,
                        assignedTeamId = state.selectedTeamId,
                        assignedUserId = null
                    )
                )
            } else {
                incidentRepository.create(
                    request = CreateIncidentRequest(
                        title = state.title.trim(),
                        description = state.description.ifBlank { null },
                        severity = state.severity,
                        type = state.type,
                        assignedTeamId = state.selectedTeamId,
                        assignedUserId = null
                    )
                )
            }

            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { _ ->
                        currentState.copy(
                            isSaving = false,
                            submitSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        currentState.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Failed to save incident"
                        )
                    }
                )
            }
        }
    }

    fun consumeSubmitSuccess() {
        _uiState.update { it.copy(submitSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
