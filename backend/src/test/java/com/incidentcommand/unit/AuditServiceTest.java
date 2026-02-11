package com.incidentcommand.unit;

import com.incidentcommand.model.AuditLog;
import com.incidentcommand.repository.AuditLogRepository;
import com.incidentcommand.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void logAction_shouldSaveAuditLog() {
        auditService.logAction(1L, "CREATED", null, "OPEN", "admin");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertEquals(1L, saved.getIncidentId());
        assertEquals("CREATED", saved.getAction());
        assertNull(saved.getPreviousState());
        assertEquals("OPEN", saved.getNewState());
        assertEquals("admin", saved.getPerformedBy());
    }

    @Test
    void logAction_withPreviousAndNewState_shouldSaveBoth() {
        auditService.logAction(2L, "STATUS_CHANGE", "OPEN", "ACKNOWLEDGED", "responder");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertEquals("OPEN", saved.getPreviousState());
        assertEquals("ACKNOWLEDGED", saved.getNewState());
    }

    @Test
    void getAuditTrail_shouldReturnLogs() {
        AuditLog log1 = new AuditLog(1L, "CREATED", null, "OPEN", "admin");
        AuditLog log2 = new AuditLog(1L, "STATUS_CHANGE", "OPEN", "ACKNOWLEDGED", "responder");
        when(auditLogRepository.findByIncidentIdOrderByTimestampDesc(1L)).thenReturn(List.of(log2, log1));

        List<AuditLog> result = auditService.getAuditTrail(1L);

        assertEquals(2, result.size());
        assertEquals("STATUS_CHANGE", result.get(0).getAction());
    }

    @Test
    void getAuditTrail_whenNoLogs_shouldReturnEmptyList() {
        when(auditLogRepository.findByIncidentIdOrderByTimestampDesc(999L)).thenReturn(Collections.emptyList());

        List<AuditLog> result = auditService.getAuditTrail(999L);

        assertTrue(result.isEmpty());
    }
}
