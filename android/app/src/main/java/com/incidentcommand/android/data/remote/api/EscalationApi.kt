package com.incidentcommand.android.data.remote.api

import retrofit2.http.POST
import retrofit2.http.Query

interface EscalationApi {

    @POST("escalations/trigger")
    suspend fun trigger(
        @Query("incidentId") incidentId: Long,
        @Query("policyId") policyId: Long
    ): String
}
