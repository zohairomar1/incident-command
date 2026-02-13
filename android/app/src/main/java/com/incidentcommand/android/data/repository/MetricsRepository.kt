package com.incidentcommand.android.data.repository

import com.incidentcommand.android.data.remote.api.MetricsApi
import com.incidentcommand.android.data.remote.dto.MetricsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsRepository @Inject constructor(
    private val metricsApi: MetricsApi
) {

    suspend fun getMetrics(): Result<MetricsResponse> = withContext(Dispatchers.IO) {
        runCatching { metricsApi.getMetrics() }
    }
}
