# Prodigo ERP — API

> Multi-tenant SaaS ERP backend: **Prodigo API**.

Prodigo ERP is the backend / business engine of the **Prodigo ecosystem**. It exposes
a single REST contract (OpenAPI) consumed by two independent frontends:

```text
                         PRODIGO ECOSYSTEM
                                │
             ┌──────────────────┼──────────────────┐
             │                  │                  │
             ▼                  ▼                  ▼
       prodigo-api         prodigo-web        prodigo-os
       Backend/API         ERP Responsive      Desktop OS
             │                  │                  │
             │                  └────────┬─────────┘
             │                           │
             └────────── REST / OpenAPI ─┘
```

The API is the **source of truth for business logic**: authentication, identity,
multi-tenancy, RBAC, module subscriptions and the ERP domain modules (CRM, Inventory,
Sales, Invoicing, Payments, …).

- **`prodigo-api`** — this repository. Business engine, database, security, modules.
- **`prodigo-web`** — universal responsive ERP (separate repo).
- **`prodigo-os`** — desktop environment (separate repo).

> The three repositories are intentionally decoupled. The only contract between them is
> the OpenAPI definition published by this API. Do not introduce a shared library across
> the repos before real duplication appears.

---

## Table of contents

- [Status](#status)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Security & multi-tenancy](#security--multi-tenancy)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Database & migrations](#database--migrations)
- [Testing](#testing)
- [API reference](./docs/API.md)
- [Known gaps / next steps](#known-gaps--next-steps)
- [License](#license)

---

## Status

This project is a **pre-1.0 development snapshot** (`0.0.1-SNAPSHOT`). It is not a
released/production version yet.

What already works end-to-end:

- Tenant-aware onboarding (register a company + owner user in one call).
- JWT authentication with rotating refresh tokens (HTTP-only cookie).
- Tenant RBAC: system roles/permissions + tenant roles/permissions, assignment APIs.
- Module registry and tenant module subscriptions created at signup.
- Tenant entitlements read API.
- OpenAPI/Swagger, API versioning, global error handling, structured logging.

See [`CHANGELOG.md`](./CHANGELOG.md) for the detailed development history and
[`docs/API.md`](./docs/API.md) for the full API reference.

---

## Tech stack

| Area            | Choice                                                        |
| --------------- | ------------------------------------------------------------- |
| Language        | Java 26                                                       |
| Framework       | Spring Boot 4.1.0 (Spring MVC, Spring Security, Spring Data JPA) |
| Build           | Maven (wrapper `./mvnw`)                                      |
| Database        | PostgreSQL                                                    |
| Migrations      | Flyway (`db/migration`)                                       |
| Auth            | JWT (`jjwt`) + BCrypt, refresh tokens in DB                   |
| Mapping         | MapStruct + Lombok                                            |
| Docs            | springdoc-openapi (Swagger UI)                                |
| Tests           | JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL)        |
| Logging         | Logback (file appender)                                       |

---

## Architecture

**Modular monolith + hexagonal architecture**, organized by business module.

Each module is split into three layers:

```text
<module>/
├── domain/           # Pure business model & rules (no framework deps)
├── application/      # Use cases (ports) + services (implementations)
│   ├── port/input    # Use case interfaces + commands
│   ├── port/output   # SPI the domain needs (persistence, security, …)
│   └── service       # Use case implementations
└── adapter/
    ├── input/rest    # Controllers, DTOs, web mappers  (driving adapters)
    └── output/       # JPA entities, repositories, mappers (driven adapters)
```

Rules of the house:

- **Dependencies point inward.** Adapters depend on the application/domain, never the
  reverse. The domain has no dependency on Spring or JPA.
- **Use-case ports in, SPI ports out.** Controllers call `*UseCase` interfaces; services
  call `*Port` interfaces implemented by adapters.
- **No cross-module database access.** A module never reads another module's tables.
  Cross-module communication goes through facades (ports) or, later, domain events.

Top-level package layout:

```text
com.licode.prodigoerp
├── ProdigoErpApplication.java
├── auth/        # Identity, authentication, RBAC (roles, permissions, users)
├── tenant/      # Tenants, entitlements, tenant module lookup
├── module/      # Module registry + subscriptions
└── common/      # Cross-cutting: config, security, shared ports, exceptions
    ├── config/     # SecurityConfig, PathConfig, WebConfig, TenantContext
    ├── security/   # JwtAuthFilter, JwtUtil, TenantFilter, SecurityUtils
    ├── exception/  # GlobalExceptionHandler + error DTOs
    └── shared/     # CurrentUserPort + adapter
```

The `auth` module currently also owns users/roles/permissions. As the platform grows,
these concerns may be split into dedicated modules.

---

## Security & multi-tenancy

### Request pipeline

Every request flows through this chain:

```text
Request
   │
   ▼
JwtAuthFilter          → parses the Bearer token, builds Authentication
   │                      (roles → ROLE_x, permissions → PERM_x)
   ▼
TenantFilter           → extracts tenantId from the principal and puts
   │                      it in TenantContext (ThreadLocal)
   ▼
Spring Security        → path authorization (public vs authenticated)
   │
   ▼
@PreAuthorize          → method-level authorization (hasRole / hasAuthority)
   │
   ▼
Controller → Use case → Domain
```

The two filters are registered in `common/config/SecurityConfig.java`, and the list of
public paths lives in `common/config/PathConfig.java`.

### JWT & refresh tokens

- Access tokens are JWTs signed with HMAC (configurable secret), default TTL **15 min**.
- Claims include `userId`, `username`, `email`, `tenantId`, `tenantSlug`, `roles` and
  `permissions`. Role claims become `ROLE_*` authorities; permission claims become
  `PERM_*` authorities.
- Refresh tokens are persisted (`refresh_tokens` table), sent as an **HTTP-only,
  SameSite=Strict cookie** named `refresh_token`, default TTL **14 days**, and are
  **rotated** on every `/auth/refresh`.
- Logout revokes the refresh token and clears the cookie + security context.

### Multi-tenancy

**Strategy: shared schema + row-level tenant scoping.**

- `TenantContext` is a `ThreadLocal<UUID>` holding the current tenant for the request.
- `TenantFilter` populates it from the authenticated principal and clears it in a
  `finally` block.
- Tenant-scoped use cases resolve data using `TenantContext.getCurrentTenant()`.
- Users may have a null tenant (platform super admins).

Planned hardening: Hibernate tenant filter and PostgreSQL RLS policies.

### RBAC & permission naming

`User → Roles → Permissions`. Two namespaces exist:

```text
# Platform / Super Admin permissions
ERP.<RESOURCE>.<ACTION>
# e.g. ERP.SYSTEM.ACCESS, ERP.TENANT.READ, ERP.MODULE.UPDATE, ERP.SUBSCRIPTION.SUSPEND

# Module permissions
<MODULE_KEY>.<RESOURCE>.<ACTION>
# e.g. INVENTORY.PRODUCT.CREATE, HR.EMPLOYEE.READ, INVENTORY.STOCK.ADJUST
```

Authorities in the security context are prefixed at load time:

- Roles → `ROLE_<NAME>` → `hasRole('SUPER_ADMIN')`
- Permissions → `PERM_<code>` → `hasAuthority('ERP.ADMIN.CREATE')`

> The permission naming convention (`ERP.*` vs `<module>.<resource>.<action>`) is
> recorded as decision **M0-03**. Note that seeded module permission codes use the
> **uppercase module key** (`INVENTORY.PRODUCT.READ`), not the lowercase form shown in
> early design notes.

---

## Getting started

### Prerequisites

- **JDK 26**
- **PostgreSQL** (running locally or via Docker)
- Maven is not required — use the wrapper (`./mvnw`)

### 1. Create the database

```bash
createdb prodigodb
```

### 2. Configure the connection

Edit `src/main/resources/application.properties` (or override with environment
variables / `--spring.datasource.*`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/prodigodb
spring.datasource.username=postgres
spring.datasource.password=<your-password>

security.jwt.secret=<at-least-32-byte-secret>
security.jwt.expiration=900000
security.jwt.refresh-expiration=604800000
```

> Tip: keep secrets out of version control. Prefer env vars
> (`SPRING_DATASOURCE_PASSWORD`, `SECURITY_JWT_SECRET`, …) over committing real values.

### 3. Run

```bash
./mvnw spring-boot:run
```

Flyway applies the schema and seeds on startup (`spring.flyway.enabled=true`). The API
starts on `http://localhost:8080`.

### 4. API documentation

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Written reference: [`docs/API.md`](./docs/API.md)

### 5. Smoke test

Register a company + owner (public):

```bash
curl -X POST http://localhost:8080/api/1.0/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
        "companyName": "Acme SARL",
        "companySlug": "acme",
        "country": "CM",
        "username": "owner",
        "email": "owner@acme.cm",
        "password": "S3cret!pass",
        "firstName": "Awa",
        "lastName": "Ngu",
        "selectedModules": [
          { "moduleKey": "INVENTORY", "moduleName": "Inventory" }
        ]
      }'
```

Then log in and call an authenticated endpoint:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/1.0/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"owner","password":"S3cret!pass"}' | jq -r .accessToken)

curl http://localhost:8080/api/1.0/tenant/modules \
  -H "Authorization: Bearer $TOKEN"
```

---

## Configuration

Key properties (`src/main/resources/application.properties`):

| Property                        | Purpose                                          |
| ------------------------------- | ------------------------------------------------ |
| `spring.datasource.*`           | PostgreSQL connection                            |
| `spring.jpa.hibernate.ddl-auto` | `validate` — schema is owned by Flyway           |
| `spring.flyway.enabled`         | Enables migrations                               |
| `spring.flyway.locations`       | `db/migration`                                   |
| `security.jwt.secret`           | HMAC signing key                                 |
| `security.jwt.expiration`       | Access token TTL (ms)                            |
| `security.jwt.refresh-expiration` | Refresh token TTL (ms)                         |
| `springdoc.api-docs.path`       | OpenAPI JSON path (`/api-docs`)                  |
| `logging.file.path`             | Log file location                                |

Logging is configured in `src/main/resources/logback-spring.xml`.

---

## Database & migrations

Schema is managed exclusively by **Flyway**. `spring.jpa.hibernate.ddl-auto=validate`
means the JPA entities must match the migrations.

Migrations in `src/main/resources/db/migration`:

| Version | File                                    | Content                                                     |
| ------- | --------------------------------------- | ----------------------------------------------------------- |
| V1      | `V1__create_schema.sql`                 | Core tables + indexes/constraints                           |
| V2      | `V2__seed_system_permissions.sql`       | `ERP.*` platform permissions                                |
| V3      | `V3__seed_modules_and_module_permissions.sql` | Modules (`HR`, `INVENTORY`) + their permissions       |
| V4      | `V4__seed_system_roles.sql`             | System `SUPER_ADMIN` role                                   |
| V5      | `V5__seed_role_permissions.sql`         | Grants all `ERP.*` permissions to `SUPER_ADMIN`             |

A reference, hand-maintained version of the schema also lives at
`src/main/resources/db/sql/prodigo-schema.sql`.

### Tables

```text
tenants                  tenant_entitlements
modules                  module_subscriptions
users                    refresh_tokens
roles                    user_roles
permissions              role_permissions
```

Notable modeling choices:

- All identifiers are **UUID** (generated with `pgcrypto`).
- Auditing columns on core tables: `created_at`, `updated_at`, `created_by`, `updated_by`.
- `roles.tenant_id IS NULL` → **system role**; otherwise tenant-scoped.
- `permissions.tenant_assignable` distinguishes platform vs tenant-grantable permissions.
- Partial unique indexes enforce role-name uniqueness per system / per tenant.
- `module_subscriptions` has `UNIQUE(tenant_id, module_id)` and `status`, `expires_at`.

---

## Testing

Tests live under `src/test/java`. The project is set up for **Testcontainers
(PostgreSQL)** for integration tests, plus Spring Boot slice tests.

```bash
./mvnw test
```

Current tests:

- `ProdigoErpApplicationTests` — context load.
- `OnboardingServiceTest`, `SuperAdminServiceTest`, `AccessProvisioningServiceTest` —
  service/use-case unit tests.
- `TenantModuleServiceTest`, `TenantModuleServiceIntegrationTest` — tenant module
  behavior and integration.

Planned: tenant isolation suite, RBAC 403/200 suite, module gate tests,
ArchUnit / Spring Modulith boundary rules.

---

## Known gaps / next steps

Concrete items visible in the current code:

- **No `GET /me` yet** — identity/tenant/roles/permissions/modules aggregation is planned (M1-13).
- **Subscribe / unsubscribe not implemented** — `TenantModuleController` only exposes reads;
  subscription is currently created during onboarding only.
- **Module gate missing** — a request to a disabled module's endpoint is not yet rejected (M1-21).
- **Route binding bug**: `TenantRolePermissionController` maps `{rolesId}` but binds
  `roleId`, which breaks those endpoints at runtime. Align the template and the variable.
- **Guard gaps**: several tenant RBAC controllers and `AdminModuleController` have no
  active `@PreAuthorize` yet (some annotations are commented out).
- **Secrets in config**: real DB credentials / JWT secret should move to environment
  variables before any deployment.
- **CORS**: `SecurityConfig` currently allows only `http://localhost:3000` — make this
  configurable per environment.
- **Pagination/sorting/filtering** conventions and `PageResponse` are not yet standardized
  across list endpoints.
- **Tenant isolation tests** and **architecture (boundary) tests** have not landed yet.

---

## License

See [`LICENSE`](./LICENSE).
