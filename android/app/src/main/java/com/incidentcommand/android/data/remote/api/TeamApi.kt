package com.incidentcommand.android.data.remote.api

import com.incidentcommand.android.data.remote.dto.CreateTeamRequest
import com.incidentcommand.android.data.remote.dto.TeamResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TeamApi {

    @GET("teams")
    suspend fun getAll(): List<TeamResponse>

    @GET("teams/{id}")
    suspend fun getById(@Path("id") id: Long): TeamResponse

    @POST("teams")
    suspend fun create(@Body request: CreateTeamRequest): TeamResponse
}
