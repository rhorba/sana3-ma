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

## SESSION_END 2026-07-06
Done this session:
- Resumed Sprint 1 at Batch 2 (backend auth). No new architecture decisions needed (JWT/bcrypt/endpoints were
  already fixed in docs/architecture-sana3-ma.md ADR-3 and docs/security-sana3-ma.md).
- Implemented full auth slice: domain (User, Role, UserRepository, DuplicateEmailException), application
  (RegisterUser/Login/RefreshToken handlers, PasswordHasher + TokenService ports), adapter-persistence (JPA
  UserEntity + repo adapter), adapter-web (AuthController, BCryptPasswordHasher cost 12, JjwtTokenService HS256,
  JwtAuthenticationFilter, SecurityConfig, error envelope). ADMIN role blocked from public self-registration.
- Tests: 36 passing (domain/application unit, adapter-persistence Testcontainers Postgres+PostGIS, adapter-web
  @WebMvcTest + JWT/filter unit tests).
- Ran a real docker-compose smoke test (register/login/refresh/protected-endpoint) — found and fixed a genuine bug
  the unit tests missed (Tomcat's /error dispatch re-entering the security filter chain and clobbering 400s with
  401; fixed via permitAll on /error). Reverified via curl and reran the full suite green afterward.
- Committed locally as 8154830 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).
- Local port conflict again this session: DB_HOST_PORT 5433 was taken by an unrelated project's container; bumped
  to 5434 in local .env only (.env.example unchanged).

Next session: resume at Batch 3 (backend artisan profile — CQRS command/query, ownership auth, tests). See
.logs/activity.md "PLAN 2026-07-05 — Sprint 1 batches" for the full B1-B10 breakdown and docs/stories-sana3-ma.md
Epic 2 (or wherever artisan profile stories live) for acceptance criteria.
