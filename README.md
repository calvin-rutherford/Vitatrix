# Vitatrix

A Spring Boot backend prototype for nursing notes and shift handoffs. This is a
local portfolio demo with synthetic patients and notes seeded by `DataSeeder` in
the `dev` profile. It has not been validated for clinical use; use synthetic data
only.

## Implemented behavior

- Stateless JWT login at `POST /api/auth/login`; send the returned token as
  `Authorization: Bearer <token>`. HTTP Basic authentication is not configured.
- `ADMIN` and `NURSE` method authorization. Admins create patients and manage users;
  both roles can read patients and create notes.
- Note authorship comes from the authenticated account. Structured entries can be
  read by patient, edited and verified.
- Shift summaries support `shift`, `patientId`, `nurseUsername`, `from` and `to`
  filters. Nurses are restricted to their own summaries; admins can query across
  nurses. `DAY`, `EVENING` and `NIGHT` are the shift values.

The Java source is authoritative. Earlier Sprint 2A Basic Auth instructions have
been superseded by the JWT implementation; this repository is not a claim of a
completed healthcare product.

## Local setup

Requires JDK 17, Maven, PostgreSQL 15 (or Docker with Compose), and optionally
`curl` and `jq` for the example below. Dependencies and versions are in `pom.xml`;
there is no Maven wrapper.

```sh
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The checked-in Compose service starts a local development database on port 5432
using the demo settings in `src/main/resources/application-dev.properties`.
It binds the host port on all interfaces by default; use an isolated development
machine. The application defaults to the `dev` profile and port 8080. On an empty
database, the dev seeder creates `admin` / `admin123` and `nurse1` / `nurse123`,
plus two synthetic patients and sample notes. Existing data is not reset.

```sh
TOKEN=$(curl --fail-with-body -sS http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"nurse1","password":"nurse123"}' | jq -er .token)
curl --fail-with-body -sS http://localhost:8080/api/patients \
  -H "Authorization: Bearer $TOKEN"
curl --fail-with-body -sS -X POST \
  'http://localhost:8080/api/shift-summaries/generate?shift=DAY' \
  -H "Authorization: Bearer $TOKEN"
```

API descriptions are available at <http://localhost:8080/swagger-ui.html> and
<http://localhost:8080/v3/api-docs>. These are development endpoints.

## Configuration and limits

`JwtUtil` reads `jwt.secret` and `jwt.expiration` (seconds, default 3600).
Spring environment overrides are `JWT_SECRET` and `JWT_EXPIRATION`, **not**
`JWT_EXP_SECONDS`. The code contains a development fallback signing key that is
also reachable outside the dev profile; setting the prod profile alone does not
remove it. The prod datasource reads `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`,
with schema validation instead of automatic updates. These settings are not
production approval or evidence of clinical, compliance or security readiness.

Structured extraction and summaries are rule-based in the service classes, not a
validated clinical model. There is no clinical evaluation, deployment acceptance
or measured accuracy claim in this repository.

## Validation

```sh
mvn test
mvn package
```

The tests use the H2 settings in `src/test/resources`, so they do not require the
dev PostgreSQL service. `Sprint2BSecurityTests` exercises authentication and role
boundaries; `StructuredEntryControllerTest` covers structured-entry APIs.
Build output belongs in ignored `target/`, not source control. Test and package
commands above are reproduction instructions, not a claim that they passed in
every environment.
