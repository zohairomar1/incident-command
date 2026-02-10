package com.incidentcommand.strategy;

import com.incidentcommand.model.EscalationPolicy;
import com.incidentcommand.model.Incident;

public interface EscalationStrategy {
    boolean shouldEscalate(Incident incident);
    void escalate(Incident incident, EscalationPolicy policy);
    String getStrategyType();
}
