package com.incidentcommand.unit;

import com.incidentcommand.model.EscalationPolicy;
import com.incidentcommand.model.Incident;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import com.incidentcommand.repository.EscalationPolicyRepository;
import com.incidentcommand.repository.IncidentRepository;
import com.incidentcommand.service.AuditService;
import com.incidentcommand.service.EscalationService;
import com.incidentcommand.strategy.EscalationStrategy;
import com.incidentcommand.strategy.SeverityBasedEscalation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalationServiceTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private EscalationPolicyRepository policyRepository;
    @Mock private AuditService auditService;

    private EscalationService escalationService;
    private EscalationStrategy severityStrategy;

    @BeforeEach
    void setUp() {
        severityStrategy = new SeverityBasedEscalation();
        escalationService = new EscalationService(
                incidentRepository, policyRepository, List.of(severityStrategy), auditService);
    }

    @Test
    void triggerEscalation_withInvalidIncident_shouldThrow() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> escalationService.triggerEscalation(999L, 1L));
    }

    @Test
    void triggerEscalation_withInvalidPolicy_shouldThrow() {
        Incident incident = createTestIncident();
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> escalationService.triggerEscalation(1L, 999L));
    }

    @Test
    void triggerEscalation_withUnknownStrategy_shouldThrow() {
        Incident incident = createTestIncident();
        EscalationPolicy policy = new EscalationPolicy("Test Policy", "UNKNOWN_TYPE");
        policy.setId(1L);

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThrows(IllegalArgumentException.class,
                () -> escalationService.triggerEscalation(1L, 1L));
    }

    @Test
    void getAllPolicies_shouldReturnAllPolicies() {
        EscalationPolicy policy = new EscalationPolicy("Default", "SEVERITY_BASED");
        when(policyRepository.findAll()).thenReturn(List.of(policy));

        List<EscalationPolicy> result = escalationService.getAllPolicies();

        assertEquals(1, result.size());
        assertEquals("Default", result.get(0).getName());
    }

    @Test
    void triggerEscalation_whenShouldEscalate_shouldSaveAndAudit() {
        // Create incident old enough to trigger severity-based escalation (P1 > 15 min)
        Incident incident = createTestIncident();
        incident.setCreatedAt(LocalDateTime.now().minusMinutes(20));

        EscalationPolicy policy = new EscalationPolicy("Default", "SEVERITY_BASED");
        policy.setId(1L);
        policy.setRules(new java.util.ArrayList<>());

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(incidentRepository.save(incident)).thenReturn(incident);

        escalationService.triggerEscalation(1L, 1L);

        verify(incidentRepository).save(incident);
        verify(auditService).logAction(eq(1L), eq("ESCALATED"), anyString(), anyString(), eq("SYSTEM"));
    }

    @Test
    void triggerEscalation_whenShouldNotEscalate_shouldNotSave() {
        // Create recent incident that should NOT trigger escalation
        Incident incident = createTestIncident();
        incident.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        EscalationPolicy policy = new EscalationPolicy("Default", "SEVERITY_BASED");
        policy.setId(1L);

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        escalationService.triggerEscalation(1L, 1L);

        verify(incidentRepository, never()).save(any());
        verify(auditService, never()).logAction(anyLong(), anyString(), anyString(), anyString(), anyString());
    }

    private Incident createTestIncident() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setTitle("Test");
        incident.setSeverity(Severity.P1);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setType(IncidentType.INFRASTRUCTURE);
        incident.setCreatedAt(LocalDateTime.now());
        return incident;
    }
}
