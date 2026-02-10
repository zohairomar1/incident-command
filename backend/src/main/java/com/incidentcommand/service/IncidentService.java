package com.incidentcommand.service;

import com.incidentcommand.dto.CreateIncidentRequest;
import com.incidentcommand.dto.UpdateIncidentRequest;
import com.incidentcommand.factory.IncidentFactory;
import com.incidentcommand.model.Incident;
import com.incidentcommand.model.User;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.repository.IncidentRepository;
import com.incidentcommand.repository.TeamRepository;
import com.incidentcommand.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final IncidentFactory incidentFactory;
    private final AuditService auditService;

    public IncidentService(IncidentRepository incidentRepository, UserRepository userRepository,
                           TeamRepository teamRepository, IncidentFactory incidentFactory,
                           AuditService auditService) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.incidentFactory = incidentFactory;
        this.auditService = auditService;
    }

    @Transactional
    public Incident createIncident(CreateIncidentRequest request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Incident incident = incidentFactory.create(request, creator);

        if (request.getAssignedTeamId() != null) {
            incident.setAssignedTeam(teamRepository.findById(request.getAssignedTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found")));
        }
        if (request.getAssignedUserId() != null) {
            incident.setAssignedUser(userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found")));
        }

        Incident saved = incidentRepository.save(incident);
        auditService.logAction(saved.getId(), "CREATED", null, saved.getStatus().name(), username);
        return saved;
    }

    @Transactional
    public Incident updateIncident(Long id, UpdateIncidentRequest request, String username) {
        Incident incident = findById(id);
        String previousState = incident.getStatus().name();

        if (request.getTitle() != null) incident.setTitle(request.getTitle());
        if (request.getDescription() != null) incident.setDescription(request.getDescription());
        if (request.getSeverity() != null) incident.setSeverity(request.getSeverity());
        if (request.getType() != null) incident.setType(request.getType());

        if (request.getStatus() != null) {
            updateStatus(incident, request.getStatus());
        }

        if (request.getAssignedTeamId() != null) {
            incident.setAssignedTeam(teamRepository.findById(request.getAssignedTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found")));
        }
        if (request.getAssignedUserId() != null) {
            incident.setAssignedUser(userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found")));
        }

        Incident saved = incidentRepository.save(incident);
        auditService.logAction(saved.getId(), "UPDATED", previousState, saved.getStatus().name(), username);
        return saved;
    }

    @Transactional
    public Incident updateIncidentStatus(Long id, IncidentStatus newStatus, String username) {
        Incident incident = findById(id);
        String previousState = incident.getStatus().name();
        updateStatus(incident, newStatus);
        Incident saved = incidentRepository.save(incident);
        auditService.logAction(saved.getId(), "STATUS_CHANGE", previousState, newStatus.name(), username);
        return saved;
    }

    @Transactional
    public void deleteIncident(Long id, String username) {
        Incident incident = findById(id);
        incidentRepository.delete(incident);
    }

    @Transactional(readOnly = true)
    public Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Incident> findAll() {
        return incidentRepository.findAll();
    }

    public List<Incident> findByAssignedUser(Long userId) {
        return incidentRepository.findByAssignedUserId(userId);
    }

    private void updateStatus(Incident incident, IncidentStatus newStatus) {
        validateStatusTransition(incident.getStatus(), newStatus);
        incident.setStatus(newStatus);
        if (newStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }
    }

    private void validateStatusTransition(IncidentStatus current, IncidentStatus target) {
        boolean valid = switch (current) {
            case OPEN -> target == IncidentStatus.ACKNOWLEDGED || target == IncidentStatus.RESOLVED || target == IncidentStatus.CLOSED;
            case ACKNOWLEDGED -> target == IncidentStatus.RESOLVED || target == IncidentStatus.CLOSED;
            case RESOLVED -> target == IncidentStatus.CLOSED || target == IncidentStatus.OPEN;
            case CLOSED -> false;
        };
        if (!valid) {
            throw new IllegalStateException("Invalid status transition: " + current + " -> " + target);
        }
    }
}
