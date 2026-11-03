# Architecture

> System context, module boundaries and code organization for **Prodigo API**.
> See the [documentation index](./README.md) for the other guides.

## Ecosystem context

Prodigo ERP is the backend/business engine of the **Prodigo ecosystem**. It exposes a
single REST contract (OpenAPI) consumed by two independent frontends:

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

- **`prodigo-api`** — this repository. Business engine, database, security and modules.
- **`prodigo-web`** — universal responsive ERP (separate repository).
- **`prodigo-os`** — desktop environment (separate repository).

The repositories are intentionally decoupled. The only contract between them is the
OpenAPI definition published by this API. Do not introduce a shared library across the
repositories before real duplication appears.

The API is the source of truth for business logic: authentication, identity,
multi-tenancy, RBAC, module subscriptions and the ERP domain modules (CRM, Inventory,
Sales, Invoicing, Payments, …).

## Technology stack

| Area       | Choice                                                          |
| ---------- | --------------------------------------------------------------- |
| Language   | Java 26                                                         |
| Framework  | Spring Boot 4.1.0 (Spring MVC, Security, Data JPA)              |
| Build      | Maven wrapper (`./mvnw`)                                        |
| Database   | PostgreSQL                                                      |
| Migrations | Flyway (`db/migration`)                                         |
| Auth       | JWT (`jjwt`) + BCrypt, refresh tokens stored in the database    |
| Mapping    | MapStruct + Lombok                                              |
| Docs       | springdoc-openapi (Swagger UI)                                  |
| Tests      | JUnit 5, Mockito, Spring Boot Test; Testcontainers dependencies |
| Logging    | Logback (file appender)                                         |

## Style: modular monolith + hexagonal architecture

The codebase is a **modular monolith** organized by business module. Each module is split
into three layers:

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

### Rules of the house

- **Dependencies point inward.** Adapters depend on the application/domain, never the
  reverse. The domain has no dependency on Spring or JPA.
- **Use-case ports in, SPI ports out.** Controllers call `*UseCase` interfaces; services
  call `*Port` interfaces implemented by adapters.
- **No cross-module database access.** A module never reads another module's tables.
  Cross-module communication goes through facades (ports) or, later, domain events.
- **One OpenAPI contract.** Frontends integrate through the REST API, not shared code.

## Top-level package layout

```text
com.licode.prodigoerp
├── ProdigoErpApplication.java
├── auth/        # Identity, authentication, RBAC (roles, permissions, users)
├── tenant/      # Tenants, entitlements, tenant module lookup
├── module/      # Module registry + subscriptions
└── common/      # Cross-cutting: config, security, shared ports, exceptions
    ├── autoconfig/ # TenantAutoConfiguration
    ├── config/     # SecurityConfig, PathConfig, WebConfig, TenantContext
    ├── security/   # JwtAuthFilter, JwtUtil, TenantFilter, SecurityUtils
    ├── exception/  # GlobalExceptionHandler + error DTOs
    └── shared/     # CurrentUserPort + adapter
```

The `auth` module currently also owns users, roles and permissions. As the platform
grows, these concerns may be split into dedicated modules.

## Module responsibilities

| Module    | Responsibility                                                                 |
| --------- | ------------------------------------------------------------------------------ |
| `auth`    | Registration/login, JWT/refresh tokens, users, roles, permissions, provisioning |
| `tenant`  | Tenants, entitlements, tenant-scoped module queries                            |
| `module`  | Module registry, module permissions, tenant subscriptions                      |
| `common`  | Security filters, configuration, shared ports, global exceptions                |

## Request flow

The web layer is a thin adapter: controller → use case → domain. Security filters run
before authorization and populate the authenticated user and tenant context. The full
filter chain and authorization model are documented in [`SECURITY.md`](./SECURITY.md).

```text
HTTP request
   │
   ▼
JwtAuthFilter ──► TenantFilter ──► Spring Security path rules
   │
   ▼
@PreAuthorize ──► Controller ──► Use case ──► Domain
   │
   ▼
Adapter / database
```

## Related documentation

- [`SECURITY.md`](./SECURITY.md) — JWT, refresh tokens, tenant context and RBAC.
- [`API.md`](./API.md) — endpoint reference.
- [`DATABASE.md`](./DATABASE.md) — schema and migrations.
