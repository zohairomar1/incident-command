package com.incidentcommand.android.ui.incidents.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incidentcommand.android.data.remote.dto.IncidentResponse
import com.incidentcommand.android.data.repository.AuthRepository
import com.incidentcommand.android.data.repository.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncidentDetailUiState(
    val isLoading: Boolean = true,
    val incident: IncidentResponse? = null,
    val availableTransitions: List<String> = emptyList(),
    val errorMessage: String? = null,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false,
    val isDeleted: Boolean = false
)

@HiltViewModel
class IncidentDetailViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        IncidentDetailUiState(
            canEdit = authRepository.hasRole("ADMIN") || authRepository.hasRole("RESPONDER"),
            canDelete = authRepository.hasRole("ADMIN")
        )
    )
    val uiState: StateFlow<IncidentDetailUiState> = _uiState.asStateFlow()

    fun loadIncident(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = incidentRepository.getById(id)
            _uiState.update {
                result.fold(
                    onSuccess = { incident ->
                        it.copy(
                            isLoading = false,
                            incident = incident,
                            availableTransitions = transitionsFor(incident.status),
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to load incident"
                        )
                    }
                )
            }
        }
    }

    fun updateStatus(status: String) {
        val id = _uiState.value.incident?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = incidentRepository.patchStatus(id, status)
            _uiState.update {
                result.fold(
                    onSuccess = { updated ->
                        it.copy(
                            isLoading = false,
                            incident = updated,
                            availableTransitions = transitionsFor(updated.status),
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to update status"
                        )
                    }
                )
            }
        }
    }

    fun deleteIncident() {
        val id = _uiState.value.incident?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = incidentRepository.delete(id)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { _ ->
                        state.copy(
                            isLoading = false,
                            isDeleted = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        state.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to delete incident"
                        )
                    }
                )
            }
        }
    }

    private fun transitionsFor(status: String): List<String> {
        return when (status) {
            "OPEN" -> listOf("ACKNOWLEDGED", "RESOLVED", "CLOSED")
            "ACKNOWLEDGED" -> listOf("RESOLVED", "CLOSED")
            "RESOLVED" -> listOf("CLOSED", "OPEN")
            else -> emptyList()
        }
    }
}
