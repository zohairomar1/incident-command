package com.incidentcommand.android.data.repository

import com.incidentcommand.android.data.local.dao.IncidentDao
import com.incidentcommand.android.data.local.entity.toEntity
import com.incidentcommand.android.data.local.entity.toResponse
import com.incidentcommand.android.data.remote.api.IncidentApi
import com.incidentcommand.android.data.remote.dto.CreateIncidentRequest
import com.incidentcommand.android.data.remote.dto.IncidentResponse
import com.incidentcommand.android.data.remote.dto.UpdateIncidentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepository @Inject constructor(
    private val incidentApi: IncidentApi,
    private val incidentDao: IncidentDao
) {

    suspend fun getAll(): Result<List<IncidentResponse>> = withContext(Dispatchers.IO) {
        runCatching {
            val incidents = incidentApi.getAll()
            incidentDao.deleteAll()
            incidentDao.insertAll(incidents.map { it.toEntity() })
            incidents
        }.recoverCatching {
            val cached = incidentDao.getAll().map { it.toResponse() }
            if (cached.isNotEmpty()) cached else throw it
        }
    }

    suspend fun getById(id: Long): Result<IncidentResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val incident = incidentApi.getById(id)
            incident
        }.recoverCatching {
            incidentDao.getById(id)?.toResponse() ?: throw it
        }
    }

    suspend fun create(request: CreateIncidentRequest): Result<IncidentResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val created = incidentApi.create(request)
            refreshCacheFromApi()
            created
        }
    }

    suspend fun update(id: Long, request: UpdateIncidentRequest): Result<IncidentResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val updated = incidentApi.update(id, request)
            refreshCacheFromApi()
            updated
        }
    }

    suspend fun patchStatus(id: Long, status: String): Result<IncidentResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val updated = incidentApi.patchStatus(id, status)
            refreshCacheFromApi()
            updated
        }
    }

    suspend fun delete(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            incidentApi.delete(id)
            refreshCacheFromApi()
            Unit
        }
    }

    private suspend fun refreshCacheFromApi() {
        val incidents = incidentApi.getAll()
        incidentDao.deleteAll()
        incidentDao.insertAll(incidents.map { it.toEntity() })
    }
}
