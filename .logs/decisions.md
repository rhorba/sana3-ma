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
