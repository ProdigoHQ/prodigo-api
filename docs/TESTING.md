# Testing

> Test stack, current coverage and planned suites for **Prodigo API**.
> See the [documentation index](./README.md) for the other guides.

## Stack

The project uses **JUnit 5**, **Mockito** and **Spring Boot Test**.

Testcontainers dependencies are present, but no container configuration is wired yet:
context and integration tests use the configured PostgreSQL datasource from
`application.properties`.

## Run the tests

```bash
./mvnw test
```

Make sure the configured PostgreSQL database is reachable before running the Spring
context or integration tests.

## Current tests

| Test                                    | Purpose                                                            |
| --------------------------------------- | ------------------------------------------------------------------ |
| `ProdigoErpApplicationTests`            | Spring context load                                                 |
| `OnboardingServiceTest`                 | Tenant onboarding/registration use case                             |
| `AccessProvisioningServiceTest`         | Role/permission provisioning use case                               |
| `PlatformAdminServiceTest`              | Platform admin registration use case                                |
| `TenantModuleServiceTest`               | Tenant module queries and missing-module handling                   |
| `TenantModuleServiceIntegrationTest`    | Tenant module integration with persistence                          |
| `StatusTypeMappingTest`                 | JPA enum persistence, REST status serialization, nullable `last_login` |

## Planned coverage

- Tenant isolation suite.
- RBAC 403/200 suite.
- Module gate tests.
- ArchUnit / Spring Modulith boundary rules.
- Testcontainers-based integration setup.

See [`ROADMAP.md`](./ROADMAP.md) for the broader list of open items.
