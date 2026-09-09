# Product Direction

This project is a learning prototype evolving toward tenant-managed fraud and risk workflows for:

- Fintech platforms

- Banking systems

- Anti-abuse systems

- Identity verification pipelines

The first product milestone is one complete **tenant-managed transaction review flow**: ingest a transaction, calculate behavior features, return an explained decision, let an analyst investigate, and record the resolution. Login abuse detection remains a related use case for the same event and rule engine.

Commercial usefulness still needs validation with a specific customer segment and pilot. The immediate focus is straightforward integration, understandable decisions, and useful analyst workflows.

## Current State and Planned Capabilities

| Area | Implemented today | Planned |
|------|-------------------|---------|
| Tenant management | Tenant records and tenant-scoped operations in several services | Authentication, tenant-bound authorization on every operation, administrator/analyst/read-only roles |
| Detection | Rules over incoming payload fields, scores, assessments, and rule hits | Server-calculated history, velocity rules, tenant-configurable decision thresholds |
| Behavior history | Persisted per-tenant/entity total events, last seen time, last IP, and last device; updated during ingestion | Use history in evaluation, reliable concurrent updates, time-window features |
| Transactions | Generic events can carry transaction data | Validated transaction contract, currency-aware amounts, external transaction IDs, timestamps, and idempotent ingestion |
| Decisions | Synchronous evaluation; ingestion returns event/assessment IDs and `ACCEPTED` | Return score, decision, and reasons directly; support monitoring-only rollout |
| Fraud operations | Alerts and alert status updates | Tenant console, assigned cases, investigation notes, fraud/legitimate outcomes, and audit history |

The application is not yet ready for a customer pilot. Tenant isolation is incomplete in some read paths, authentication is absent, profile increments can lose updates under concurrency, and some rule evaluation failures silently become non-matches. These are explicit items in the roadmap.

### Planned Transaction and Fraud Workflow

```text
Authenticated Tenant Integration
    ↓
Validate Transaction and Deduplicate Retries
    ↓
Load Prior Behavior and Calculate Time-Window Features
    ↓
Evaluate Tenant Rules and Decision Thresholds
    ↓
Return Decision, Score, and Reasons; Persist Assessment
    ↓
Tenant Integration Applies Hold / Block / Allow
    ↓
Analyst Investigates Case and Records Outcome
```

For example, a tenant could choose to review a transaction above a configured amount when the customer has made more than five transactions in ten minutes. The platform would calculate the count itself, explain the rule match, and create a review item. This is a planned example, not a currently supported history-based rule.

An `ALLOW`, `REVIEW`, or `BLOCK` decision is a recommendation from the engine. The tenant's application or payment integration must enforce the corresponding action; the platform does not currently stop or hold payments.

Feature calculation must define whether the current event counts toward a window and preserve prior device/IP values before updating the profile. Server-derived features must remain separate from customer-supplied payload fields.
