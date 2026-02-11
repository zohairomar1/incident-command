package com.incidentcommand.unit;

import com.incidentcommand.dto.CreateIncidentRequest;
import com.incidentcommand.dto.UpdateIncidentRequest;
import com.incidentcommand.factory.IncidentFactory;
import com.incidentcommand.model.Incident;
import com.incidentcommand.model.Team;
import com.incidentcommand.model.User;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.IncidentType;
import com.incidentcommand.model.enums.Severity;
import com.incidentcommand.repository.IncidentRepository;
import com.incidentcommand.repository.TeamRepository;
import com.incidentcommand.repository.UserRepository;
import com.incidentcommand.service.AuditService;
import com.incidentcommand.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private UserRepository userRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private IncidentFactory incidentFactory;
    @Mock private AuditService auditService;

    @InjectMocks
    private IncidentService incidentService;

    private User testUser;
    private Incident testIncident;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(1L);

        testIncident = new Incident();
        testIncident.setId(1L);
        testIncident.setTitle("Test Incident");
        testIncident.setSeverity(Severity.P2);
        testIncident.setStatus(IncidentStatus.OPEN);
        testIncident.setType(IncidentType.INFRASTRUCTURE);
        testIncident.setCreatedBy(testUser);
        testIncident.setCreatedAt(LocalDateTime.now());
        testIncident.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createIncident_shouldCreateAndReturnIncident() {
        CreateIncidentRequest request = new CreateIncidentRequest("Server Down", "DB server unresponsive",
                Severity.P1, IncidentType.INFRASTRUCTURE, null, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(incidentFactory.create(request, testUser)).thenReturn(testIncident);
        when(incidentRepository.save(testIncident)).thenReturn(testIncident);

        Incident result = incidentService.createIncident(request, "testuser");

        assertNotNull(result);
        assertEquals("Test Incident", result.getTitle());
        verify(incidentRepository).save(testIncident);
        verify(auditService).logAction(eq(1L), eq("CREATED"), isNull(), eq("OPEN"), eq("testuser"));
    }

    @Test
    void createIncident_withInvalidUser_shouldThrow() {
        CreateIncidentRequest request = new CreateIncidentRequest("Test", "Desc",
                Severity.P1, IncidentType.SECURITY, null, null);
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> incidentService.createIncident(request, "unknown"));
    }

    @Test
    void findById_shouldReturnIncident() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));

        Incident result = incidentService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_notFound_shouldThrow() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> incidentService.findById(999L));
    }

    @Test
    void findAll_shouldReturnAllIncidents() {
        when(incidentRepository.findAll()).thenReturn(List.of(testIncident));

        List<Incident> results = incidentService.findAll();

        assertEquals(1, results.size());
    }

    @Test
    void updateIncidentStatus_fromOpenToAcknowledged_shouldSucceed() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(incidentRepository.save(testIncident)).thenReturn(testIncident);

        Incident result = incidentService.updateIncidentStatus(1L, IncidentStatus.ACKNOWLEDGED, "testuser");

        assertEquals(IncidentStatus.ACKNOWLEDGED, result.getStatus());
        verify(auditService).logAction(eq(1L), eq("STATUS_CHANGE"), eq("OPEN"), eq("ACKNOWLEDGED"), eq("testuser"));
    }

    @Test
    void updateIncidentStatus_fromOpenToResolved_shouldSetResolvedAt() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(incidentRepository.save(testIncident)).thenReturn(testIncident);

        Incident result = incidentService.updateIncidentStatus(1L, IncidentStatus.RESOLVED, "testuser");

        assertEquals(IncidentStatus.RESOLVED, result.getStatus());
        assertNotNull(result.getResolvedAt());
    }

    @Test
    void updateIncidentStatus_fromClosedToOpen_shouldThrow() {
        testIncident.setStatus(IncidentStatus.CLOSED);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));

        assertThrows(IllegalStateException.class,
                () -> incidentService.updateIncidentStatus(1L, IncidentStatus.OPEN, "testuser"));
    }

    @Test
    void updateIncident_shouldUpdateFields() {
        UpdateIncidentRequest request = new UpdateIncidentRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Desc");

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(incidentRepository.save(testIncident)).thenReturn(testIncident);

        Incident result = incidentService.updateIncident(1L, request, "testuser");

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Desc", result.getDescription());
    }

    @Test
    void updateIncident_withTeamAssignment_shouldAssignTeam() {
        Team team = new Team("Platform");
        team.setId(1L);
        UpdateIncidentRequest request = new UpdateIncidentRequest();
        request.setAssignedTeamId(1L);

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(incidentRepository.save(testIncident)).thenReturn(testIncident);

        Incident result = incidentService.updateIncident(1L, request, "testuser");

        assertEquals(team, result.getAssignedTeam());
    }

    @Test
    void deleteIncident_shouldDelete() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));

        incidentService.deleteIncident(1L, "admin");

        verify(incidentRepository).delete(testIncident);
    }

    @Test
    void findByAssignedUser_shouldReturnIncidents() {
        when(incidentRepository.findByAssignedUserId(1L)).thenReturn(List.of(testIncident));

        List<Incident> results = incidentService.findByAssignedUser(1L);

        assertEquals(1, results.size());
    }

    @Test
    void createIncident_withInvalidTeam_shouldThrow() {
        CreateIncidentRequest request = new CreateIncidentRequest("Test", "Desc",
                Severity.P1, IncidentType.SECURITY, 999L, null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(incidentFactory.create(request, testUser)).thenReturn(testIncident);
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> incidentService.createIncident(request, "testuser"));
    }

    @Test
    void createIncident_withInvalidAssignedUser_shouldThrow() {
        CreateIncidentRequest request = new CreateIncidentRequest("Test", "Desc",
                Severity.P1, IncidentType.SECURITY, null, 999L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(incidentFactory.create(request, testUser)).thenReturn(testIncident);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> incidentService.createIncident(request, "testuser"));
    }

    @Test
    void updateIncident_withInvalidTeam_shouldThrow() {
        UpdateIncidentRequest request = new UpdateIncidentRequest();
        request.setAssignedTeamId(999L);

        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> incidentService.updateIncident(1L, request, "testuser"));
    }

    @Test
    void updateIncidentStatus_fromResolvedToOpen_shouldSucceed() {
        testIncident.setStatus(IncidentStatus.RESOLVED);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));
        when(incidentRepository.save(testIncident)).thenReturn(testIncident);

        Incident result = incidentService.updateIncidentStatus(1L, IncidentStatus.OPEN, "testuser");

        assertEquals(IncidentStatus.OPEN, result.getStatus());
    }

    @Test
    void updateIncidentStatus_fromAcknowledgedToOpen_shouldThrow() {
        testIncident.setStatus(IncidentStatus.ACKNOWLEDGED);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(testIncident));

        assertThrows(IllegalStateException.class,
                () -> incidentService.updateIncidentStatus(1L, IncidentStatus.OPEN, "testuser"));
    }
}
