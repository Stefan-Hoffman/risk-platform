# Fraud and Risk Management Platform

A multi-tenant Spring Boot API for evaluating events against configurable risk rules, recording assessments, and generating alerts.

The project is evolving toward tenant-managed transaction screening and fraud investigation. Authentication, complete tenant authorization, and analyst case management are still planned.

## Features

- Event ingestion and tenant-specific risk rules
- JSON conditions with comparison operators and AND/OR groups
- Risk scores, ALLOW/REVIEW/BLOCK decisions, and rule hit tracking
- Alerts with status management
- Basic entity behavior history
- Paginated and filtered APIs, validation, and centralized error handling
- Unit, controller, and PostgreSQL integration tests

## Tech stack

Java 21 · Spring Boot 3 · Spring Data JPA · PostgreSQL 16 · Flyway · MapStruct · Maven

## Getting started

Use Docker Desktop or Rancher Desktop with the dockerd (Moby) engine and Docker Compose. Container builds include Java 21; install Java 21 locally for IDE or Maven runs.

```bash
git clone https://github.com/Stefan-Hoffman/risk-platform.git
cd risk-platform
```

Create a `.env` file in the project root with local development settings:

```dotenv
DB_HOST=localhost
DB_PORT=5432
DB_NAME=risk_platform
DB_USER=risk_user
DB_PASSWORD=risk_pass
```

Start the database and application:

```bash
docker compose up --build
```

The API runs at `http://localhost:8080`. Open [Swagger UI](http://localhost:8080/swagger-ui/index.html) to explore endpoints and request schemas. Flyway applies database migrations at startup.

Stop the containers with `docker compose down`. Database data is retained in a Docker volume. If the application starts before PostgreSQL is ready, rerun the startup command once the database is healthy.

For local Java/IDE setup and troubleshooting, see [Development](https://github.com/Stefan-Hoffman/risk-platform/wiki/Development).

## Usage

Create a tenant and entity, configure an event rule, then submit an event through Swagger UI. Ingestion returns event and assessment IDs; retrieve the assessment to inspect its score, decision, and matched rules.

Rules currently evaluate incoming payload fields. Stored behavior history is not yet used in scoring, and a BLOCK decision does not itself stop a payment.

See [API and Rules](https://github.com/Stefan-Hoffman/risk-platform/wiki/API-and-Rules) for endpoints, rule examples, and decision thresholds.

## Testing

Use Java 21 and configure a **dedicated PostgreSQL test database** before running tests. Integration tests clear application tables. See [test setup](https://github.com/Stefan-Hoffman/risk-platform/wiki/Development#running-tests-locally).

```bash
./mvnw test
```

On Windows, use `.\mvnw.cmd test`. The wiki includes the full CI-equivalent command, including the tenant service tests that do not follow Maven's default filename pattern.

GitHub Actions runs Maven verification and a Docker build on pull requests and pushes to `main`. Dependency maintenance uses security-driven updates rather than routine version bumps.

## Roadmap

The next milestone is secure tenant management and reliable behavior history, followed by transaction velocity rules and a complete analyst review workflow. Graph analysis and ML scoring are longer-term work.

See the [full roadmap](https://github.com/Stefan-Hoffman/risk-platform/wiki/Roadmap).

## Documentation

Detailed documentation lives in the [GitHub Wiki](https://github.com/Stefan-Hoffman/risk-platform/wiki):

- [Development](https://github.com/Stefan-Hoffman/risk-platform/wiki/Development)
- [Architecture](https://github.com/Stefan-Hoffman/risk-platform/wiki/Architecture)
- [API and Rules](https://github.com/Stefan-Hoffman/risk-platform/wiki/API-and-Rules)
- [Database](https://github.com/Stefan-Hoffman/risk-platform/wiki/Database)
- [CI and Security](https://github.com/Stefan-Hoffman/risk-platform/wiki/CI-and-Security)
- [Product Direction](https://github.com/Stefan-Hoffman/risk-platform/wiki/Product-Direction)
- [Roadmap](https://github.com/Stefan-Hoffman/risk-platform/wiki/Roadmap)

Wiki source files are also kept in [docs/wiki](docs/wiki/Home.md). Publishing changes to the main repository does not automatically update the separate wiki repository.
