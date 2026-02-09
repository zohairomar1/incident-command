CREATE TABLE escalation_policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    strategy_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE escalation_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id BIGINT NOT NULL,
    delay_minutes INT NOT NULL,
    target_team_id BIGINT,
    target_user_id BIGINT,
    rule_order INT NOT NULL,
    FOREIGN KEY (policy_id) REFERENCES escalation_policies(id),
    FOREIGN KEY (target_team_id) REFERENCES teams(id),
    FOREIGN KEY (target_user_id) REFERENCES users(id)
);
