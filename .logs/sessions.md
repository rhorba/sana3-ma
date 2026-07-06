# SESSIONS — Sana3.ma



## SESSION_START 2026-07-04
Request: pivot stack to Java Spring Boot (backend) + Angular latest (frontend), Postgres+PostGIS retained, document-first method, full sprint video recording, Docker Compose for now (k8s later if needed), env vars with dev defaults, push every sprint end, CI must be green before done.

## SESSION_END 2026-07-06
Done this session:
- Pivoted stack (Next.js -> Java Spring Boot + Angular), logged decision
- Chosen architecture: comprehensive (hexagonal + CQRS-lite backend, NgRx frontend)
- All 10 foundation docs written to docs/, approved, committed+pushed to github.com/rhorba/sana3-ma (main)
- Sprint 1 planned: 10 batches (B1-B10), auth + artisan profile scope
- Batch 1 DONE: backend Maven multi-module skeleton (domain/application/adapter-persistence/adapter-web/bootstrap),
  Spring Boot 4.1.0 on Java 25, Flyway migrations (users, artisan_profiles+PostGIS), docker-compose.yml (postgres+backend),
  .env.example. Verified end-to-end via docker compose (build, Flyway migration, actuator health UP). Committed locally
  (not pushed yet — push happens at sprint SHIP per rule 7). Local port conflicts (5432, 8080 already in use on this
  machine) worked around via DB_HOST_PORT=5433 / BACKEND_HOST_PORT=8081 (container-to-container ports unaffected).

Next session: resume at Batch 2 (backend auth: register/login/refresh, JWT, bcrypt, tests) — see .logs/activity.md
"PLAN 2026-07-05 — Sprint 1 batches" for full B1-B10 breakdown, and docs/stories-sana3-ma.md Epic 1 for acceptance criteria.
Not yet pushed to origin — do that at sprint end (Batch 10 SHIP), or sooner if requested.

## SESSION_START 2026-07-06
Resumed from previous SESSION_END (2026-07-06). Continuing Sprint 1 at Batch 2: backend auth (register/login/refresh, JWT, bcrypt, tests). Batch 1 (backend Maven skeleton) confirmed done and locally committed, not yet pushed (push deferred to sprint SHIP per rule 7).
