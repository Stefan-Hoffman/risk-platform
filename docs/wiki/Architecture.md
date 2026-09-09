# Architecture

## Architecture Overview

The platform currently follows a layered modular monolith architecture using Spring Boot.

### High-Level Flow

```text
Client Request
    ↓
Controller Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
PostgreSQL
```

### Core Architectural Components

#### Controller Layer

Responsible for exposing REST APIs and handling HTTP requests/responses.

Examples:

- TenantController

- EventController

- RiskRuleController

- AlertController

Responsibilities:

- Request validation

- Request mapping

- Response formatting

- Delegating business logic to services

#### Service Layer

Contains the core business logic of the platform.

Examples:

- EventService

- RiskRuleService

- RiskEngineService

- AlertService

- RiskAssessmentService

Responsibilities:

- Event ingestion

- Rule evaluation

- Risk scoring

- Alert generation

- Tenant validation

- Assessment creation

The service layer acts as the orchestration layer of the platform.

#### Repository Layer

Handles persistence and database access using Spring Data JPA.

Examples:

- EventRepository

- RiskRuleRepository

- AlertRepository

Responsibilities:

- CRUD operations

- Filtering

- Pagination

- Tenant-specific data retrieval

#### Database Layer

The platform uses PostgreSQL as the primary relational database.

Database schema management is handled through Flyway migrations.

Key entities include:

- Tenants

- Events

- Risk Rules

- Risk Assessments

- Rule Hits

- Alerts

- Entity Records

- Entity Behavior Profiles

### Rule Engine Architecture

Incoming events are evaluated against configurable risk rules stored in the database.

Flow:

```text
Event Ingestion
    ↓
Persist Event and Update Basic Behavior Profile
    ↓
Load Enabled Rules
    ↓
Evaluate Rule Conditions
    ↓
Calculate Total Risk Score
    ↓
Create Risk Assessment
    ↓
Generate Alert (if threshold exceeded)
```

Supported rule operators:

- EQUALS

- NOT_EQUALS

- GREATER_THAN

- LESS_THAN

- GREATER_THAN_OR_EQUALS

- LESS_THAN_OR_EQUALS

- AND

- OR

Evaluation currently reads only the event payload. Stored behavior profiles do not yet influence scores. `FeatureProfile` and `EnrichedEvent` provide model scaffolding for future capabilities; their presence does not mean feature calculation or fingerprinting is implemented.

### Exception Handling

The platform uses centralized global exception handling through:

```java
@RestControllerAdvice
```

This avoids excessive try/catch blocks throughout controllers and services while ensuring standardized API error responses.

### Mapping Strategy

The platform uses MapStruct for DTO ↔ Entity mapping.

Benefits:

- Cleaner services/controllers

- Reduced boilerplate

- Better separation of concerns

### Current Architectural Direction

The project intentionally starts as a modular monolith to:

- simplify development

- reduce operational complexity

- iterate quickly on business logic

The planned fraud workflows can be implemented within this modular monolith. Service extraction and asynchronous processing will be considered when measured scaling or operational requirements justify them.

Potential future service extraction areas:

- Event ingestion service

- Rule evaluation service

- Alerting service

- Graph analysis service

- Feature store service
