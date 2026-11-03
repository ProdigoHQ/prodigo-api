# Prodigo API — Reference

> Full API documentation for **Prodigo API** (this repository's backend).
> Part of the Prodigo ERP documentation. See the [documentation index](./README.md)
> for setup, architecture and configuration guides.

Base URL: `http://localhost:8080`. Unless stated otherwise, paths below target
`version = 1.0`. Endpoints marked 🔓 are public; the rest require
`Authorization: Bearer <token>`.

---

## Table of contents

- [API conventions](#api-conventions)
- [Authentication](#authentication--api10auth)
- [Platform / Super Admin](#platform--super-admin--api10sadmin)
- [Tenant RBAC](#tenant-rbac--api10roles--api10permissions)
- [Modules](#modules--api10modules--api10tenantmodules)
- [Tenant & entitlements](#tenant--entitlements--api10tenant)
- [Modules & permission catalog](#modules--permission-catalog)

---

## API conventions

### Base path & versioning

- All business endpoints live under `/api/{version}/...`.
- Version is a **path segment** and defaults to `1.0` (`common/config/WebConfig.java`).
  Supported versions: `1.0`, `2.0`, `3.0` (currently only `1.0` is implemented).
- Consequently the effective base path today is `/api/1.0/`.

```http
GET    /api/1.0/products
GET    /api/1.0/products/{id}
POST   /api/1.0/products
PATCH  /api/1.0/products/{id}
DELETE /api/1.0/products/{id}
```

### Authentication header

Protected endpoints require:

```http
Authorization: Bearer <access_token>
```

Public endpoints: `/api/1.0/auth/register`, `/api/1.0/auth/login`,
`/api/1.0/auth/refresh`, `/api/1.0/auth/logout`, `/api/1.0/modules/public`, `/api-docs`,
`/swagger-ui/**`.

### Error format

Errors are produced by `common/exception/GlobalExceptionHandler.java`. Responses are
based on **RFC 7807-style** fields (with `traceId` and `errorCode`):

```json
{
  "timestamp": "2026-10-12T09:15:22.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "No Tenant Entitlement Found with the Tenant Id: ...",
  "path": "/api/1.0/tenant/entitlements",
  "errorCode": "RESOURCE_NOT_FOUND",
  "traceId": "b2f1c0e2-...-9a1d"
}
```

Validation errors add a `errors` array:

```json
{
  "timestamp": "2026-10-12T09:15:22.123Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields are invalid.",
  "path": "/api/1.0/auth/register",
  "errorCode": "VALIDATION_ERROR",
  "traceId": "b2f1c0e2-...-9a1d",
  "errors": [
    { "field": "email", "message": "must be a well-formed email address" }
  ]
}
```

Status → `errorCode` mapping:

| Status | errorCode              | Raised for                                        |
| -----: | ---------------------- | ------------------------------------------------- |
| 400    | `VALIDATION_ERROR`     | Bean validation failures                          |
| 400    | `BAD_REQUEST`          | `BadRequestException`; malformed body / invalid enum value |
| 401    | `INVALID_CREDENTIALS`  | Bad username/password                             |
| 401    | `UNAUTHORIZED`         | Invalid/expired JWT, missing refresh cookie       |
| 403    | `ACCESS_DENIED`        | `AccessDeniedException` (including tenant not `ACTIVE`) |
| 404    | `RESOURCE_NOT_FOUND`   | `NotFoundException`, unknown user/resource        |
| 409    | `CONFLICT`             | Duplicate username / email / slug / role / module |
| 500    | `INTERNAL_SERVER_ERROR`| Unexpected error                                  |

> ⚠️ `ForbiddenException` is declared on the 403 handler, but the handler method parameter
> is typed `AccessDeniedException`. A thrown `ForbiddenException` can therefore fall
> through to the generic 500 handler. See [Known gaps](./ROADMAP.md#known-gaps).

---

## Authentication — `/api/1.0/auth`

| Method | Path       | Access | Description                                         |
| ------ | ---------- | ------ | --------------------------------------------------- |
| POST   | `/register`| 🔓     | Register a company + owner user; returns tokens + refresh cookie |
| POST   | `/login`   | 🔓     | Authenticate; returns access token + refresh cookie  |
| GET    | `/refresh` | 🔓(cookie) | Rotate refresh token; returns new access token   |
| GET    | `/logout`  | 🔓(cookie) | Revoke refresh token, clear cookie               |

**POST `/register`** request body:

```json
{
  "companyName": "Acme SARL",
  "companySlug": "acme",
  "country": "CM",
  "username": "owner",
  "email": "owner@acme.cm",
  "password": "S3cret!pass",
  "firstName": "Awa",
  "lastName": "Ngu",
  "selectedModules": [
    { "moduleId": null, "moduleName": "Inventory", "moduleKey": "INVENTORY" }
  ]
}
```

What it does, transactionally: create tenant → create default entitlements → create
module subscriptions (first selected module is free, others get a 30-day window) →
create owner user → create `OWNER` / `ADMIN` / `MEMBER` tenant roles → assign OWNER →
issue tokens. The `V7` trigger grants each new tenant role its default `TENANT.*`
permissions; module permissions are linked for the modules selected at signup.

`200` response: the endpoint returns the same body shape as login
(access token in the body, refresh token in the `Set-Cookie` header) and the registering
user's roles/permissions.

```json
{
  "userId": "0f4d…",
  "tenantSlug": "acme",
  "accessToken": "eyJhbGciOi…",
  "roles": ["OWNER"],
  "permissions": ["TENANT.USER.READ", "INVENTORY.MODULE.CRUD", "…"]
}
```

**POST `/login`** request body:

```json
{ "username": "owner", "password": "S3cret!pass" }
```

`200` response (access token in the body, refresh token in the `Set-Cookie` header):

```json
{
  "userId": "0f4d…",
  "tenantSlug": "acme",
  "accessToken": "eyJhbGciOi…",
  "roles": ["OWNER"],
  "permissions": ["TENANT.USER.READ", "INVENTORY.MODULE.CRUD", "…"]
}
```

Login rejects users whose tenant is not `ACTIVE` with `403 ACCESS_DENIED`. Platform users
have no tenant and bypass that check.

`GET /refresh` returns `{ "accessToken": "…" }` and sets a new `refresh_token` cookie.
The cookie uses `HttpOnly`, `Secure`, `SameSite=Strict`, `Path=/` and a 14-day `Max-Age`.
The persisted refresh token itself expires according to
`security.jwt.refresh-expiration` (7 days in the checked-in configuration).

---

## Platform / Super Admin — `/api/1.0/s/admin`

Reserved for platform operators. Platform users have no tenant (`tenantId` is null) and
`/api/1.0/s/admin/**` is excluded from `TenantFilter`. Controllers are guarded with
permission authorities such as `@PreAuthorize("hasAuthority('PERM_ERP.ADMIN.READ')")`.

**Platform admins** (`PlatformAdminController`):

| Method | Path          | Authority                                                   | Description                              |
| ------ | ------------- | ----------------------------------------------------------- | ---------------------------------------- |
| POST   | `/create`     | `PERM_ERP.ADMIN.CREATE` or `PERM_ERP.ADMIN.UPDATE`          | Create a platform admin                  |
| GET    | `/`           | `PERM_ERP.ADMIN.READ`                                       | List platform admins                     |
| GET    | `/{adminId}`  | `PERM_ERP.ADMIN.READ`                                       | Get a platform admin with roles/permissions |
| PUT    | `/{adminId}`  | `PERM_ERP.ADMIN.CREATE` or `PERM_ERP.ADMIN.UPDATE`          | Update a platform admin's name fields    |

`POST /create` request body (`roleId` must identify a system role, i.e. a role with a
null tenant):

```json
{
  "username": "platformadmin",
  "email": "platformadmin@example.com",
  "password": "S3cret!pass",
  "firstName": "Awa",
  "lastName": "Ngu",
  "roleId": "<system-role-uuid>"
}
```

`POST /create` returns `201 Created` with a plain-text confirmation. `PUT /{adminId}`
accepts only names:

```json
{ "firstName": "Awa", "lastName": "Ngu" }
```

`GET /{adminId}` returns the admin plus assigned roles and permissions:

```json
{
  "id": "<user-uuid>",
  "username": "platformadmin",
  "email": "platformadmin@example.com",
  "firstName": "Awa",
  "lastName": "Ngu",
  "status": "ACTIVE",
  "lastLogin": "2026-11-03T09:15:22Z",
  "createdAt": "2026-10-12T09:15:22Z",
  "createBy": "PRODIGO_ERP_API",
  "roles": [
    { "id": "<role-uuid>", "name": "SUPER_ADMIN", "description": "Platform super administrator", "createdAt": "2026-10-12T09:00:00Z" }
  ],
  "permissions": [
    { "id": "<permission-uuid>", "code": "ERP.ADMIN.READ", "description": "View platform administrators", "action": "READ", "resource": "ADMIN" }
  ]
}
```

**Modules** (`AdminModuleController`):

| Method | Path        | Authority* | Description                                      |
| ------ | ----------- | ---------- | ------------------------------------------------ |
| POST   | `/modules/` | none       | Register a module + its permissions; returns a plain-text `200` |

\* `AdminModuleController` does not declare an authority guard yet; the request still
requires a valid JWT because `/api/1.0/**` is authenticated by path security.

`POST /modules/` body:

```json
{
  "name": "CRM",
  "description": "Customer relationship management",
  "moduleKey": "CRM",
  "price": 0,
  "createPermissions": [
    { "description": "View customers", "moduleKey": "CRM", "action": "READ", "resource": "CUSTOMER" }
  ]
}
```

**Permissions** (`PlatformPermissionController`):

| Method | Path                          | Authority                                                   | Description               |
| ------ | ----------------------------- | ----------------------------------------------------------- | ------------------------- |
| GET    | `/permissions`                | `PERM_ERP.PERMISSION.READ`                                   | List all permissions      |
| GET    | `/permissions/{permissionId}` | `PERM_ERP.PERMISSION.READ`                                   | Get a permission (by UUID)|
| POST   | `/permissions`                | `PERM_ERP.PERMISSION.CREATE` or `PERM_ERP.PERMISSION.UPDATE` | Create a permission       |
| PUT    | `/permissions/{permissionId}` | `PERM_ERP.PERMISSION.CREATE` or `PERM_ERP.PERMISSION.UPDATE` | Update a permission       |
| DELETE | `/permissions/{permissionId}` | `PERM_ERP.PERMISSION.DELETE`                                 | Delete a permission       |

**Roles** (`PlatformRoleController`):

| Method | Path                                        | Authority*                          | Description                         |
| ------ | ------------------------------------------- | ----------------------------------- | ----------------------------------- |
| GET    | `/roles`                                    | class-level guard                   | List system roles                   |
| GET    | `/roles/{roleId}`                           | class-level guard                   | Get a system role                   |
| POST   | `/roles`                                    | `PERM_ERP.ROLE.CREATE` or `...UPDATE` | Create a system role              |
| PUT    | `/roles/{roleId}`                           | `PERM_ERP.ROLE.CREATE` or `...UPDATE` | Update a system role              |
| DELETE | `/roles/{roleId}`                           | `PERM_ERP.ROLE.DELETE`              | Delete a system role                |
| GET    | `/tenants/{tenantId}/roles`                 | `PERM_ERP.USER.READ`                | Read-only: list a tenant's roles    |
| GET    | `/tenants/{tenantId}/roles/{roleId}`        | `PERM_ERP.USER.READ`                | Read-only: get a tenant role        |

\* The class-level guard is
`@PreAuthorize("hasRole('PERM_ERP.ROLE.READ')")`. `hasRole` adds the `ROLE_` prefix, so
this currently checks `ROLE_PERM_ERP.ROLE.READ` instead of the intended
`PERM_ERP.ROLE.READ` authority; see [Known gaps](./ROADMAP.md#known-gaps).

**Role permissions** (`PlatformRolePermissionController`):

| Method | Path                                       | Authority*                                                       | Description                  |
| ------ | ------------------------------------------ | ---------------------------------------------------------------- | ---------------------------- |
| GET    | `/roles/{roleId}/permissions`              | `PERM_ERP.PERMISSION.ASSIGN` or `PERM_ERP.PERMISSION.REVOKE`     | List permissions of a role   |
| POST   | `/roles/{roleId}/permissions/{permissionId}` | same class-level guard                                         | Assign permission to a role  |
| DELETE | `/roles/{roleId}/permissions/{permissionId}` | same class-level guard                                         | Revoke permission from role  |

\* The class-level guard applies to all three operations.

---

## Tenant RBAC — `/api/1.0/roles` & `/api/1.0/permissions`

Tenant-scoped role and permission management. Callers need the corresponding `TENANT.*`
authorities seeded in `V6`; default tenant roles receive them from the `V7` trigger.

**Roles** (`TenantRoleController`):

| Method | Path              | Authority                                                            | Description          |
| ------ | ----------------- | -------------------------------------------------------------------- | -------------------- |
| GET    | `/roles`          | `PERM_TENANT.ROLE.READ`                                              | List tenant roles    |
| GET    | `/roles/{roleId}` | `PERM_TENANT.ROLE.READ`                                              | Get a tenant role    |
| POST   | `/roles`          | `PERM_TENANT.ROLE.CREATE` or `PERM_TENANT.ROLE.UPDATE`               | Create a tenant role |
| PUT    | `/roles/{roleId}` | `PERM_TENANT.ROLE.CREATE` or `PERM_TENANT.ROLE.UPDATE`               | Update a tenant role |
| DELETE | `/roles/{roleId}` | `PERM_TENANT.ROLE.DELETE`                                            | Delete a tenant role |

**Role permissions** (`TenantRolePermissionController`):

| Method | Path                                         | Authority*                                                  | Description                 |
| ------ | -------------------------------------------- | ----------------------------------------------------------- | --------------------------- |
| GET    | `/roles/{rolesId}/permissions`               | `PERM_TENANT.ROLE.READ_ASSIGNMENTS`                         | List a role's permissions   |
| POST   | `/roles/{rolesId}/permissions/{permissionId}`| `PERM_TENANT.ROLE.ASSIGN` or `PERM_TENANT.ROLE.REVOKE`      | Assign permission           |
| DELETE | `/roles/{rolesId}/permissions/{permissionId}`| `PERM_TENANT.ROLE.ASSIGN` or `PERM_TENANT.ROLE.REVOKE`      | Revoke permission           |

> ⚠️ The route template is `{rolesId}` while the handler binds `roleId`; these endpoints
> currently fail at runtime. See [Known gaps](./ROADMAP.md#known-gaps).

**Permissions** (`TenantPermissionController`):

| Method | Path                          | Authority                       | Description                  |
| ------ | ----------------------------- | ------------------------------- | ---------------------------- |
| GET    | `/permissions`                | `PERM_TENANT.PERMISSION.READ`   | List tenant-assignable permissions |
| GET    | `/permissions/{permissionId}` | `PERM_TENANT.PERMISSION.READ`   | Get a tenant-assignable permission |

Payloads:

```jsonc
// Create/update role
{ "name": "Sales Manager", "description": "Manages quotes and orders" }

// Permission summary (response)
{ "id": "<uuid>", "code": "INVENTORY.PRODUCT.READ", "description": "View inventory products",
  "action": "READ", "resource": "PRODUCT" }
```

---

## Modules — `/api/1.0/modules` & `/api/1.0/tenant/modules`

**Public catalog** (`ModuleController`):

| Method | Path       | Access | Description                       |
| ------ | ---------- | ------ | --------------------------------- |
| GET    | `/public`  | 🔓     | List publicly available modules   |

**Tenant modules** (`TenantModuleController`, tenant derived from the token):

| Method | Path                     | Description                                            |
| ------ | ------------------------ | ------------------------------------------------------ |
| GET    | `/tenant/modules/`       | Modules the current tenant is subscribed to            |
| GET    | `/tenant/modules/available` | Modules the tenant does not yet own                 |
| GET    | `/tenant/modules/{moduleKey}` | Module details + the permissions it owns         |

Sample `GET /tenant/modules/{moduleKey}` response:

```json
{
  "id": "<uuid>",
  "name": "Inventory",
  "description": "Inventory management",
  "moduleKey": "INVENTORY",
  "price": 0,
  "currency": "XAF",
  "isActive": true,
  "permissions": [
    { "id": "<uuid>", "code": "INVENTORY.PRODUCT.READ", "action": "READ", "resource": "PRODUCT" }
  ]
}
```

---

## Tenant & entitlements — `/api/1.0/tenant`

| Method | Path                  | Description                                            |
| ------ | --------------------- | ------------------------------------------------------ |
| GET    | `/tenant/entitlements`| Current tenant's limits (max users, storage, products) |

Sample response:

```json
{
  "id": "<entitlement-uuid>",
  "maxUsers": 5,
  "maxStorageGb": 5,
  "maxProducts": 20,
  "createdAt": "2026-10-12T09:00:00Z"
}
```

---

## Modules & permission catalog

Modules are seeded (`V3`) and each owns its permissions. Module permission codes are
built as `<MODULE_KEY>.<RESOURCE>.<ACTION>`.

| Module key  | Name           | Example permissions                                                                 |
| ----------- | -------------- | ----------------------------------------------------------------------------------- |
| `HR`        | Human Resources| `HR.EMPLOYEE.*`, `HR.DEPARTMENT.*`, `HR.POSITION.*`, `HR.LEAVE.*`, `HR.ATTENDANCE.*`, `HR.PAYROLL.*`, `HR.MODULE.CRUD` |
| `INVENTORY` | Inventory      | `INVENTORY.PRODUCT.*`, `INVENTORY.CATEGORY.*`, `INVENTORY.WAREHOUSE.*`, `INVENTORY.STOCK.READ/ADJUST`, `INVENTORY.STOCK_MOVEMENT.*`, `INVENTORY.SUPPLIER.*`, `INVENTORY.MODULE.CRUD` |

Platform (`ERP.*`) permissions seeded in `V2` and `V6` cover `ADMIN`, `USER`, `ROLE`,
`PERMISSION`, `SYSTEM`, `TENANT`, `MODULE`, `SUBSCRIPTION`, `ENTITLEMENT`, `BILLING`,
`PAYMENT`, `INVOICE`, plus `AUTH`, `SESSION`, `INVITATION`, `API_KEY`, `SECURITY` and
`AUDIT`.

Tenant-scoped (`TENANT.*`) permissions seeded in `V6` cover authentication/account
lifecycle, profile, users, roles, permissions, policies, sessions, audit, security and
company settings. They are tenant-assignable, and the `V7` trigger grants defaults to new
tenant roles:

- `OWNER` — all `TENANT.*` permissions.
- `ADMIN` — all `TENANT.*` permissions except company-level settings updates.
- `MEMBER` — end-user subset (auth/profile/session-own, company read, role/permission read).

Roles:

- **System:** `SUPER_ADMIN` (all `ERP.*`), `PLATFORM_ADMIN` (all `ERP.*` except
  destructive/maintenance operations) and `PLATFORM_AUDITOR` (read-only).
- **Per tenant (created at signup):** `OWNER`, `ADMIN`, `MEMBER`.
- System roles can be deleted only while unassigned; tenant default roles cannot be
  deleted. The `<MODULE_KEY>.MODULE.CRUD` permission is additionally linked to
  `OWNER`/`ADMIN` for the modules the tenant subscribed to.

Future ERP modules (planned): `CRM`, `SALES`, `INVOICING`, `PAYMENTS`, `POS`,
`PROJECTS`, `ECOMMERCE`, `REPORTING`.

---

Back to the [documentation index](./README.md).
