# Changelog

All notable project changes documented here are reconstructed from the repository's git history, current source structure, and the schema/configuration present in the codebase.

## Release state and versioning

This repository does not currently expose Git tags or GitHub release metadata. A `git tag` check returned no tags, and the project artifact is still in a snapshot state:

- `pom.xml` declares version `0.0.1-SNAPSHOT`
- `git status` / branch history shows a development branch (`main`) without published release tags

Therefore, the project is best understood as a pre-1.0 development snapshot rather than a numbered production release. The sections below are grouped by meaningful development milestones rather than by formal release tags.

## Unreleased / current snapshot (0.0.1-SNAPSHOT)

The current codebase reflects a multi-tenant ERP backend built around Spring Boot, PostgreSQL, JWT-based authentication, tenant-aware RBAC, module subscriptions, and tenant onboarding flows.

### Current capabilities evidenced in the codebase

- Multi-tenant tenant and entitlement model with tenant-scoped context and route security
- JWT authentication and refresh-token persistence
- Role and permission catalogs with system-level and tenant-level access control
- Module registration and tenant module subscription flow
- Super-admin and tenant-admin provisioning logic
- OpenAPI/Swagger integration and API versioning under `/api/{version}`
- Application schema initialized through `src/main/resources/db/sql/prodigo-schema.sql`

### Notable current architecture

- Security is configured through `common/config/SecurityConfig.java` with CORS, JWT auth filtering, and tenant filtering
- Public endpoints are enumerated in `common/config/PathConfig.java`
- The database schema includes `tenants`, `tenant_entitlements`, `modules`, `module_subscriptions`, `users`, `roles`, `user_roles`, `permissions`, `role_permissions`, and `refresh_tokens`
- The project follows a hexagonal / layered architecture with separate application, domain, adapter, and common package groups

## 2026-10-03 to 2026-10-09 — Platform and tenant RBAC administration completes the access model

### Added

- Platform permission catalog service and adapters (`feat: Add permission catalog use case and adapter`)
- System role service CRUD logic (`feat: Implement system role service CRUD logic`)
- Tenant role and permission app services (`feat: Add tenant role and permission app services`)
- System role-permission service (`feat: Add system role-permission service`)
- Access provisioning and query contracts/services (`feat: Add access provisioning/query contracts`, `feat: Add access provisioning and query services`)
- Tenant role and permission REST controllers (`feat: Add tenant role/permission REST controllers`)
- Platform role-permission REST controller (`feat: Add platform role-permission REST controller`)
- Permission catalog admin API (`feat: Add permission catalog admin API`)
- Expanded CRUD endpoints for platform roles (`refactor: expand platform role controller CRUD APIs`)

### Refactoring and cleanup

- Obsolete auth wiring was removed (`refactor: Remove obsolete auth use case/service wiring`)
- Current-user and module-subscription logic was simplified (`refactor: Use current user and simplify module subscription`)
- Registration/orchestration logic was centralized under `OnboardingService` (`refactor: the registration/orchestration service into OnboardingService`)
- Super-admin provisioning flow was refined (`refactor: super admin provisioning flow`)

### Validation and test coverage

- Service tests were added and updated for onboarding and provisioning flows (`feat(test): Add AccessProvisioningService unit tests`, `refactor(test): OnboardingService unit tests`, `feat(test): Align service tests with new auth ports`)
- Additional tests cover tenant module behavior and super-admin flows.

## 2026-09-01 to 2026-10-02 — RBAC, permissions, and system-admin controls

### Added

- Super-admin registration flow and system permissions handling (`feat: Add super admin registration flow`, `feat: Handle super admin system permissions`)
- Permission APIs and uniqueness enforcement (`feat: Add permission APIs and enforce uniqueness`)
- Permission deletion flow (`feat: Add permission deletion flow`)
- Role summary endpoint with permissions (`feat: Add role summary endpoint with permissions`)
- Current-user port and forbidden handling (`feat: Add current-user port and forbidden handling`)
- Tenant auto-config and naming cleanup (`chore: Add tenant auto-config and naming cleanup`)
- File-based Logback logging and structured auth logging (`feat: Configure file-based Logback logging`, `feat: Add structured logging to auth services`)
- Swagger/OpenAPI integration (`feat: started swagger/openapi integration`)

### Changed

- Permission access was moved behind dedicated ports (`refactor: permission access into dedicated port`, `refactor: Route active permission lookup via permission port`)
- Role and permission queries were refactored to dedicated query services and ports (`refactor: permission queries to dedicated port`, `feat: Implement missing role and permission queries`)
- Permission models gained a `tenant_assignable` flag (`feat: Add tenant-assignable flag to permissions`)
- Role and permission controllers were reorganized and clarified (`chore: Rename admin auth controllers for clarity`, `refactor: reorganize auth controllers and extend DTO mapping`)

### Security and authorization

- Admin creation is protected by authorization checks (`feat: Protect admin creation with role checks`)
- Role and permission catalog flows were expanded for both platform and tenant scopes.
- The project now supports separate platform-level and tenant-level role/permission administration.

## 2026-08-29 to 2026-09-11 — Multi-tenancy hardening and tenant-scoped auth

### Added

- Tenant filter and context in the security chain (`feat: Add tenant filter and context to security chain`)
- Active tenant validation (`feat: Add active tenant validation`)
- Tenant module lookup endpoints and active module exposure (`feat: Add available tenant modules endpoint`, `feat: Add tenant module details endpoint`)
- Permission lookup by module key (`feat: Add permission lookup by module key`)
- Tenant entitlements read API and service ports (`feat: Add tenant entitlements read API and service port`)

### Fixed

- JWT user and tenant IDs are parsed as UUIDs (`fix: Parse JWT user and tenant IDs as UUIDs`)
- Public path handling in the tenant filter was corrected (`fix: tenant filter public path handling`)
- Tenant module lookup missing cases are handled safely (`fix: Handle missing tenant module lookup safely`)
- Basic authentication was disabled and JWT filter errors were handled more safely (`fix: Disable HTTP Basic and handle JWT filter errors`)

### Security improvements

- The security chain started enforcing tenant-scoped authentication and active-tenant checks.
- Login and admin creation flows were restricted and hardened based on tenant validity and authorization state.
- The project introduced stronger separation between tenant and system-level access.

## 2026-08-20 to 2026-08-21 — Hexagonal architecture refactor and security package cleanup

### Changed

- Auth and security packages were restructured to separate hexagonal concerns (`refactor(Hex): Restructure auth and security packages`)
- Common configuration classes were moved into `common.config` (`refactor(hex): Relocate config classes to common.config`)
- Exceptions were moved into the common package (`refactor(Hex): Move exceptions into common package`)
- Security utilities were relocated into `common.security` (`refactor(Hex): Move SecurityUtils to common.security package`)
- JWT utilities were moved into the common security module (`refactor(Hex): Move JWT utilities into common security module`)
- Refresh-token entity/repository support was added under the new hexagonal structure (`refactor(Hex): Add refresh token entity and JPA repository`)

### Impact

- This is a major architectural cleanup intended to improve modularity, separation of concerns, and long-term maintainability.
- It marked a clear move from a compact initial implementation toward a more structured application architecture.

## 2026-08-01 to 2026-08-10 — Auth flows, onboarding, and module subscription expansion

### Added

- JWT filter integration and authentication exception handling (`feat: Add AuthExceptionHandler and wire JWT filter`)
- Refresh-token repository and issuance flow (`feat: Add refresh token repository`, `feat: Implement persistent refresh token issuance`)
- Login endpoint and validated authentication flow (`feat: Add login API and enforce auth validations`)
- Tenant-aware registration flow (`feat: Add tenant-aware auth registration flow`)
- Logout endpoint with token revocation (`feat: Add logout endpoint with token revocation`)
- Refresh-token endpoint/cookie handling (`feat: Add refresh token endpoint and cookie handling`)
- Tenant admin APIs and status updates (`feat: Add versioned tenant admin APIs and status update`)
- Module creation with permission bootstrap (`feat: Add module creation with permission bootstrap`)
- Module subscriptions during signup (`feat: Add module subscriptions to tenant signup`, `feat: Wire module subscription into registration`)

### Changed

- Security utilities were hardened and user resolution logic was tightened (`refactor: Harden SecurityUtils user resolution`)
- Authentication manager configuration was simplified (`refactor: Simplify AuthenticationManager bean setup`)
- API and permission handling were updated to better fit tenant and admin flows (`refactor:  actor and permission module handling`)

### Security and authorization improvements

- The auth flow started to enforce stricter handling around admin creation, tenant validation, and permission assignment.
- Role-permission assignment helper and module permission bootstrapping were introduced to support tenant onboarding.

### Breaking / compatibility notes

- The project began moving toward a stronger UUID-based identity model; the commit history includes: `feat: Migrate auth, module, and tenant IDs to UUID` on 2026-08-29.
- This is a compatibility-relevant change for API consumers and persistence assumptions.

## 2026-07-27 to 2026-07-29 — Initial project foundation

### Added

- Initial repository scaffold and baseline application setup (`initial commit`)
- PostgreSQL database configuration (`feat: Add PostgreSQL database configuration`)
- Core tenant and role-based access-control domain entities (`feat: Add core tenant and RBAC entities`)
- Spring Security baseline and path-based access configuration (`feat: Add Spring Security and path-based access config`)
- Tenant controller, service, and repository layer (`feat: Add tenant controller, service, and repository`)
- Refresh-token persistence support (`feat: Add refresh token persistence`)

### Security and auth foundation

- JWT security scaffolding and authentication provider were introduced (`feat: Scaffold JWT security and auth provider`)
- JWT user principal handling was refactored (`feat: Refactor JWT user principal handling`)
- Tenant and refresh-token mapping fixes were applied during early auth work (`fix: tenant and refresh token mappings`)

### Database impact

- The initial schema established the first tenant, user, and authorization structures in the persistence layer.
- This is the first verifiable foundation for the current multi-tenant ERP design.

## Backward compatibility and breaking-change notes

This project has not reached a formal production release, but the history already shows a few areas with compatibility impact:

- UUID migration for auth, module, and tenant identifiers (2026-08-29)
- Tenant-scoped security filtering introduced after the initial auth setup (2026-08-29 onward)
- Role and permission access model was reorganized several times as the system-level and tenant-level RBAC APIs matured
- Endpoint and controller structure was reorganized between 2026-09 and 2026-10 to reflect the final permission, role, and module management design

## Database evolution summary

The SQL schema in `src/main/resources/db/sql/prodigo-schema.sql` is the clearest database-evolution artifact in the repository. It defines the following major entities and relationships:

- `tenants` and `tenant_entitlements`
- `modules` and `module_subscriptions`
- `users` and `refresh_tokens`
- `roles` and `user_roles`
- `permissions` and `role_permissions`

It also includes schema adjustments such as:

- `tenant_assignable` on permissions
- `UNIQUE` constraints on tenant/module and permission codes
- foreign keys enforcing tenant, module, role, and permission relationships

## Conclusion

The repository history shows a project that started as a Spring Boot + PostgreSQL starter with basic tenant/RBAC entities and evolved into a more mature multi-tenant ERP backend with JWT-based auth, tenant-aware security filters, module subscriptions, and a comprehensive role/permission catalog.

The project is not yet at a formal released version. Based on the available evidence, the repository is best treated as a development snapshot (`0.0.1-SNAPSHOT`) rather than a published release. The changelog above captures the meaningful milestones that are both verifiable in git history and reflected in the current implementation.
