# Roadmap

## Phase 1 (Completed)

- [x] REST API structure

- [x] Multi-tenancy support

- [x] Event ingestion

- [x] Rule engine (basic)

- [x] Risk scoring

- [x] Alert generation

- [x] Unit + controller tests

## Phase 2 (Completed)

- [x] Rule operators (>, <, AND, OR)

- [x] Rule hit tracking

- [x] Pagination + filtering

- [x] Improved validation and error handling

Phases 1 and 2 describe the existing prototype foundation, not production readiness. Phase 3 has started with basic history persistence. Security and tenant administration now precede a customer-facing fraud workflow.

## Phase 3 — Secure Tenant Management and Reliable History (Current)

- [x] Persist basic entity behavior profiles and update them during ingestion

- [x] Unit and integration coverage for basic profile persistence

- [ ] Authentication for tenant users and API integrations (JWT / OAuth2)

- [ ] Derive tenant access from authenticated identity and scope every read/write to that tenant

- [ ] Tenant roles: administrator, analyst, and read-only user

- [ ] Audit history for rule, configuration, and analyst decision changes

- [ ] Make profile creation and counter updates safe under concurrent ingestion

- [ ] Surface invalid rules and evaluation errors explicitly instead of silently treating them as non-matches

- [ ] Add authorization and concurrent ingestion regression coverage

## Phase 4 — Transaction Decisions and Velocity Rules

- [ ] Define a validated transaction event contract: external transaction ID, amount, currency, customer/entity, recipient, and event timestamp

- [ ] Idempotent transaction ingestion so retries do not duplicate assessments, alerts, or history

- [ ] Define timestamp handling and time-window semantics, including late events and whether the current event is counted

- [ ] Add indexed tenant/entity/event-type/time-window queries

- [ ] Build an evaluation context combining payload data with separate server-calculated behavior features

- [ ] Velocity rules for login frequency, transaction frequency, and cumulative spend with explicit currency handling

- [ ] Tenant-managed rule lifecycle and configurable score thresholds

- [ ] Return decision, score, and matched reasons directly from synchronous evaluation

- [ ] Monitoring-only mode and rule preview against historical events before enforcement

- [ ] Document integration responsibility for enforcing allow, review/hold, and block actions

- [ ] Test window boundaries, tenant isolation, retries, and end-to-end rule-to-decision behavior

## Phase 5 — Tenant Fraud Investigation and First Pilot

- [ ] Tenant console for configuring rules and reviewing assessments and alerts

- [ ] Case management: assignment, investigation notes, status transitions, and linked transactions/assessments

- [ ] Record confirmed fraud and legitimate outcomes, analyst identity, and resolution time

- [ ] Record review actions and communicate resolutions to the tenant integration for enforcement

- [ ] Use analyst feedback to measure false positives and guide rule tuning

- [ ] Rate limiting and operational observability (for example, Prometheus/Grafana)

- [ ] Select a target customer segment and validate integration and workflow needs with a pilot

- [ ] Demonstrate a complete transaction review flow: ordinary activity passes, suspicious activity is explained and queued, an analyst resolves the case, and the integration receives the outcome

## Phase 6 — Richer Behavioral Detection

- [ ] Device/IP history and fingerprinting

- [ ] First-time recipient and unfamiliar device signals

- [ ] Risk aggregation over time

- [ ] Behavioral baselines and anomaly detection, such as unusual transaction amounts

- [ ] Expanded tenant dashboards for fraud outcomes and detection performance

## Phase 7 — Relationship Analysis

- [ ] Graph-based analysis (evaluate Neo4j when needed)

- [ ] Shared device/IP detection within authorized tenant boundaries

- [ ] Fraud ring detection

- [ ] Entity relationship scoring

## Phase 8 — Scale and Advanced Scoring (When Justified)

- [ ] Async processing (Kafka/RabbitMQ) where latency and workflow requirements allow it

- [ ] Extract microservices only where measured scaling or operational needs justify them

- [ ] Real-time operational dashboards

- [ ] ML-based scoring once sufficient labeled outcomes and evaluation data exist
