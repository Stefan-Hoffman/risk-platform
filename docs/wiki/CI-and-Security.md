# CI and Security

The repository includes `.github/workflows/ci.yml` and `.github/dependabot.yml`.

| Check | When | Purpose |
|-------|------|---------|
| Build and test | Pull requests, pushes to `main`, or manual dispatch | Java 21 Maven verification with a disposable PostgreSQL 16 service; runs unit, controller, and integration tests and exercises Flyway migrations |
| Docker build | Same CI triggers | Builds the existing Dockerfile to catch packaging failures; does not publish or deploy an image |
| Dependabot security updates | When an enabled Dependabot alert has an available automated fix | Proposes updates for known vulnerable dependencies; routine version-update PRs are disabled and fixes are not automatically merged |

CI overrides Spring datasource settings with disposable database credentials, so no database secrets or access to a developer database are needed. Test reports are uploaded even after test failures and retained for seven days. The workflow explicitly includes the existing tenant service test class named `TenantService.java`, which Maven's default test naming patterns would otherwise miss.

To activate, commit and push the configuration to GitHub with Actions enabled for the repository. After the first successful run, configure a branch ruleset for `main` requiring **Build and test** and **Docker build** before merging. These repository settings must be enabled separately. Both jobs must pass: the Dockerfile itself skips tests.

Dependency maintenance follows a security-first policy: detect known vulnerabilities, review the proposed remediation, and run CI before merging. All ecosystems have `open-pull-requests-limit: 0`, which disables routine version-update PRs while allowing enabled security updates. The weekly schedule remains only because the configuration schema requires it; security updates are driven by alerts. Version exclusions have been removed so they do not obstruct required fixes.

An administrator must enable **Dependency graph**, **Dependabot alerts**, and **Dependabot security updates** under the repository's security settings. The YAML file alone does not activate vulnerability detection or security fixes. Enable automatic dependency submission for Maven if available to improve coverage of resolved dependencies. Review any existing routine update PRs separately.

A security fix can still require a coordinated framework upgrade and can fail CI. Some alerts cannot be fixed automatically and require manual remediation. For example, Spring Boot 3.3.x pairs with springdoc 2.6.x; use the [springdoc compatibility matrix](https://springdoc.org/#faq) when evaluating an upgrade. Plan occasional maintenance for unsupported dependencies even when no vulnerability alert is present.

Dependabot is not a container-image vulnerability scanner. The Docker job currently checks only that the image builds; scanning operating-system packages inside the image would require a separate scanner such as Trivy or Grype.

Suggested follow-ups:

- CodeQL scanning for Java once code scanning availability is confirmed for the repository.

- JaCoCo coverage reporting, followed by a meaningful coverage baseline for detection and tenant authorization logic.

- Tagged image publishing and deployment when a registry and hosting target have been chosen.

References: [GitHub Maven CI](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven), [PostgreSQL services](https://docs.github.com/en/actions/tutorials/use-containerized-services/create-postgresql-service-containers), and [Dependabot configuration](https://docs.github.com/en/code-security/reference/supply-chain-security/dependabot-options-reference).

## Configuration and Security

- .env is ignored from Git

- Use local development credentials only for local development

- Tenant checks exist in service operations, but some ID-based reads still require tenant scoping

- Authentication, role-based access, and audit history are planned before a tenant pilot

- Global exception handling implemented
