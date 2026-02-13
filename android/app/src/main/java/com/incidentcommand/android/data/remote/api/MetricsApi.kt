package com.incidentcommand.android.data.remote.api

import com.incidentcommand.android.data.remote.dto.MetricsResponse
import retrofit2.http.GET

interface MetricsApi {

    @GET("metrics")
    suspend fun getMetrics(): MetricsResponse
}
