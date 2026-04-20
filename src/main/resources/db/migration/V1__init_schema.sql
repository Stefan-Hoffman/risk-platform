CREATE TABLE tenants (
                         tenant_id      VARCHAR(64) PRIMARY KEY,
                         name           VARCHAR(255) NOT NULL,
                         status         VARCHAR(32) NOT NULL,
                         created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE entity_records (
                                entity_id      VARCHAR(64) PRIMARY KEY,
                                tenant_id      VARCHAR(64) NOT NULL,
                                entity_type    VARCHAR(64) NOT NULL,
                                external_ref   VARCHAR(255),
                                created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_entity_records_tenant
                                    FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id)
);

CREATE TABLE events (
                        event_id       VARCHAR(64) PRIMARY KEY,
                        tenant_id      VARCHAR(64) NOT NULL,
                        entity_id      VARCHAR(64) NOT NULL,
                        event_type     VARCHAR(64) NOT NULL,
                        event_timestamp TIMESTAMP NOT NULL,
                        source         VARCHAR(128) NOT NULL,
                        ip_address     VARCHAR(64),
                        device_id      VARCHAR(128),
                        payload_json   JSONB NOT NULL,
                        created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_events_tenant
                            FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id),
                        CONSTRAINT fk_events_entity
                            FOREIGN KEY (entity_id) REFERENCES entity_records (entity_id)
);

CREATE TABLE enriched_events (
                                 event_id              VARCHAR(64) PRIMARY KEY,
                                 country               VARCHAR(128),
                                 city                  VARCHAR(128),
                                 vpn_detected          BOOLEAN,
                                 device_fingerprint    VARCHAR(255),
                                 known_device          BOOLEAN,
                                 asn                   VARCHAR(255),
                                 CONSTRAINT fk_enriched_events_event
                                     FOREIGN KEY (event_id) REFERENCES events (event_id)
);

CREATE TABLE feature_profiles (
                                  profile_id                 BIGSERIAL PRIMARY KEY,
                                  tenant_id                  VARCHAR(64) NOT NULL,
                                  entity_id                  VARCHAR(64) NOT NULL,
                                  usual_country              VARCHAR(128),
                                  avg_login_hour             INTEGER,
                                  known_device_count         INTEGER,
                                  avg_transaction_amount     NUMERIC(19, 2),
                                  last_updated               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_feature_profiles_tenant
                                      FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id),
                                  CONSTRAINT fk_feature_profiles_entity
                                      FOREIGN KEY (entity_id) REFERENCES entity_records (entity_id)
);

CREATE TABLE risk_rules (
                            rule_id          VARCHAR(64) PRIMARY KEY,
                            tenant_id        VARCHAR(64) NOT NULL,
                            name             VARCHAR(255) NOT NULL,
                            event_type       VARCHAR(64) NOT NULL,
                            conditions_json  JSONB NOT NULL,
                            risk_score       INTEGER NOT NULL,
                            enabled          BOOLEAN NOT NULL DEFAULT TRUE,
                            version          INTEGER NOT NULL,
                            created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_risk_rules_tenant
                                FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id)
);

CREATE TABLE risk_assessments (
                                  assessment_id    VARCHAR(64) PRIMARY KEY,
                                  event_id         VARCHAR(64) NOT NULL,
                                  tenant_id        VARCHAR(64) NOT NULL,
                                  total_score      INTEGER NOT NULL,
                                  decision         VARCHAR(64) NOT NULL,
                                  evaluated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_risk_assessments_event
                                      FOREIGN KEY (event_id) REFERENCES events (event_id),
                                  CONSTRAINT fk_risk_assessments_tenant
                                      FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id)
);

CREATE TABLE rule_hits (
                           rule_hit_id      BIGSERIAL PRIMARY KEY,
                           assessment_id    VARCHAR(64) NOT NULL,
                           rule_id          VARCHAR(64) NOT NULL,
                           risk_score       INTEGER NOT NULL,
                           CONSTRAINT fk_rule_hits_assessment
                               FOREIGN KEY (assessment_id) REFERENCES risk_assessments (assessment_id),
                           CONSTRAINT fk_rule_hits_rule
                               FOREIGN KEY (rule_id) REFERENCES risk_rules (rule_id)
);

CREATE TABLE alerts (
                        alert_id         VARCHAR(64) PRIMARY KEY,
                        tenant_id        VARCHAR(64) NOT NULL,
                        entity_id        VARCHAR(64) NOT NULL,
                        assessment_id    VARCHAR(64) NOT NULL,
                        risk_score       INTEGER NOT NULL,
                        status           VARCHAR(64) NOT NULL,
                        created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_alerts_tenant
                            FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id),
                        CONSTRAINT fk_alerts_entity
                            FOREIGN KEY (entity_id) REFERENCES entity_records (entity_id),
                        CONSTRAINT fk_alerts_assessment
                            FOREIGN KEY (assessment_id) REFERENCES risk_assessments (assessment_id)
);

CREATE TABLE entity_relationships (
                                      relationship_id     BIGSERIAL PRIMARY KEY,
                                      tenant_id           VARCHAR(64) NOT NULL,
                                      entity_id_from      VARCHAR(64) NOT NULL,
                                      entity_id_to        VARCHAR(64) NOT NULL,
                                      relationship_type   VARCHAR(64) NOT NULL,
                                      created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_entity_relationships_tenant
                                          FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id),
                                      CONSTRAINT fk_entity_relationships_from
                                          FOREIGN KEY (entity_id_from) REFERENCES entity_records (entity_id),
                                      CONSTRAINT fk_entity_relationships_to
                                          FOREIGN KEY (entity_id_to) REFERENCES entity_records (entity_id)
);

CREATE INDEX idx_events_entity_id ON events(entity_id);
CREATE INDEX idx_events_event_type ON events(event_type);
CREATE INDEX idx_events_event_timestamp ON events(event_timestamp);
CREATE INDEX idx_risk_rules_event_type ON risk_rules(event_type);
CREATE INDEX idx_entity_relationships_from ON entity_relationships(entity_id_from);
CREATE INDEX idx_entity_relationships_to ON entity_relationships(entity_id_to);