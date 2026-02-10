package com.incidentcommand.strategy;

import com.incidentcommand.model.EscalationPolicy;
import com.incidentcommand.model.EscalationRule;
import com.incidentcommand.model.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TimeBasedEscalation implements EscalationStrategy {

    private static final Logger log = LoggerFactory.getLogger(TimeBasedEscalation.class);

    @Override
    public boolean shouldEscalate(Incident incident) {
        long minutesSinceCreation = ChronoUnit.MINUTES.between(incident.getCreatedAt(), LocalDateTime.now());
        return minutesSinceCreation >= 30;
    }

    @Override
    public void escalate(Incident incident, EscalationPolicy policy) {
        log.info("Time-based escalation triggered for incident {}", incident.getId());

        long minutesSinceCreation = ChronoUnit.MINUTES.between(incident.getCreatedAt(), LocalDateTime.now());

        for (EscalationRule rule : policy.getRules()) {
            if (minutesSinceCreation >= rule.getDelayMinutes()) {
                if (rule.getTargetTeam() != null) {
                    incident.setAssignedTeam(rule.getTargetTeam());
                    log.info("Time-escalated incident {} to team: {} (after {} min delay)",
                            incident.getId(), rule.getTargetTeam().getName(), rule.getDelayMinutes());
                }
                if (rule.getTargetUser() != null) {
                    incident.setAssignedUser(rule.getTargetUser());
                    log.info("Time-escalated incident {} to user: {} (after {} min delay)",
                            incident.getId(), rule.getTargetUser().getUsername(), rule.getDelayMinutes());
                }
            }
        }
    }

    @Override
    public String getStrategyType() {
        return "TIME_BASED";
    }
}
