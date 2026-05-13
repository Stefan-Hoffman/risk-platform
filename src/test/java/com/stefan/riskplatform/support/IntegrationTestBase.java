package com.stefan.riskplatform.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("""
        TRUNCATE TABLE
            rule_hits,
            alerts,
            risk_assessments,
            enriched_events,
            events,
            risk_rules,
            feature_profiles,
            entity_relationships,
            entity_records,
            tenants
        RESTART IDENTITY CASCADE
    """);
    }
}