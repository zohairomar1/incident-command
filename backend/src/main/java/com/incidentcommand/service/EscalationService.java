package com.incidentcommand.service;

import com.incidentcommand.model.EscalationPolicy;
import com.incidentcommand.model.Incident;
import com.incidentcommand.repository.EscalationPolicyRepository;
import com.incidentcommand.repository.IncidentRepository;
import com.incidentcommand.strategy.EscalationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EscalationService {

    private static final Logger log = LoggerFactory.getLogger(EscalationService.class);

    private final IncidentRepository incidentRepository;
    private final EscalationPolicyRepository policyRepository;
    private final Map<String, EscalationStrategy> strategies;
    private final AuditService auditService;

    public EscalationService(IncidentRepository incidentRepository,
                             EscalationPolicyRepository policyRepository,
                             List<EscalationStrategy> strategyList,
                             AuditService auditService) {
        this.incidentRepository = incidentRepository;
        this.policyRepository = policyRepository;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(EscalationStrategy::getStrategyType, Function.identity()));
        this.auditService = auditService;
    }

    @Transactional
    public void triggerEscalation(Long incidentId, Long policyId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));
        EscalationPolicy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + policyId));

        EscalationStrategy strategy = strategies.get(policy.getStrategyType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy type: " + policy.getStrategyType());
        }

        if (strategy.shouldEscalate(incident)) {
            String previousTeam = incident.getAssignedTeam() != null ? incident.getAssignedTeam().getName() : "none";
            strategy.escalate(incident, policy);
            incidentRepository.save(incident);

            String newTeam = incident.getAssignedTeam() != null ? incident.getAssignedTeam().getName() : "none";
            auditService.logAction(incidentId, "ESCALATED", "team:" + previousTeam, "team:" + newTeam, "SYSTEM");
            log.info("Escalation completed for incident {}", incidentId);
        } else {
            log.info("Escalation not needed for incident {} based on {} strategy", incidentId, policy.getStrategyType());
        }
    }

    public List<EscalationPolicy> getAllPolicies() {
        return policyRepository.findAll();
    }
}
