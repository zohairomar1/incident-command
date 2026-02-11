package com.incidentcommand.unit;

import com.incidentcommand.dto.CreateIncidentRequest;
import com.incidentcommand.factory.IncidentFactory;
import com.incidentcommand.model.Incident;
import com.incidentcommand.model.User;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IncidentFactoryTest {

    private IncidentFactory factory;
    private User testUser;

    @BeforeEach
    void setUp() {
        factory = new IncidentFactory();
        testUser = new User("testuser", "test@example.com", "password");
    }

    @Test
    void create_shouldSetBasicFields() {
        CreateIncidentRequest request = new CreateIncidentRequest("Test", "Description",
                Severity.P2, IncidentType.INFRASTRUCTURE, null, null);

        Incident incident = factory.create(request, testUser);

        assertEquals("Test", incident.getTitle());
        assertEquals("Description", incident.getDescription());
        assertEquals(Severity.P2, incident.getSeverity());
        assertEquals(IncidentType.INFRASTRUCTURE, incident.getType());
        assertEquals(IncidentStatus.OPEN, incident.getStatus());
        assertEquals(testUser, incident.getCreatedBy());
    }

    @Test
    void create_securityIncident_shouldDefaultToP1WhenSeverityNull() {
        CreateIncidentRequest request = new CreateIncidentRequest("Security Breach", null,
                null, IncidentType.SECURITY, null, null);

        Incident incident = factory.create(request, testUser);

        assertEquals(Severity.P1, incident.getSeverity());
        assertTrue(incident.getDescription().startsWith("[SECURITY]"));
    }

    @Test
    void create_infrastructureIncident_shouldDefaultToP2WhenSeverityNull() {
        CreateIncidentRequest request = new CreateIncidentRequest("Server Down", "DB unreachable",
                null, IncidentType.INFRASTRUCTURE, null, null);

        Incident incident = factory.create(request, testUser);

        assertEquals(Severity.P2, incident.getSeverity());
    }

    @Test
    void create_serviceDegradation_shouldDefaultToP3WhenSeverityNull() {
        CreateIncidentRequest request = new CreateIncidentRequest("Slow API", "High latency",
                null, IncidentType.SERVICE_DEGRADATION, null, null);

        Incident incident = factory.create(request, testUser);

        assertEquals(Severity.P3, incident.getSeverity());
    }

    @Test
    void create_withExplicitSeverity_shouldNotOverride() {
        CreateIncidentRequest request = new CreateIncidentRequest("Security Issue", "Desc",
                Severity.P4, IncidentType.SECURITY, null, null);

        Incident incident = factory.create(request, testUser);

        assertEquals(Severity.P4, incident.getSeverity());
    }
}
