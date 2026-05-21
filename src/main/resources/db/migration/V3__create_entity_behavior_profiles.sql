CREATE TABLE entity_behavior_profiles (
                                          profile_id VARCHAR(255) PRIMARY KEY,

                                          tenant_id VARCHAR(255) NOT NULL,
                                          entity_id VARCHAR(255) NOT NULL,

                                          last_seen_at TIMESTAMP,
                                          last_ip_address VARCHAR(255),
                                          last_device_id VARCHAR(255),

                                          total_events INTEGER NOT NULL DEFAULT 0,

                                          created_at TIMESTAMP NOT NULL,
                                          updated_at TIMESTAMP NOT NULL,

                                          CONSTRAINT fk_behavior_profile_tenant
                                              FOREIGN KEY (tenant_id)
                                                  REFERENCES tenants (tenant_id),

                                          CONSTRAINT fk_behavior_profile_entity
                                              FOREIGN KEY (entity_id)
                                                  REFERENCES entity_records (entity_id),

                                          CONSTRAINT uq_behavior_profile_tenant_entity
                                              UNIQUE (tenant_id, entity_id)
);