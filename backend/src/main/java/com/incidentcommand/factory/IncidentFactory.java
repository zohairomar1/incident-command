package com.incidentcommand.factory;

import com.incidentcommand.dto.CreateIncidentRequest;
import com.incidentcommand.model.Incident;
import com.incidentcommand.model.User;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import org.springframework.stereotype.Component;

@Component
public class IncidentFactory {

    public Incident create(CreateIncidentRequest request, User createdBy) {
        Incident incident = new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setType(request.getType());
        incident.setStatus(IncidentStatus.OPEN);
        incident.setCreatedBy(createdBy);

        applyTypeDefaults(incident);

        return incident;
    }

    private void applyTypeDefaults(Incident incident) {
        switch (incident.getType()) {
            case SECURITY -> {
                if (incident.getSeverity() == null) {
                    incident.setSeverity(Severity.P1);
                }
                if (incident.getDescription() == null) {
                    incident.setDescription("[SECURITY] " + incident.getTitle());
                }
            }
            case INFRASTRUCTURE -> {
                if (incident.getSeverity() == null) {
                    incident.setSeverity(Severity.P2);
                }
            }
            case SERVICE_DEGRADATION -> {
                if (incident.getSeverity() == null) {
                    incident.setSeverity(Severity.P3);
                }
            }
        }
    }
}
