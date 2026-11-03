# Prodigo ERP — API

> Multi-tenant SaaS ERP backend for the **Prodigo ecosystem**.

**Prodigo API** (this repository) is the backend and business engine of the Prodigo
ecosystem. It owns authentication, identity, multi-tenancy, RBAC, module subscriptions
and the ERP domain modules, and exposes the REST/OpenAPI contract consumed by
`prodigo-web` and `prodigo-os`.

- **Status:** pre-1.0 development snapshot (`0.0.1-SNAPSHOT`)
- **Stack:** Java 26 · Spring Boot 4.1 · PostgreSQL · Flyway
- **Run:** `./mvnw spring-boot:run` (configure the database first)

## Documentation

Detailed documentation lives under [`docs/`](./docs/README.md):

| Document                                               | Contents                                                |
| ------------------------------------------------------ | ------------------------------------------------------- |
| [`docs/README.md`](./docs/README.md)                   | Documentation index and suggested reading order         |
| [`docs/GETTING_STARTED.md`](./docs/GETTING_STARTED.md) | Prerequisites, configuration, run and smoke test        |
| [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md)       | System context, hexagonal architecture and packages     |
| [`docs/SECURITY.md`](./docs/SECURITY.md)               | JWT/refresh tokens, multi-tenancy and RBAC              |
| [`docs/API.md`](./docs/API.md)                         | Full REST API reference                                 |
| [`docs/DATABASE.md`](./docs/DATABASE.md)               | Flyway migrations, schema and seed data                 |
| [`docs/TESTING.md`](./docs/TESTING.md)                 | Test stack and current test coverage                    |
| [`docs/ROADMAP.md`](./docs/ROADMAP.md)                 | Known gaps and next steps                               |
| [`CHANGELOG.md`](./CHANGELOG.md)                       | Development history                                     |

## License

See [`LICENSE`](./LICENSE).
