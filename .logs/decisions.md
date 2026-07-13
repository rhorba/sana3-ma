# DECISIONS — Sana3.ma



## DECISION 2026-07-04 — Stack pivot
Sana3.ma pivots from Next.js/Drizzle to: Backend = Java Spring Boot (latest), Frontend = Angular (latest), DB = PostgreSQL 16 + PostGIS (unchanged). README/docs to be updated accordingly.
Deploy target for sprint 1: Docker Compose only (YAGNI). Kubernetes deferred until real scaling need.
Sprint 1 scope: Auth (register/login, JWT) + Artisan profile CRUD. Angular shell with login/profile pages.
Git remote: github.com/rhorba/sana3-ma (to be wired as origin before sprint-1 SHIP push).

## DECISION 2026-07-04 — Architecture approach (sprint 1)
Chosen: COMPREHENSIVE. Backend = hexagonal (ports & adapters), multi-module Maven, domain layer framework-free, CQRS-lite for artisan-profile reads. Frontend = Angular with full NgRx store, structured for future module-federation/micro-frontend split.
Note: heavier than sprint-1 scope (auth+profile CRUD) strictly requires; user chose this deliberately over YAGNI-recommended Simple, citing anticipated multi-team/bounded-context growth. Architecture + Test Architect docs must reflect this choice's ADR with re-evaluate trigger.

## BRAINSTORM 2026-07-06 — Batch 2 (backend auth) approach
No new architectural decision needed: JWT scheme (HS256 dev, access 15min, refresh 7d httpOnly SameSite=Strict cookie), bcrypt cost 12, and endpoints (POST /api/v1/auth/register|login|refresh) were already fixed in docs/architecture-sana3-ma.md ADR-3 and docs/security-sana3-ma.md §3. Confirmed with user: proceed with those decisions as-is for Batch 2, no re-brainstorm.

## BRAINSTORM 2026-07-10 — Batch 7 (docker-compose wiring) API_BASE_URL approach
Chosen: build-time Docker ARG substitution over a runtime nginx env.js approach. Reason: YAGNI — the app
only needs one environment (local docker-compose) this sprint per docs/devops-sana3-ma.md §1 (staging/prod
not needed yet); a runtime config file adds an entrypoint script and window.__env wiring with no present
payoff. Re-evaluate if/when a staging environment is actually stood up (would need image-once-deploy-many
without a rebuild).

## FIX 2026-07-10 — Batch 8 security scan findings
Semgrep flagged `backend/Dockerfile` running the JVM as root (no USER directive) — added a non-root `app`
user in the final stage. Trivy image scan then found 5 HIGH CVEs (libexpat, p11-kit) in the backend's
Alpine base and 4 HIGH CVEs (c-ares, libexpat) in the frontend's — all with fixes already published in the
Alpine repo, just not yet pulled into the base image layer. Added `RUN apk upgrade --no-cache` to both
Dockerfiles' final stage rather than pinning specific package versions, so future `docker compose build`
runs keep picking up Alpine's latest security patches automatically. Neither finding was Critical (the
documented CI gate only fails on Critical), but both had trivial fixes so fixed anyway rather than deferring.

## BRAINSTORM 2026-07-13 — Sprint 4 scope
Options presented (ranked simple->complex per YAGNI): real payment gateway (extends existing checkout),
QR craft certificates (independent greenfield), cooperative multi-user accounts (identity rework), DHL
export (naturally follows payment). User chose **cooperative multi-user accounts** — a bigger
architectural scope than the "simplest first" default, explicitly overriding the recommended option
(payment gateway). Proceeding to PLAN: need to read current identity/auth model (User entity,
ArtisanProfile ownership) before scoping the multi-user rework.
