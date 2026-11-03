# Roadmap & Known Gaps

> Current status, open issues and next steps for **Prodigo API**.
> See the [documentation index](./README.md) for the other guides.

## Status

The project is a **pre-1.0 development snapshot** (`0.0.1-SNAPSHOT`). It is not a
released or production-ready version yet.

What works end-to-end today:

- Tenant-aware onboarding (register a company + owner user in one call).
- JWT authentication with rotating refresh tokens (HTTP-only cookie).
- Platform administration: create, list, inspect and update platform admins.
- Tenant RBAC: system and tenant roles/permissions, assignment APIs and endpoint guards.
- Module registry and tenant module subscriptions created at signup.
- Tenant entitlements read API.
- Typed status enums persisted as strings and enforced with database check constraints.
- OpenAPI/Swagger, API versioning, global error handling and structured logging.

## Known gaps

Concrete items visible in the current code:

- **No `GET /me` yet** — identity/tenant/roles/permissions/modules aggregation is planned
  (M1-13).
- **Subscribe / unsubscribe not implemented** — `TenantModuleController` only exposes
  reads; subscription is currently created during onboarding only.
- **Module gate missing** — a request to a disabled module's endpoint is not yet rejected
  (M1-21).
- **Route binding bug** — `TenantRolePermissionController` maps `{rolesId}` but binds
  `roleId`, which breaks those endpoints at runtime. Align the template and the variable.
- **Platform role guard bug** — `PlatformRoleController` uses
  `@PreAuthorize("hasRole('PERM_ERP.ROLE.READ')")` at class level. `hasRole` adds the
  `ROLE_` prefix, so this checks `ROLE_PERM_ERP.ROLE.READ` instead of the intended
  `PERM_ERP.ROLE.READ` authority and can deny the class-guarded role read endpoints. Use
  `hasAuthority`.
- **Guard gap** — `AdminModuleController` still has no active `@PreAuthorize` permission
  guard (the path is authenticated, but any valid user can create modules).
- **Weak onboarding validation** — `selectedModules` is not validated; a missing list
  fails downstream while parsing subscriptions.
- **Secrets in config** — real DB credentials / JWT secret are committed in
  `application.properties` and should move to environment variables before any
  deployment.
- **CORS** — `SecurityConfig` currently allows only `http://localhost:3000`; make this
  configurable per environment.
- **Refresh cookie/token mismatch** — the cookie is issued with a 14-day `Max-Age`, while
  the persisted refresh token uses `security.jwt.refresh-expiration` (7 days in the
  checked-in configuration). Align both lifetimes before production.
- **Error-handler signature** — `GlobalExceptionHandler.handleAccessDenied` lists
  `ForbiddenException` in `@ExceptionHandler` but accepts `AccessDeniedException`, so a
  thrown `ForbiddenException` can fall through to the generic 500 handler. Give the
  handler a matching parameter.
- **Pagination/sorting/filtering** conventions and `PageResponse` are not yet standardized
  across list endpoints.
- **Tenant isolation tests** and **architecture (boundary) tests** have not landed yet.

## Planned hardening

- Hibernate tenant filter and PostgreSQL RLS policies.
- Module gate enforcement for disabled/unsubscribed modules.
- Standardized list responses (pagination, sorting, filtering).
- Testcontainers-based integration test setup.
- Configuration hardening: environment-based secrets and per-environment CORS.

## Planned ERP modules

`CRM`, `SALES`, `INVOICING`, `PAYMENTS`, `POS`, `PROJECTS`, `ECOMMERCE`, `REPORTING`.
