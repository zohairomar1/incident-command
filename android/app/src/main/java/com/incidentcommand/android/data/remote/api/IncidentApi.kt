package com.incidentcommand.android.data.remote.api

import com.incidentcommand.android.data.remote.dto.CreateIncidentRequest
import com.incidentcommand.android.data.remote.dto.IncidentResponse
import com.incidentcommand.android.data.remote.dto.UpdateIncidentRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IncidentApi {

    @GET("incidents")
    suspend fun getAll(): List<IncidentResponse>

    @GET("incidents/{id}")
    suspend fun getById(@Path("id") id: Long): IncidentResponse

    @POST("incidents")
    suspend fun create(@Body request: CreateIncidentRequest): IncidentResponse

    @PUT("incidents/{id}")
    suspend fun update(@Path("id") id: Long, @Body request: UpdateIncidentRequest): IncidentResponse

    @PATCH("incidents/{id}/status")
    suspend fun patchStatus(@Path("id") id: Long, @Query("status") status: String): IncidentResponse

    @DELETE("incidents/{id}")
    suspend fun delete(@Path("id") id: Long)
}
