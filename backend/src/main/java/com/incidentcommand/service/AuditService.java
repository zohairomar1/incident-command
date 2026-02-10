package com.incidentcommand.service;

import com.incidentcommand.model.AuditLog;
import com.incidentcommand.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(Long incidentId, String action, String previousState, String newState, String performedBy) {
        AuditLog log = new AuditLog(incidentId, action, previousState, newState, performedBy);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAuditTrail(Long incidentId) {
        return auditLogRepository.findByIncidentIdOrderByTimestampDesc(incidentId);
    }
}
