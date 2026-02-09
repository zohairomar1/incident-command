package com.incidentcommand.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_id", nullable = false)
    private Long incidentId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "previous_state", length = 1000)
    private String previousState;

    @Column(name = "new_state", length = 1000)
    private String newState;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public AuditLog(Long incidentId, String action, String previousState, String newState, String performedBy) {
        this.incidentId = incidentId;
        this.action = action;
        this.previousState = previousState;
        this.newState = newState;
        this.performedBy = performedBy;
    }
}
