# ACME People — Salary Management

A full-stack employee compensation management demo built with Angular 21, Java 21/Spring Boot 3, Spring Data JPA, and a persistent H2 relational database.

**Requirements and product scope:** [requirements.md](./requirements.md)  
**Architecture, trade-offs, local/container run, and demo checklist:** [docs/architecture-and-demo.md](./docs/architecture-and-demo.md)

## Features

- Seeded with 10,000 deterministic synthetic employee records on first startup.
- Employee directory with server-side search, country/department filters, and pagination.
- Validated employee create/update/delete; work email addresses are unique.
- Country compensation summaries show average/range by local currency, never mixing currencies.
- Responsive Angular UI, accessible labels, inline errors, and confirm-before-delete.
- Docker Compose deployment for a persistent local demo.

## Run locally

Use Java 21 and Node 20.19+ or 22.12+ with npm. The Maven Wrapper downloads Maven 3.9.9 automatically.

```powershell
# Terminal 1 — backend
Set-Location backend
.\mvnw.cmd spring-boot:run

# Terminal 2 — frontend, from the project root
npm ci
npm start
```

Open [http://localhost:4200](http://localhost:4200). The development frontend calls `http://localhost:8080/api` directly; configure the backend to allow CORS requests from `http://localhost:4200`. The first API startup creates `backend/data/` and seeds the database; subsequent starts preserve edits.

For a containerized local deployment, run `docker compose up --build` and visit [http://localhost:4200](http://localhost:4200).

## Tests and build

```powershell
# Frontend
npm test -- --watch=false
npm run build

# Backend
Set-Location backend
.\mvnw.cmd test
```

## API

- `GET /api/employees?page=0&size=12&search=&country=&department=`
- `POST /api/employees`
- `PUT /api/employees/{id}`
- `DELETE /api/employees/{id}`
- `GET /api/analytics/countries`

See the requirements and architecture notes before treating this assessment demo as a production HR system. Real employee compensation requires authentication, authorization, audit history, operational controls, and a production database.
