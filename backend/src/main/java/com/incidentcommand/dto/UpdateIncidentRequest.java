package com.incidentcommand.dto;

import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIncidentRequest {
    private String title;
    private String description;
    private Severity severity;
    private IncidentStatus status;
    private IncidentType type;
    private Long assignedTeamId;
    private Long assignedUserId;
}
