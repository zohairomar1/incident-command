package com.incidentcommand.dto;

import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIncidentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Severity is required")
    private Severity severity;

    @NotNull(message = "Incident type is required")
    private IncidentType type;

    private Long assignedTeamId;
    private Long assignedUserId;
}
