package com.incidentcommand.android.data.repository

import com.incidentcommand.android.data.manager.TokenManager
import com.incidentcommand.android.data.remote.api.AuthApi
import com.incidentcommand.android.data.remote.dto.AuthResponse
import com.incidentcommand.android.data.remote.dto.LoginRequest
import com.incidentcommand.android.data.remote.dto.RegisterRequest
import com.incidentcommand.android.data.remote.dto.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = authApi.login(LoginRequest(username = username, password = password))
            tokenManager.saveToken(response.token)
            tokenManager.saveUser(response.username, response.roles)
            response
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        role: String
    ): Result<UserResponse> = withContext(Dispatchers.IO) {
        runCatching {
            authApi.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    role = role
                )
            )
        }
    }

    fun logout() {
        tokenManager.clear()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    fun username(): String? {
        return tokenManager.getUsername()
    }

    fun roles(): Set<String> {
        return tokenManager.getRoles()
    }

    fun hasRole(role: String): Boolean {
        return tokenManager.hasRole(role)
    }
}
