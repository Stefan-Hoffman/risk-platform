# Database Design

## Entities

- Tenant

- EntityRecord

- Event

- RiskRule

- RiskAssessment

- RuleHit

- Alert

- EntityBehaviorProfile

## ERD

![Database entity relationship diagram](https://raw.githubusercontent.com/Stefan-Hoffman/risk-platform/main/assets/mermaid-diagram.png)

Flyway migrations in `src/main/resources/db/migration` are the source of truth for the current schema; the diagram may lag behind newer migrations.
