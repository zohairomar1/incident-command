package com.incidentcommand.android.data.repository

import com.incidentcommand.android.data.remote.api.TeamApi
import com.incidentcommand.android.data.remote.dto.CreateTeamRequest
import com.incidentcommand.android.data.remote.dto.TeamResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor(
    private val teamApi: TeamApi
) {

    suspend fun getAll(): Result<List<TeamResponse>> = withContext(Dispatchers.IO) {
        runCatching { teamApi.getAll() }
    }

    suspend fun getById(id: Long): Result<TeamResponse> = withContext(Dispatchers.IO) {
        runCatching { teamApi.getById(id) }
    }

    suspend fun create(name: String, memberIds: Set<Long> = emptySet()): Result<TeamResponse> = withContext(Dispatchers.IO) {
        runCatching {
            teamApi.create(
                CreateTeamRequest(
                    name = name,
                    memberIds = memberIds
                )
            )
        }
    }
}
