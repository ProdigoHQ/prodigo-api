# Security & Multi-tenancy

> Authentication, tenant isolation and authorization for **Prodigo API**.
> See the [documentation index](./README.md) for the other guides.

## Request pipeline

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

The filters are registered in `common/config/SecurityConfig.java`. Public paths are
defined in `common/config/PathConfig.java`:

- `/api/1.0/auth/register`
- `/api/1.0/auth/login`
- `/api/1.0/auth/refresh`
- `/api/1.0/auth/logout`
- `/api/1.0/modules/public`
- `/api-docs`
- `/swagger-ui/**`

All other paths under `/api/1.0/**` require authentication; anything else is denied.

## JWT & refresh tokens

- Access tokens are JWTs signed with HMAC (configurable secret), default TTL **15 min**.
- Claims include `userId`, `username`, `email`, `tenantId`, `tenantSlug`, `roles` and
  `permissions`. Role claims become `ROLE_*` authorities; permission claims become
  `PERM_*` authorities.
- Refresh tokens are persisted in the `refresh_tokens` table, sent as an **HTTP-only,
  SameSite=Strict cookie** named `refresh_token` with a 14-day cookie lifetime, and are
  **rotated** on every `/auth/refresh`.
- The server-side refresh-token lifetime is controlled by
  `security.jwt.refresh-expiration` (7 days in the checked-in configuration).
- Logout revokes the refresh token and clears the cookie + security context.
- Login checks that the user's tenant is `ACTIVE`; platform users have no tenant and
  bypass that check.

## Multi-tenancy

**Strategy: shared schema + row-level tenant scoping.**

- `TenantContext` is a `ThreadLocal<UUID>` holding the current tenant for the request.
- `TenantFilter` populates it from the authenticated principal and clears it in a
  `finally` block.
- It skips public paths and `/api/1.0/s/admin/**`, where platform operators have no
  tenant.
- Tenant-scoped use cases resolve data using `TenantContext.getCurrentTenant()`.
- Users may have a null tenant (platform admins).

Planned hardening: Hibernate tenant filter and PostgreSQL RLS policies. See
[`ROADMAP.md`](./ROADMAP.md).

## RBAC & permission naming

`User → Roles → Permissions`. Three namespaces exist:

```text
# Platform / Super Admin permissions
ERP.<RESOURCE>.<ACTION>
# e.g. ERP.SYSTEM.ACCESS, ERP.TENANT.READ, ERP.MODULE.UPDATE, ERP.SUBSCRIPTION.SUSPEND

# Module permissions
<MODULE_KEY>.<RESOURCE>.<ACTION>
# e.g. INVENTORY.PRODUCT.CREATE, HR.EMPLOYEE.READ, INVENTORY.STOCK.ADJUST

# Tenant-scoped permissions
TENANT.<RESOURCE>.<ACTION>
# e.g. TENANT.USER.READ, TENANT.ROLE.CREATE, TENANT.PERMISSION.READ
```

Authorities in the security context are prefixed at load time:

- Roles → `ROLE_<NAME>` → `hasRole('SUPER_ADMIN')`
- Permissions → `PERM_<code>` → `hasAuthority('PERM_ERP.ADMIN.CREATE')`

> The permission naming convention (`ERP.*`, `<module>.<resource>.<action>` and
> `TENANT.*`) is recorded as decision **M0-03**. Seeded module permission codes use the
> **uppercase module key** (`INVENTORY.PRODUCT.READ`), not the lowercase form shown in
> early design notes.

Migration `V6` seeds the `TENANT.*` catalog, and the `V7` trigger grants default
`TENANT.*` permissions whenever a tenant role is created. The catalog and default role
grants are documented in [`API.md`](./API.md#modules--permission-catalog) and
[`DATABASE.md`](./DATABASE.md).

## Current guard gaps

Some authorization rules are still incomplete. The concrete issues are tracked in
[`ROADMAP.md`](./ROADMAP.md#known-gaps):

- `TenantRolePermissionController` has a path-variable mismatch.
- `PlatformRoleController` uses `hasRole` with a permission-style authority.
- `AdminModuleController` has no permission guard.
