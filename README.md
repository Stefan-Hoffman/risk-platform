# Risk Detection Platform (Spring Boot)

A multi-tenant **risk detection and alerting platform** built with Spring Boot.

The system ingests events, evaluates them against configurable rules, calculates a risk score, and generates alerts when thresholds are exceeded.

---

# Overview

This project simulates a real-world **fraud detection / risk engine** used in:

- Fintech platforms
- Banking systems
- Anti-abuse systems
- Identity verification pipelines

---

# ️ Core Features

- Multi-tenant architecture (tenant isolation)
- Event ingestion API
- Rule-based risk scoring engine
- JSON-based rule conditions
- Risk assessment generation
- Alert creation based on thresholds
- REST API with validation
- Global exception handling
- Unit tests (services)
- Controller tests (MockMvc)

---

# Architecture

## Event Processing Pipeline

Event → Rule Matching → Score Calculation → Decision → Alert


---

# Tech Stack

- Java 17+
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway (DB migrations)
- MapStruct (DTO mapping)
- Lombok
- JUnit 5 + Mockito
- MockMvc (controller testing)
- Docker (PostgreSQL)

---

# Getting Started

## 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/risk-platform.git
cd risk-platform
```

## 2. Create .env file
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=risk_platform
DB_USER=risk_user
DB_PASSWORD=risk_pass
SPRING_PROFILES_ACTIVE=dev
```

## 3. Start PostGreSQL

## 4. Configure IntelliJ environment variables
```
DB_HOST=localhost;DB_PORT=5432;DB_NAME=risk_platform;DB_USER=risk_user;DB_PASSWORD=risk_pass
```

## 5. Run the Application
```
./mvnw spring-boot:run
```

## 6. Open Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

---
# API Endpoints

## Tenant
```
POST /api/v1/tenants
```

## Entity
```text
POST /api/v1/entities
GET  /api/v1/entities
```

## Rules
```text
POST /api/v1/rules
GET  /api/v1/rules?eventType=LOGIN
```

## Events 
```text
POST /api/v1/events
GET  /api/v1/events/{eventId}
```

## Alerts
```text
GET   /api/v1/alerts?status=OPEN
PATCH /api/v1/alerts/{alertId}/status
```

---
# Risk Scoring Logic

Each rule contains:
```json
{
  "field": "knownDevice",
  "value": false
}
```
Evaluation:
```text
if (payload[field] == value) → add rule.riskScore
```

---
## Decision Thresholds
| Score | Decision |
|-------|----------|
| < 50 | ALLOW   |
| 50–79 | REVIEW   |
| ≥ 80 | BLOCK   |

---
# Database Design

## Entities
- Tenant
- EntityRecord
- Event
- RiskRule
- RiskAssessment
- Alert

## ERD
![](assets/mermaid-diagram.png)

---
# Testing
Run Tests:
```bash
./mvnw test
```
Includes:
- Unit tests (service layer)
- Controller tests (MockMvc)

---
# Configuration and Security

- .env is ignored from Git
- No credentials stored in the code
- Tenant isolation enforced in service layer
- Global exception handling implemented

---
# Roadmap

## Phase 1 (Completed)
- REST API structure
- Multi-tenancy support
- Event ingestion
- Rule engine (basic)
- Risk scoring
- Alert generation
- Unit + controller tests

## Phase 2 (Next)
- Rule operators (>, <, AND, OR)
- Rule hit tracking
- Pagination + filtering
- Improved validation and error handling

## Phase 3
- Feature store (user behavior history)
- Velocity rules (e.g. login frequency)
- Device/IP fingerprinting
- Risk aggregation over time

## Phase 4
- Graph-based analysis (Neo4j)
- Shared device/IP detection
- Fraud ring detection

## Phase 5
- Async processing (Kafka/RabbitMQ)
- Microservices architecture
- Real-time dashboards
- ML-based scoring

## Future Improvements
- Authentication (JWT / OAuth2)
- Role-based access control
- Audit logging
- Rate limiting
- Observability (Prometheus, Grafana)
- Frontend (Angular - TBD)
---

# Notes

This project is purely for the intention of upskilling and learning. Any feedback would be appreciated.