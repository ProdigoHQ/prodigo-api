# Database & Migrations

> Schema management, seed data and modeling choices for **Prodigo API**.
> See the [documentation index](./README.md) for the other guides.

## Flyway owns the schema

Schema changes are managed exclusively by **Flyway**. The application runs with
`spring.jpa.hibernate.ddl-auto=validate`, so the JPA entities must match the migrations.

Migrations live in `src/main/resources/db/migration`.

| Version | File                                                | Content                                                                 |
| ------- | --------------------------------------------------- | ----------------------------------------------------------------------- |
| V1      | `V1__create_schema.sql`                             | Core tables + indexes/constraints                                       |
| V2      | `V2__seed_system_permissions.sql`                   | `ERP.*` platform permissions                                            |
| V3      | `V3__seed_modules_and_module_permissions.sql`       | Modules (`HR`, `INVENTORY`) + their permissions                         |
| V4      | `V4__seed_system_roles.sql`                         | System roles `SUPER_ADMIN`, `PLATFORM_ADMIN`, `PLATFORM_AUDITOR`        |
| V5      | `V5__seed_role_permissions.sql`                     | Grants all `ERP.*` permissions to `SUPER_ADMIN`                         |
| V6      | `V6__seed_system_and_tenant_auth_permissions.sql`   | `ERP.*` auth/session/invitation/API-key/security permissions + `TENANT.*` permissions |
| V7      | `V7__seed_default_role_permissions.sql`             | Grants permissions to platform roles; trigger grants default `TENANT.*` permissions to new `OWNER`/`ADMIN`/`MEMBER` roles |
| V8      | `V8__make_user_last_login_nullable.sql`             | Allows users that have never logged in to have `NULL` `last_login`      |
| V9      | `V9__add_status_check_constraints.sql`              | Normalizes and constrains tenant, user and subscription statuses         |

A reference, hand-maintained version of the schema also lives at
`src/main/resources/db/sql/prodigo-schema.sql`.

## Tables

```text
tenants                  tenant_entitlements
modules                  module_subscriptions
users                    refresh_tokens
roles                    user_roles
permissions              role_permissions
```

## Seed data

- `V2` / `V6` — platform (`ERP.*`) permissions.
- `V3` — seeded modules `HR` and `INVENTORY`, plus their module permissions.
- `V4` — system roles `SUPER_ADMIN`, `PLATFORM_ADMIN`, `PLATFORM_AUDITOR`.
- `V5` / `V7` — grants to platform roles:
  - `SUPER_ADMIN` receives all `ERP.*` permissions.
  - `PLATFORM_ADMIN` receives all `ERP.*` except destructive/maintenance operations.
  - `PLATFORM_AUDITOR` receives read-only platform permissions.
- `V6` — tenant-scoped (`TENANT.*`) permissions for auth, profile, users, roles,
  permissions, policies, sessions, audit, security and company settings.
- `V7` — trigger `trg_seed_tenant_default_role_permissions` grants default `TENANT.*`
  permissions when a new tenant role (`OWNER` / `ADMIN` / `MEMBER`) is inserted.

The full permission catalog and default role grants are documented in
[`API.md`](./API.md#modules--permission-catalog).

## Status values

Status columns are persisted as enum names (`EnumType.STRING`) and constrained by `V9`:

| Table                 | Allowed values                                                            |
| --------------------- | ------------------------------------------------------------------------- |
| `tenants.status`      | `PENDING`, `ACTIVE`, `SUSPENDED`, `DEACTIVATED`                            |
| `users.status`        | `PENDING`, `ACTIVE`, `SUSPENDED`, `DEACTIVATED`, `LOCKED`                  |
| `module_subscriptions.status` | `PENDING`, `TRIALING`, `ACTIVE`, `PAST_DUE`, `SUSPENDED`, `CANCELLED`, `EXPIRED` |

## Notable modeling choices

- All identifiers are **UUID**: application entities use Hibernate `@UuidGenerator`, and
  seed migrations use PostgreSQL `gen_random_uuid()` (`pgcrypto`).
- Auditing columns on core tables: `created_at`, `updated_at`, `created_by`, `updated_by`
  (`refresh_tokens` only has `created_at`).
- `roles.tenant_id IS NULL` → **system role**; otherwise tenant-scoped.
- `permissions.tenant_assignable` distinguishes platform vs tenant-grantable permissions.
- Partial unique indexes enforce role-name uniqueness per system / per tenant.
- `module_subscriptions` has `UNIQUE(tenant_id, module_id)` and `status`, `expires_at`.
- `users.last_login` is nullable (`V8`) so never-logged-in users are represented correctly.
- Module permissions (`<MODULE_KEY>.MODULE.CRUD`) are linked by access provisioning for
  the modules a tenant actually subscribes to.
