package com.incidentcommand.android.data.remote.dto

data class CreateTeamRequest(
    val name: String,
    val memberIds: Set<Long>
)
