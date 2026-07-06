# ACTIVITY — Sana3.ma



## MILESTONE 2026-07-04 — Foundation docs drafted
All 10 foundation docs written to docs/: prd, system-design, architecture, security, database, ux, ui, test-strategy, devops, stories (sana3-ma). Pending user approval before commit+push (rule 13).

## PUSH 2026-07-05
Branch: main | Commit: 449b7ad
Pushed foundation docs (docs/ x10) + README pivot + .gitignore + .claude/.skills to github.com/rhorba/sana3-ma.

## PLAN 2026-07-05 — Sprint 1 batches
B1 backend scaffold (multi-module Maven, Flyway, .env.example, compose skeleton)
B2 backend auth (register/login/refresh, JWT, bcrypt, tests)
B3 backend artisan profile (CQRS command/query, ownership auth, tests)
B4 frontend scaffold (Angular standalone, Material, NgRx store, routing, Dockerfile)
B5 frontend auth UI (login/register, NgRx auth slice, JWT interceptor, guards, tests)
B6 frontend profile UI (view/edit, NgRx profile slice, tests)
B7 docker-compose full wiring + local end-to-end smoke test
B8 VERIFY: coverage (JaCoCo+Karma) >=80%, security scan (Semgrep/Trivy/Gitleaks)
B9 CI: GitHub Actions workflow, push, monitor+fix until green
B10 SHIP: Playwright E2E + video recording, final push, sprint retro, SESSION_END

## BATCH 1 DONE 2026-07-06 — Backend Maven skeleton
Multi-module Maven (domain/application/adapter-persistence/adapter-web/bootstrap), Spring Boot 4.1.0 (Java 25), Flyway migrations V1 (users) + V2 (artisan_profiles, PostGIS), .env.example, docker-compose.yml (postgres+backend), backend/Dockerfile.
Fixed during verification: Initializr's bootVersion label "4.1.0.RELEASE" doesn't exist on Central (real: 4.1.0); EntityScan moved to org.springframework.boot.persistence.autoconfigure in Boot 4; adapter-persistence was missing spring-boot-starter-flyway (had only the raw flyway-database-postgresql driver, so FlywayAutoConfiguration never activated) — fixed and re-verified end-to-end via docker compose (Flyway applied both migrations, actuator health UP). Host ports 5432/8080 were already taken locally, remapped to 5433/8081 via DB_HOST_PORT/BACKEND_HOST_PORT env vars (container-to-container traffic unaffected).

## PLAN 2026-07-06 — Batch 2 task breakdown (backend auth)
Task 2.1: domain — User entity, Email/PasswordHash value objects, Role enum, UserRepository port
Task 2.2: application — RegisterUser/Login/RefreshToken command handlers, PasswordHasher + TokenService ports
Task 2.3: adapter-persistence — JPA UserEntity, Spring Data repo, UserRepositoryAdapter
Task 2.4: adapter-web — AuthController (register/login/refresh), DTOs, BCrypt + JJWT impls, Spring Security config (stateless, JWT filter), refresh cookie handling
Task 2.5: bootstrap wiring — application.yml JWT/security properties bound to existing .env.example vars
Task 2.6: tests — domain/application unit tests, adapter-persistence Testcontainers repo test, adapter-web @WebMvcTest/security test

## BATCH 2 DONE 2026-07-06 — Backend auth (register/login/refresh)
Domain: User entity, Role enum, UserRepository port, DuplicateEmailException (backend/domain/.../user/).
Application: RegisterUser/Login/RefreshToken command handlers, PasswordHasher + TokenService ports, AuthResult (backend/application/.../auth/).
Adapter-persistence: JPA UserEntity, Spring Data repo, UserRepositoryAdapter, mapper (backend/adapter-persistence/.../user/).
Adapter-web: AuthController (register/login/refresh), DTOs, RegistrableRole (blocks ADMIN self-registration), BCryptPasswordHasher (cost 12),
JjwtTokenService (HS256, access 15min/refresh 7d per ADR-3), JwtAuthenticationFilter, SecurityConfig (stateless, CORS allow-list, permitAll auth+error paths),
AuthExceptionHandler ({"error":{code,message,details}} envelope).
Tests: 36 total, all green — 3 domain unit, 7 application unit (Mockito), 3 adapter-persistence (Testcontainers Postgres+PostGIS, real Flyway migrations),
16 adapter-web (8 @WebMvcTest controller/validation/error-mapping, 5 JJWT round-trip unit, 3 JwtAuthenticationFilter unit).
VERIFY: ran full docker-compose smoke test (register/duplicate-email/admin-role-reject/short-password/login-wrong/login-ok/refresh/refresh-no-cookie/protected-401) —
found and fixed a real bug (see .logs/issues.md — /error dispatch security hole) that unit tests alone missed. Coverage % measurement deferred to Batch 8 (dedicated VERIFY batch) per sprint plan; this batch's own tests are unit+integration+manual-e2e verified.
Not pushed yet — push happens at sprint SHIP (Batch 10) per rule 7, or sooner if requested.
Committed locally as 8154830 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).
