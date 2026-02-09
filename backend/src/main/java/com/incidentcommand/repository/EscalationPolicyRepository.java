package com.incidentcommand.repository;

import com.incidentcommand.model.EscalationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EscalationPolicyRepository extends JpaRepository<EscalationPolicy, Long> {
    Optional<EscalationPolicy> findByName(String name);
}
