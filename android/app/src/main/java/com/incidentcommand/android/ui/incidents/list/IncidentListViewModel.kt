package com.incidentcommand.android.ui.incidents.list

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

data class IncidentListUiState(
    val isLoading: Boolean = true,
    val incidents: List<IncidentResponse> = emptyList(),
    val statusFilter: String? = null,
    val severityFilter: String? = null,
    val errorMessage: String? = null,
    val canCreate: Boolean = false
)

@HiltViewModel
class IncidentListViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        IncidentListUiState(
            canCreate = authRepository.hasRole("ADMIN") || authRepository.hasRole("RESPONDER")
        )
    )
    val uiState: StateFlow<IncidentListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = incidentRepository.getAll()
            _uiState.update {
                result.fold(
                    onSuccess = { incidents ->
                        it.copy(
                            isLoading = false,
                            incidents = incidents,
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to load incidents"
                        )
                    }
                )
            }
        }
    }

    fun setStatusFilter(filter: String?) {
        _uiState.update { it.copy(statusFilter = filter) }
    }

    fun setSeverityFilter(filter: String?) {
        _uiState.update { it.copy(severityFilter = filter) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
