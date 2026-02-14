package com.incidentcommand.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incidentcommand.android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(username: String, email: String, password: String, role: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(
                username = username.trim(),
                email = email.trim(),
                password = password,
                role = role
            )
            _uiState.update { state ->
                result.fold(
                    onSuccess = { _ ->
                        state.copy(isLoading = false, isRegistered = true)
                    },
                    onFailure = { throwable ->
                        state.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Registration failed"
                        )
                    }
                )
            }
        }
    }

    fun consumeRegistered() {
        _uiState.update { it.copy(isRegistered = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
