# Prodigo ERP — Documentation

This directory contains the detailed project documentation. The root
[`README`](../README.md) is intentionally a short project summary.

## Documents

| Document                                     | Contents                                                              |
| -------------------------------------------- | --------------------------------------------------------------------- |
| [`GETTING_STARTED.md`](./GETTING_STARTED.md) | Prerequisites, database setup, configuration, running and smoke test  |
| [`ARCHITECTURE.md`](./ARCHITECTURE.md)       | Ecosystem context, modular monolith, hexagonal layers and packages    |
| [`SECURITY.md`](./SECURITY.md)               | Request pipeline, JWT/refresh tokens, multi-tenancy and RBAC          |
| [`API.md`](./API.md)                         | Complete REST API reference with authorities and payload examples     |
| [`DATABASE.md`](./DATABASE.md)               | Flyway migrations, tables, seed data and modeling choices             |
| [`TESTING.md`](./TESTING.md)                 | Test stack, current test suite and planned coverage                   |
| [`ROADMAP.md`](./ROADMAP.md)                 | Known gaps, hardening items and planned modules                       |

## Suggested reading order

1. [`GETTING_STARTED.md`](./GETTING_STARTED.md) — run the API locally.
2. [`ARCHITECTURE.md`](./ARCHITECTURE.md) — understand the code organization.
3. [`SECURITY.md`](./SECURITY.md) — understand auth, tenants and permissions.
4. [`API.md`](./API.md) — call the endpoints.
5. [`DATABASE.md`](./DATABASE.md) — understand migrations and seed data.
6. [`TESTING.md`](./TESTING.md) — run and extend the test suite.
7. [`ROADMAP.md`](./ROADMAP.md) — check what is still missing.

Development history is available in [`../CHANGELOG.md`](../CHANGELOG.md). The root
[`README`](../README.md) links back to all of the above.
