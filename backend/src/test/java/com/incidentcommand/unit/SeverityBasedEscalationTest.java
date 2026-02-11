package com.incidentcommand.unit;

import com.incidentcommand.model.*;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import com.incidentcommand.strategy.SeverityBasedEscalation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeverityBasedEscalationTest {

    private SeverityBasedEscalation strategy;

    @BeforeEach
    void setUp() {
        strategy = new SeverityBasedEscalation();
    }

    @Test
    void shouldEscalate_p1OlderThan15Minutes_shouldReturnTrue() {
        Incident incident = createIncident(Severity.P1, LocalDateTime.now().minusMinutes(20));
        assertTrue(strategy.shouldEscalate(incident));
    }

    @Test
    void shouldEscalate_p1NewerThan15Minutes_shouldReturnFalse() {
        Incident incident = createIncident(Severity.P1, LocalDateTime.now().minusMinutes(5));
        assertFalse(strategy.shouldEscalate(incident));
    }

    @Test
    void shouldEscalate_p4OlderThan240Minutes_shouldReturnTrue() {
        Incident incident = createIncident(Severity.P4, LocalDateTime.now().minusMinutes(250));
        assertTrue(strategy.shouldEscalate(incident));
    }

    @Test
    void shouldEscalate_p4NewerThan240Minutes_shouldReturnFalse() {
        Incident incident = createIncident(Severity.P4, LocalDateTime.now().minusMinutes(100));
        assertFalse(strategy.shouldEscalate(incident));
    }

    @Test
    void getStrategyType_shouldReturnSeverityBased() {
        assertEquals("SEVERITY_BASED", strategy.getStrategyType());
    }

    @Test
    void escalate_shouldAssignToTargetTeam() {
        Incident incident = createIncident(Severity.P1, LocalDateTime.now().minusMinutes(20));
        Team team = new Team("Escalation Team");
        team.setId(1L);

        EscalationPolicy policy = new EscalationPolicy("SevPolicy", "SEVERITY_BASED");
        EscalationRule rule = new EscalationRule();
        rule.setTargetTeam(team);
        rule.setRuleOrder(1);
        rule.setPolicy(policy);
        policy.setRules(new ArrayList<>(List.of(rule)));

        strategy.escalate(incident, policy);

        assertEquals(team, incident.getAssignedTeam());
    }

    @Test
    void escalate_shouldAssignToTargetUser() {
        Incident incident = createIncident(Severity.P1, LocalDateTime.now().minusMinutes(20));
        User user = new User("oncall", "oncall@test.com", "pass");
        user.setId(1L);

        EscalationPolicy policy = new EscalationPolicy("SevPolicy", "SEVERITY_BASED");
        EscalationRule rule = new EscalationRule();
        rule.setTargetUser(user);
        rule.setRuleOrder(1);
        rule.setPolicy(policy);
        policy.setRules(new ArrayList<>(List.of(rule)));

        strategy.escalate(incident, policy);

        assertEquals(user, incident.getAssignedUser());
    }

    @Test
    void escalate_withEmptyRules_shouldNotAssign() {
        Incident incident = createIncident(Severity.P1, LocalDateTime.now().minusMinutes(20));

        EscalationPolicy policy = new EscalationPolicy("EmptyPolicy", "SEVERITY_BASED");
        policy.setRules(new ArrayList<>());

        strategy.escalate(incident, policy);

        assertNull(incident.getAssignedTeam());
        assertNull(incident.getAssignedUser());
    }

    private Incident createIncident(Severity severity, LocalDateTime createdAt) {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setSeverity(severity);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setType(IncidentType.INFRASTRUCTURE);
        incident.setCreatedAt(createdAt);
        return incident;
    }
}
