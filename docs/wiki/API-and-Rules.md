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

## Risk Scoring Logic

Example condition group stored in a rule's `conditionsJson`:
```json
{
  "operator": "AND",
  "conditions": [
    {
      "field": "knownDevice",
      "operator": "EQUALS",
      "value": false
    }
  ]
}
```
Evaluation:
```text
if (payload[field] == value) → add rule.riskScore
```

In this example, `knownDevice` must currently be supplied in the payload; the engine does not calculate it from device history. Scores sum the scores of matched rules, and rule hits retain the matched contributions.

### Decision Thresholds

| Score | Decision |
|-------|----------|
| < 50 | ALLOW   |
| 50–79 | REVIEW   |
| ≥ 80 | BLOCK   |

These thresholds are currently fixed in code. Scores of 50 or above also create an alert. Tenant-configurable thresholds and explained decisions in the ingestion response are planned.
## Assessments

```text
GET /api/v1/assessments/{assessmentId}
```

Use Swagger UI at `/swagger-ui/index.html` for request schemas, required headers, and response models. Ingestion evaluates synchronously but currently returns event/assessment IDs and `ACCEPTED`; retrieve the assessment to inspect the decision.
