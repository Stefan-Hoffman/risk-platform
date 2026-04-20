ALTER TABLE risk_rules
    ALTER COLUMN conditions_json TYPE TEXT;

ALTER TABLE events
    ALTER COLUMN payload_json TYPE TEXT;