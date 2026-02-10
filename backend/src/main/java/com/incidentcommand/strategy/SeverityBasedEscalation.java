package com.incidentcommand.strategy;

import com.incidentcommand.model.EscalationPolicy;
import com.incidentcommand.model.EscalationRule;
import com.incidentcommand.model.Incident;
import com.incidentcommand.model.enums.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
public class SeverityBasedEscalation implements EscalationStrategy {

    private static final Logger log = LoggerFactory.getLogger(SeverityBasedEscalation.class);

    private static final Map<Severity, Integer> ESCALATION_THRESHOLDS_MINUTES = Map.of(
            Severity.P1, 15,
            Severity.P2, 30,
            Severity.P3, 60,
            Severity.P4, 240
    );

    @Override
    public boolean shouldEscalate(Incident incident) {
        int thresholdMinutes = ESCALATION_THRESHOLDS_MINUTES.getOrDefault(incident.getSeverity(), 60);
        long minutesSinceCreation = ChronoUnit.MINUTES.between(incident.getCreatedAt(), LocalDateTime.now());
        return minutesSinceCreation >= thresholdMinutes;
    }

    @Override
    public void escalate(Incident incident, EscalationPolicy policy) {
        log.info("Severity-based escalation triggered for incident {} (severity: {})", incident.getId(), incident.getSeverity());

        for (EscalationRule rule : policy.getRules()) {
            if (rule.getTargetTeam() != null) {
                incident.setAssignedTeam(rule.getTargetTeam());
                log.info("Escalated incident {} to team: {}", incident.getId(), rule.getTargetTeam().getName());
                break;
            }
            if (rule.getTargetUser() != null) {
                incident.setAssignedUser(rule.getTargetUser());
                log.info("Escalated incident {} to user: {}", incident.getId(), rule.getTargetUser().getUsername());
                break;
            }
        }
    }

    @Override
    public String getStrategyType() {
        return "SEVERITY_BASED";
    }
}
