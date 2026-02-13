package com.incidentcommand.android.data.repository

import com.incidentcommand.android.data.remote.api.EscalationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EscalationRepository @Inject constructor(
    private val escalationApi: EscalationApi
) {

    suspend fun trigger(incidentId: Long, policyId: Long): Result<String> = withContext(Dispatchers.IO) {
        runCatching { escalationApi.trigger(incidentId = incidentId, policyId = policyId) }
    }
}
