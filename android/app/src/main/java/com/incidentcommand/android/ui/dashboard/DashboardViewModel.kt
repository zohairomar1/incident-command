package com.incidentcommand.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incidentcommand.android.data.remote.dto.MetricsResponse
import com.incidentcommand.android.data.repository.AuthRepository
import com.incidentcommand.android.data.repository.MetricsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val metrics: MetricsResponse? = null,
    val errorMessage: String? = null,
    val username: String = "",
    val isAdmin: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val metricsRepository: MetricsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            username = authRepository.username().orEmpty(),
            isAdmin = authRepository.hasRole("ADMIN")
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadMetrics()
    }

    fun loadMetrics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = metricsRepository.getMetrics()
            _uiState.update {
                result.fold(
                    onSuccess = { metrics ->
                        it.copy(
                            isLoading = false,
                            metrics = metrics,
                            errorMessage = null
                        )
                    },
                    onFailure = { throwable ->
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to load metrics"
                        )
                    }
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
