package com.incidentcommand.unit;

import com.incidentcommand.model.*;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import com.incidentcommand.strategy.TimeBasedEscalation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeBasedEscalationTest {

    private TimeBasedEscalation strategy;

    @BeforeEach
    void setUp() {
        strategy = new TimeBasedEscalation();
    }

    @Test
    void shouldEscalate_olderThan30Minutes_shouldReturnTrue() {
        Incident incident = createIncident(LocalDateTime.now().minusMinutes(35));
        assertTrue(strategy.shouldEscalate(incident));
    }

    @Test
    void shouldEscalate_newerThan30Minutes_shouldReturnFalse() {
        Incident incident = createIncident(LocalDateTime.now().minusMinutes(10));
        assertFalse(strategy.shouldEscalate(incident));
    }

    @Test
    void escalate_shouldAssignToTargetTeam() {
        Incident incident = createIncident(LocalDateTime.now().minusMinutes(45));
        Team team = new Team("Escalation Team");
        team.setId(1L);

        EscalationPolicy policy = new EscalationPolicy("TimePolicy", "TIME_BASED");
        EscalationRule rule = new EscalationRule();
        rule.setDelayMinutes(30);
        rule.setTargetTeam(team);
        rule.setRuleOrder(1);
        rule.setPolicy(policy);
        policy.setRules(new ArrayList<>(List.of(rule)));

        strategy.escalate(incident, policy);

        assertEquals(team, incident.getAssignedTeam());
    }

    @Test
    void escalate_shouldAssignToTargetUser() {
        Incident incident = createIncident(LocalDateTime.now().minusMinutes(45));
        User user = new User("oncall", "oncall@test.com", "pass");
        user.setId(1L);

        EscalationPolicy policy = new EscalationPolicy("TimePolicy", "TIME_BASED");
        EscalationRule rule = new EscalationRule();
        rule.setDelayMinutes(30);
        rule.setTargetUser(user);
        rule.setRuleOrder(1);
        rule.setPolicy(policy);
        policy.setRules(new ArrayList<>(List.of(rule)));

        strategy.escalate(incident, policy);

        assertEquals(user, incident.getAssignedUser());
    }

    @Test
    void escalate_shouldNotAssignWhenDelayNotMet() {
        Incident incident = createIncident(LocalDateTime.now().minusMinutes(20));
        Team team = new Team("Escalation Team");
        team.setId(1L);

        EscalationPolicy policy = new EscalationPolicy("TimePolicy", "TIME_BASED");
        EscalationRule rule = new EscalationRule();
        rule.setDelayMinutes(30);
        rule.setTargetTeam(team);
        rule.setRuleOrder(1);
        rule.setPolicy(policy);
        policy.setRules(new ArrayList<>(List.of(rule)));

        strategy.escalate(incident, policy);

        assertNull(incident.getAssignedTeam());
    }

    @Test
    void getStrategyType_shouldReturnTimeBased() {
        assertEquals("TIME_BASED", strategy.getStrategyType());
    }

    private Incident createIncident(LocalDateTime createdAt) {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setSeverity(Severity.P2);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setType(IncidentType.INFRASTRUCTURE);
        incident.setCreatedAt(createdAt);
        return incident;
    }
}
