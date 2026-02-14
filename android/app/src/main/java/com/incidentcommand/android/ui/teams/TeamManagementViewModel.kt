package com.incidentcommand.android.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incidentcommand.android.data.remote.dto.TeamResponse
import com.incidentcommand.android.data.repository.AuthRepository
import com.incidentcommand.android.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TeamManagementUiState(
    val isLoading: Boolean = true,
    val isCreating: Boolean = false,
    val teamNameInput: String = "",
    val teams: List<TeamResponse> = emptyList(),
    val errorMessage: String? = null,
    val isAdmin: Boolean = false
)

@HiltViewModel
class TeamManagementViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TeamManagementUiState(isAdmin = authRepository.hasRole("ADMIN"))
    )
    val uiState: StateFlow<TeamManagementUiState> = _uiState.asStateFlow()

    init {
        if (_uiState.value.isAdmin) {
            loadTeams()
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onTeamNameChanged(value: String) {
        _uiState.update { it.copy(teamNameInput = value) }
    }

    fun loadTeams() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = teamRepository.getAll()
            _uiState.update {
                result.fold(
                    onSuccess = { teams ->
                        it.copy(
                            isLoading = false,
                            teams = teams,
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to load teams"
                        )
                    }
                )
            }
        }
    }

    fun createTeam() {
        val name = _uiState.value.teamNameInput.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Team name is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, errorMessage = null) }
            val result = teamRepository.create(name = name)
            _uiState.update {
                result.fold(
                    onSuccess = { created ->
                        it.copy(
                            isCreating = false,
                            teamNameInput = "",
                            teams = (it.teams + created).sortedBy { team -> team.name },
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        it.copy(
                            isCreating = false,
                            errorMessage = throwable.message ?: "Failed to create team"
                        )
                    }
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
