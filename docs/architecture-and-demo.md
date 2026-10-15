# Architecture and demo notes

## Architecture

```text
Browser (Angular standalone app)
       | development: http://localhost:8080/api (CORS)
       | production: same-origin /api
       v
Spring Boot REST API — validation, pagination, CRUD, country/currency summaries
       |
       v
Spring Data JPA — indexed employee table — persistent H2 database
```

The API is the source of truth for validation and filtering. The directory returns bounded pages (12 by default; API cap 100), so loading 10,000 employees does not require sending them to the browser. The country overview is calculated by the database and groups by both country and currency; values from different currencies are never combined. Seed data uses deterministic names, country allocation, salaries, dates, and unique synthetic email addresses, and is inserted only when the employee table is empty.

H2 is a convenient file-backed relational database for the self-contained assessment demo. A production deployment should use managed PostgreSQL (with schema migrations), authentication/authorization, audit logging, backups, TLS, and secrets from a managed secret store before entering actual employee compensation data.

## Run locally

Requirements: Java 21 and Node 20.19+ or 22.12+ with npm. The checked-in Maven Wrapper downloads Maven 3.9.9.

1. Start the API: `cd backend` then `.\mvnw.cmd spring-boot:run`. The first startup creates the schema and seeds 10,000 sample employees.
2. In another terminal at the repository root, run `npm ci` then `npm start`.
3. Open `http://localhost:4200`. The development environment calls `http://localhost:8080/api` directly. Configure backend CORS to allow `http://localhost:4200`.
4. Run tests with `cd backend` then `.\mvnw.cmd test`, and `npm test -- --watch=false` from the project root.

The database persists under `backend/data/`. Remove that database only when you intentionally want a fresh demo dataset.

## Container deployment

Run `docker compose up --build`, then open `http://localhost:4200`. The API seeds an empty persistent Docker volume on its first start; the web container waits for the API health check. This is a local/demo deployment, not a production-secured hosting configuration.

## Demo walkthrough

1. Open the dashboard and point out the 10,000-employee count and the local-currency note.
2. Search for `employee100` and filter to a country/department; use the pagination controls to browse records.
3. Add an employee with a valid email, country, salary, and start date. Show it in the directory.
4. Edit the employee's department/salary, then demonstrate duplicate email validation.
5. Delete the sample record and inspect the country compensation averages and ranges.

## Trade-offs

- A server-side paginated directory and database aggregates keep the interface responsive with 10,000 rows.
- Synthetic records have deterministic unique email IDs to make refreshes and tests repeatable.
- Country/currency grouping avoids false comparisons without an approved exchange-rate source.
- Simple CRUD is prioritized over spreadsheet upload/export until import formats and conflict policies are clear.
