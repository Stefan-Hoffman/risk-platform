# Development

## 1. Clone the repository

```bash
git clone https://github.com/Stefan-Hoffman/risk-platform.git
cd risk-platform
```

## Docker Setup

The platform supports containerized local development using Docker and Docker Compose.

## Project Docker Files

The following files are included in the project root:

```text
Dockerfile
docker-compose.yml
.dockerignore
```

## Environment Configuration

Create a `.env` file in the project root:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=risk_platform
DB_USER=risk_user
DB_PASSWORD=risk_pass
```

## Build and Run

From the project root:

```bash
docker compose up --build
```

This will:
1. Build the Spring Boot application image
2. Start the PostgreSQL container
3. Run Flyway migrations automatically
4. Start the API on port 8080

## Accessing the Application

API:
```text
http://localhost:8080
```

Swagger/OpenAPI:
```text
http://localhost:8080/swagger-ui/index.html
```

## Stopping Containers

```bash
docker compose down
```

## Rebuilding After Changes

```bash
docker compose up --build
```

## Database Persistence

PostgreSQL data is persisted using Docker volumes.

This ensures:

- container restarts do not lose data

- local development data remains available

## Docker Networking Notes

Inside Docker Compose:

- the Spring Boot application connects to PostgreSQL using the container service name

- `DB_HOST=postgres`

Example internal connection:

```text
jdbc:postgresql://postgres:5432/risk_platform
```

Outside Docker (local IntelliJ runs):

- `DB_HOST=localhost`

## Flyway Migrations

Flyway migrations automatically execute on application startup.

Migration files are located in:

```text
src/main/resources/db/migration
```

Example:

```text
V1__init_schema.sql
```

## When not using Docker

### Configure IntelliJ environment variables

```
DB_HOST=localhost;DB_PORT=5432;DB_NAME=risk_platform;DB_USER=risk_user;DB_PASSWORD=risk_pass
```

### Run the Application

```
./mvnw spring-boot:run
```

### Open Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```
## Running tests locally

Use Java 21 and a dedicated PostgreSQL test database. Integration tests truncate application tables before each test; never point them at a development database containing data you want to keep or at production.

PowerShell example after creating an empty `risk_platform_test` database and a user with access to it:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/risk_platform_test'
$env:SPRING_DATASOURCE_USERNAME = 'risk_user'
$env:SPRING_DATASOURCE_PASSWORD = 'risk_pass'
.\mvnw.cmd --batch-mode --no-transfer-progress '-Dtest=*Test,Test*,*Tests,*TestCase,com.stefan.riskplatform.tenant.service.TenantService' verify
```

Use credentials for your test database. Spring environment overrides take precedence over the local test YAML. The explicit test selector includes the tenant service test class whose filename does not follow Maven's default test pattern. CI supplies its own disposable database.

On Linux/macOS, export the same environment variables and use `./mvnw` instead of `.\mvnw.cmd`.

## Windows and Rancher Desktop

Rancher Desktop with the **dockerd (Moby)** engine can run this repository's Docker commands. Verify `docker version` and `docker compose version` in your terminal. Java 21 is required on the host for Maven/IDE runs; the Dockerfile supplies Java 21 for container builds.

For local Windows application runs, use `.\mvnw.cmd spring-boot:run` with the `DB_*` environment variables above. A `.env` file is read by Docker Compose; local Maven/IDE runs need those variables set explicitly.

Compose starts PostgreSQL before the application but does not currently wait for database health. If the first startup reports connection refused, wait for PostgreSQL to become healthy and run `docker compose up --build` again.
