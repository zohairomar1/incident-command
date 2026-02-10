package com.incidentcommand.dto;

import com.incidentcommand.model.Incident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    @Mapping(target = "assignedTeamName", source = "assignedTeam.name")
    @Mapping(target = "assignedUsername", source = "assignedUser.username")
    @Mapping(target = "createdByUsername", source = "createdBy.username")
    IncidentResponse toResponse(Incident incident);
}
