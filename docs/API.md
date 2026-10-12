# Prodigo API — Reference

> Full API documentation for **Prodigo API** (this repository's backend).
> See the project [`README`](../README.md) for setup, architecture and configuration.

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

Public endpoints: `/api/1.0/auth/register`, `/auth/login`, `/auth/refresh`,
`/auth/logout`, `/api/1.0/modules/public`, `/api-docs`, `/swagger-ui/**`.

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
| 400    | `BAD_REQUEST`          | `BadRequestException`                             |
| 401    | `INVALID_CREDENTIALS`  | Bad username/password                             |
| 401    | `UNAUTHORIZED`         | Invalid/expired JWT, missing refresh cookie       |
| 403    | `ACCESS_DENIED`        | `AccessDeniedException`, `ForbiddenException`     |
| 404    | `RESOURCE_NOT_FOUND`   | `NotFoundException`, unknown user/resource        |
| 409    | `CONFLICT`             | Duplicate username / email / slug                 |
| 500    | `INTERNAL_SERVER_ERROR`| Unexpected error                                  |

---

## Authentication — `/api/1.0/auth`

| Method | Path       | Access | Description                                         |
| ------ | ---------- | ------ | --------------------------------------------------- |
| POST   | `/register`| 🔓     | Register a company + owner user (tenant onboarding)  |
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
issue tokens.

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
  "permissions": ["INVENTORY.PRODUCT.CREATE", "…"]
}
```

`GET /refresh` returns `{ "accessToken": "…" }` and sets a new `refresh_token` cookie.

---

## Platform / Super Admin — `/api/1.0/s/admin`

Reserved for platform operators. Controllers are guarded with
`@PreAuthorize("hasRole('SUPER_ADMIN')")`.

**Super admins** (`SuperAdminController`):

| Method | Path       | Authority            | Description               |
| ------ | ---------- | -------------------- | ------------------------- |
| POST   | `/create`  | `ERP.ADMIN.CREATE`   | Create a platform admin   |

**Modules** (`AdminModuleController`):

| Method | Path | Authority* | Description                              |
| ------ | ---- | ---------- | ---------------------------------------- |
| POST   | `/modules/` | `ERP.MODULE.CREATE` (planned) | Register a module + its permissions |

**Permissions** (`PlatformPermissionController`):

| Method | Path                          | Description               |
| ------ | ----------------------------- | ------------------------- |
| GET    | `/permissions`                | List all permissions      |
| GET    | `/permissions/{permissionId}` | Get a permission (by UUID)|
| POST   | `/permissions`                | Create a permission       |
| PUT    | `/permissions/{permissionId}` | Update a permission       |
| DELETE | `/permissions/{permissionId}` | Delete a permission       |

**Roles** (`PlatformRoleController`):

| Method | Path                                        | Description                         |
| ------ | ------------------------------------------- | ----------------------------------- |
| GET    | `/roles`                                    | List system roles                   |
| GET    | `/roles/{roleId}`                           | Get a system role                   |
| POST   | `/roles`                                    | Create a system role                |
| PUT    | `/roles/{roleId}`                           | Update a system role                |
| DELETE | `/roles/{roleId}`                           | Delete a system role                |
| GET    | `/tenants/{tenantId}/roles`                 | Read-only: list a tenant's roles    |
| GET    | `/tenants/{tenantId}/roles/{roleId}`        | Read-only: get a tenant role        |

**Role permissions** (`PlatformRolePermissionController`):

| Method | Path                                       | Description                  |
| ------ | ------------------------------------------ | ---------------------------- |
| GET    | `/roles/{roleId}/permissions`              | List permissions of a role   |
| POST   | `/roles/{roleId}/permissions/{permissionId}` | Assign permission to a role |
| DELETE | `/roles/{roleId}/permissions/{permissionId}` | Revoke permission from role |

\* `AdminModuleController` does not yet declare an authority guard.

---

## Tenant RBAC — `/api/1.0/roles` & `/api/1.0/permissions`

Tenant-scoped role and permission management (currently unguarded; `@PreAuthorize`
annotations are present but commented out pending the permission catalog rollout).

**Roles** (`TenantRoleController`):

| Method | Path              | Description         |
| ------ | ----------------- | ------------------- |
| GET    | `/roles`          | List tenant roles   |
| GET    | `/roles/{roleId}` | Get a tenant role   |
| POST   | `/roles`          | Create a tenant role|
| PUT    | `/roles/{roleId}` | Update a tenant role|
| DELETE | `/roles/{roleId}` | Delete a tenant role|

**Role permissions** (`TenantRolePermissionController`):

| Method | Path                                       | Description                 |
| ------ | ------------------------------------------ | --------------------------- |
| GET    | `/roles/{roleId}/permissions`              | List a role's permissions   |
| POST   | `/roles/{roleId}/permissions/{permissionId}` | Assign permission          |
| DELETE | `/roles/{roleId}/permissions/{permissionId}` | Revoke permission          |

> ⚠️ The route template is `{rolesId}` while the handler binds `roleId`; see
> [Known gaps](../README.md#known-gaps--next-steps).

**Permissions** (`TenantPermissionController`):

| Method | Path                          | Description                  |
| ------ | ----------------------------- | ---------------------------- |
| GET    | `/permissions`                | List permissions             |
| GET    | `/permissions/{permissionId}` | Get a permission by UUID     |

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
| GET    | `/tenant/modules`        | Modules the current tenant is subscribed to            |
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

Platform (`ERP.*`) permissions seeded in `V2` cover `ADMIN`, `USER`, `ROLE`,
`PERMISSION`, `SYSTEM`, `TENANT`, `MODULE`, `SUBSCRIPTION`, `ENTITLEMENT`, `BILLING`,
`PAYMENT`, `INVOICE`.

Roles:

- **System:** `SUPER_ADMIN` (grants all `ERP.*`).
- **Per tenant (created at signup):** `OWNER`, `ADMIN`, `MEMBER`.
- Planned standard roles also include `Member` and `Admin`; system roles cannot be deleted.

Future ERP modules (planned): `CRM`, `SALES`, `INVOICING`, `PAYMENTS`, `POS`,
`PROJECTS`, `ECOMMERCE`, `REPORTING`.

---

Back to the [`README`](../README.md).
