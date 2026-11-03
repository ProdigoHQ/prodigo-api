# Getting Started

> Local setup and first API calls for **Prodigo API**.
> See the [documentation index](./README.md) for the other guides.

## Prerequisites

- **JDK 26**
- **PostgreSQL** (running locally or via Docker)
- Maven is not required — use the wrapper (`./mvnw`)

## 1. Create the database

```bash
createdb prodigodb
```

## 2. Configure the connection

Edit `src/main/resources/application.properties`, or override with environment variables
or `--spring.datasource.*`:

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

## 3. Run

```bash
./mvnw spring-boot:run
```

Flyway applies the schema and seeds on startup (`spring.flyway.enabled=true`). The API
starts on `http://localhost:8080`.

## 4. API documentation

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Written reference: [`API.md`](./API.md)

## 5. Smoke test

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

curl http://localhost:8080/api/1.0/tenant/modules/ \
  -H "Authorization: Bearer $TOKEN"
```

## Configuration reference

Key properties in `src/main/resources/application.properties`:

| Property                          | Purpose                                          |
| --------------------------------- | ------------------------------------------------ |
| `spring.datasource.*`             | PostgreSQL connection                            |
| `spring.jpa.hibernate.ddl-auto`   | `validate` — schema is owned by Flyway           |
| `spring.flyway.enabled`           | Enables migrations                               |
| `spring.flyway.locations`         | `db/migration`                                   |
| `security.jwt.secret`             | HMAC signing key                                 |
| `security.jwt.expiration`         | Access token TTL (ms)                            |
| `security.jwt.refresh-expiration` | Refresh token TTL (ms)                           |
| `springdoc.api-docs.path`         | OpenAPI JSON path (`/api-docs`)                  |
| `logging.file.path`               | Log file location                                |

Logging is configured in `src/main/resources/logback-spring.xml`; the active file path is
hard-coded there (`./logs/api-dev.log`), with `logging.file.path` set to the same value in
`application.properties`.

## Next steps

- Understand the code organization in [`ARCHITECTURE.md`](./ARCHITECTURE.md).
- Review authentication and tenant isolation in [`SECURITY.md`](./SECURITY.md).
- Inspect the schema and migrations in [`DATABASE.md`](./DATABASE.md).
