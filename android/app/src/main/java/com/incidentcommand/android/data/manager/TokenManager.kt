package com.incidentcommand.android.data.manager

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext

class TokenManager(
    @ApplicationContext context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveUser(username: String, roles: Set<String>) {
        sharedPreferences.edit()
            .putString(KEY_USERNAME, username)
            .putStringSet(KEY_ROLES, roles)
            .apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun getRoles(): Set<String> {
        return sharedPreferences.getStringSet(KEY_ROLES, emptySet()) ?: emptySet()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }

    fun hasRole(role: String): Boolean {
        return getRoles().contains(role)
    }

    companion object {
        private const val PREFS_NAME = "incident_command_secure_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLES = "roles"
    }
}
