package com.incidentcommand.repository;

import com.incidentcommand.model.Incident;
import com.incidentcommand.model.enums.IncidentStatus;
import com.incidentcommand.model.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findBySeverity(Severity severity);

    List<Incident> findByStatus(IncidentStatus status);

    List<Incident> findByAssignedTeamId(Long teamId);

    List<Incident> findByAssignedUserId(Long userId);

    @Query("SELECT i FROM Incident i WHERE i.severity = :severity AND i.status = :status")
    List<Incident> findBySeverityAndStatus(@Param("severity") Severity severity, @Param("status") IncidentStatus status);

    @Query("SELECT i FROM Incident i WHERE i.createdAt BETWEEN :start AND :end")
    List<Incident> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT i.severity, COUNT(i) FROM Incident i GROUP BY i.severity")
    List<Object[]> countBySeverityGrouped();

    @Query("SELECT i.status, COUNT(i) FROM Incident i GROUP BY i.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT i.assignedTeam.name, COUNT(i) FROM Incident i WHERE i.assignedTeam IS NOT NULL GROUP BY i.assignedTeam.name")
    List<Object[]> countByTeamGrouped();

    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, i.createdAt, i.resolvedAt)) FROM Incident i WHERE i.resolvedAt IS NOT NULL")
    Double calculateMeanTimeToResolve();

    long countByStatus(IncidentStatus status);

    @Query("SELECT i FROM Incident i WHERE i.status IN ('OPEN', 'ACKNOWLEDGED') AND i.createdAt < :threshold")
    List<Incident> findStaleIncidents(@Param("threshold") LocalDateTime threshold);
}
