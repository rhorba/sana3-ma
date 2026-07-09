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

## PLAN 2026-07-08 — Batch 4 task breakdown (frontend scaffold)
Task 4.1: ng new (latest Angular, standalone, SCSS, routing) in frontend/
Task 4.2: ng add @angular/material, basic theme
Task 4.3: NgRx store + devtools + effects, empty auth/artisan-profile feature slices (ADR-2)
Task 4.4: routing skeleton — home/login/register/profile placeholders + not-found
Task 4.5: smoke test for root component
Task 4.6: frontend/Dockerfile (node:22-alpine build -> nginx:alpine), per docs/devops-sana3-ma.md
Task 4.7: verify build/test/lint, commit, log
Structural only — auth/profile UI logic deferred to Batch 5/6 per sprint plan.

## PLAN 2026-07-08 — Batch 5 task breakdown (frontend auth UI)
Task 5.0: fix Batch 4 gap — custom M3 Material theme from docs/ui-sana3-ma.md tokens (terracotta/teal/Inter),
  replacing the azure-blue prebuilt theme (doc explicitly deferred this to EXECUTE; missed in Batch 4).
Task 5.1: AuthService (HttpClient, withCredentials) — register/login/refresh against backend contract
  (POST /api/v1/auth/{register,login,refresh}, ApiError envelope, AuthResponse shape) confirmed from
  backend/adapter-web/src/main/java/ma/sana3/adapter/web/auth/*.java
Task 5.2: auth NgRx actions/reducer/effects/selectors (register/login/refresh/logout), replacing empty
  placeholder reducer from Batch 4
Task 5.3: JWT interceptor (attaches access token from store to outgoing requests) + provideHttpClient wiring
Task 5.4: auth route guard on /profile + silent-refresh-on-bootstrap (access token is memory-only per
  docs/security-sana3-ma.md — without a bootstrap refresh attempt, every page reload looks logged-out)
Task 5.5: Login/Register reactive form components (Material fields, inline validation per Story 1.3 AC)
Task 5.6: unit tests (reducer, effects, guard, interceptor, components), verify build/test/lint, commit, log

## PLAN 2026-07-09 — Batch 6 task breakdown (frontend profile UI)
Task 6.0: fix Batch 5 gaps found re-reading docs/ux-sana3-ma.md — authGuard becomes artisan-only (site map:
  "/profile (ARTISAN only)"); login/register success redirect becomes role-based (buyer->/, artisan->/profile)
  instead of unconditional ->/profile (Flow 1: "Redirect: Buyer->Home stub / Artisan->Profile edit").
Task 6.1: ArtisanProfileService (HttpClient) — GET/PUT /api/v1/artisan-profiles/me, matching backend contract
  from backend/adapter-web/.../artisanprofile/*.java (ArtisanProfileResponse, 404 PROFILE_NOT_FOUND, 403
  NOT_AN_ARTISAN, 400 VALIDATION_FAILED)
Task 6.2: artisan-profile NgRx actions/reducer/effects/selectors (load/save), replacing empty placeholder
Task 6.3: Profile component — combined view/edit reactive form per docs/ux-sana3-ma.md wireframe (display
  name, craft type, region, bio, phone), empty-state prompt, skeleton loading, save toast
Task 6.4: unit tests (reducer, effects, component), verify build/test/lint, commit, log

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

## PLAN 2026-07-06 — Batch 3 task breakdown (backend artisan profile)
No new architecture decisions needed (CQRS-lite command/query split fixed by ADR-1; ownership rule and endpoints fixed by docs/stories-sana3-ma.md Epic 2). V2 migration (artisan_profiles + PostGIS) already exists from Batch 1.
Task 3.1: domain — ArtisanProfile entity, ArtisanProfileRepository port, ProfileNotFoundException. Location (PostGIS point) left null/unused this batch — no lat/lng field in the AC, so no input path for it yet (YAGNI).
Task 3.2: application — UpdateArtisanProfileCommand/Handler (upsert: create-or-update per story 2.1), GetArtisanProfileQuery/Handler (separate read path per story 2.2 CQRS note). Business rule: only role=ARTISAN may own a profile (per architecture data-model note), enforced in the command handler.
Task 3.3: adapter-persistence — JPA ArtisanProfileEntity, Spring Data repo, RepositoryAdapter, mapper.
Task 3.4: adapter-web — ArtisanProfileController (PUT/GET /api/v1/artisan-profiles/me), DTOs, ownership from JWT principal (UUID already set as Authentication principal by existing JwtAuthenticationFilter — no new filter needed), reuse existing error envelope.
Task 3.5: tests — domain/application unit, adapter-persistence Testcontainers, adapter-web @WebMvcTest + security.
Task 3.6: verify — docker-compose smoke test (create as artisan, get, reject as buyer, reject unauthenticated).

## BATCH 3 DONE 2026-07-06 — Backend artisan profile (CQRS command/query)
Domain: ArtisanProfile entity (immutable, `create`/`withDetails`), ArtisanProfileRepository port (backend/domain/.../artisanprofile/).
Application: UpdateArtisanProfileCommand/Handler (upsert, rejects non-ARTISAN role via NotAnArtisanException), GetArtisanProfileQuery/Handler
(read path, ProfileNotFoundException on miss), ArtisanProfileResult + mapper (backend/application/.../artisanprofile/).
Adapter-persistence: ArtisanProfileJpaEntity (maps to existing V2 artisan_profiles table; PostGIS location column left unmapped/unused per plan),
Spring Data repo, RepositoryAdapter, mapper.
Adapter-web: ArtisanProfileController (PUT/GET /api/v1/artisan-profiles/me), ownership from JWT principal (UUID, already set by Batch 2's
JwtAuthenticationFilter), role derived from the ROLE_* authority, UpsertArtisanProfileRequest/ArtisanProfileResponse DTOs,
ArtisanProfileExceptionHandler (403 NOT_AN_ARTISAN, 404 PROFILE_NOT_FOUND, 400 VALIDATION_FAILED — reuses auth package's ApiError envelope).
Tests: 17 new (4 domain, 5 application Mockito, 3 adapter-persistence Testcontainers, 5 adapter-web @WebMvcTest) — all green.
Found during test-writing (not a runtime bug): @WebMvcTest needs real Spring Security filters active (not addFilters=false) for
SecurityMockMvcRequestPostProcessors.authentication(...) to populate the SecurityContext, and PUT/GET requests need .with(csrf())
since the default (non-custom) security auto-config used by this test slice has CSRF enabled unlike the real stateless SecurityConfig.
VERIFY: full docker-compose smoke test (register artisan+buyer, GET /me before profile exists -> 404, PUT create -> 200, GET -> 200,
PUT update same id with new updatedAt -> 200, PUT as buyer -> 403 NOT_AN_ARTISAN, GET unauthenticated -> 401, PUT blank displayName -> 400
VALIDATION_FAILED) — all as expected. Full backend test suite (46 tests) green via `mvnw -o test`. Coverage % measurement still deferred to
Batch 8 (dedicated VERIFY batch) per sprint plan.
Not pushed yet — push happens at sprint SHIP (Batch 10) per rule 7, or sooner if requested.

## BATCH 4 DONE 2026-07-08 — Frontend scaffold
Angular 22 standalone app (frontend/), Angular Material (azure-blue theme — corrected to brand theme in
Batch 5), NgRx store/effects/devtools wired with empty auth/artisanProfile feature slices per ADR-2,
lazy-loaded routing skeleton (home/login/register/profile placeholders + not-found), ESLint, multi-stage
Dockerfile (node:22-alpine build -> nginx:alpine serve). Deviations from foundation docs (both logged,
both resolved with the user): Karma -> Vitest (Karma deprecated upstream, no longer scaffolded by `ng new`;
docs/test-strategy-sana3-ma.md and docs/devops-sana3-ma.md updated), and NgRx 21.1.1 installed against
Angular 22 via --legacy-peer-deps since NgRx has not yet shipped Angular-22 support (tracked as an open
risk in .logs/risks.md, frontend/.npmrc pins legacy-peer-deps so `ng add`/CI `npm ci` don't also break).
Verified: `ng build`/`ng test` (7/7)/`ng lint` clean, and a real `docker build` of frontend/Dockerfile
end-to-end (confirmed the nginx image serves the built app from the correct path).
Committed locally as c825a51 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).

## BATCH 5 DONE 2026-07-09 — Frontend auth UI
Fixed the Batch 4 theme gap first: generated a real M3 palette from docs/ui-sana3-ma.md's brand colors
(terracotta #B5651D / teal #2E7D6B) via `ng generate @angular/material:theme-color`, replacing the
azure-blue placeholder; wired Inter via Google Fonts.
Real auth NgRx slice (auth.actions/reducer/effects/selectors.ts) replacing Batch 4's empty placeholder:
register/login/refreshToken/logout matching the exact backend contract (AuthResponse shape, ApiError
envelope, 409/401/400 codes) read directly from backend/adapter-web/.../auth/*.java. AuthService
(HttpClient, withCredentials for the httpOnly refresh cookie), functional JWT interceptor (attaches access
token from the store via selectSignal), authGuard on /profile, and a provideAppInitializer that dispatches
a silent refreshToken on bootstrap so a page reload restores the session instead of bouncing to /login
(access token is intentionally memory-only per docs/security-sana3-ma.md).
Login/Register standalone components: reactive forms, Material fields, inline validation
(required/email/minlength 10) per Story 1.3 AC, role radio group on register, MatSnackBar for auth errors,
redirect-on-authenticated honoring `returnUrl`. Added a logout control to the app shell toolbar (beyond
Story 1.3's explicit scope, but needed for the auth slice to be usable end-to-end).
Found via browser smoke-testing (not caught by unit tests): logout only cleared client-side state — no
backend logout endpoint existed, so the httpOnly refresh cookie stayed valid and a reload silently
restored the "logged out" session, and the session was never actually revoked server-side. Surfaced to the
user; user chose to add a minimal backend fix rather than ship client-only or drop the feature: `POST
/api/v1/auth/logout` (backend/adapter-web AuthController) expires the refresh cookie — stateless JWT
(ADR-3) has no server-side session store to revoke beyond that (accepted limitation, documented in the
controller). Added AuthControllerTest coverage and a frontend logout$ effect (non-dispatching).
Tests: 30 frontend (reducer, effects incl. logout$, guard, interceptor, Login/Register, app shell) green,
2 new backend AuthControllerTest cases green (full backend suite still green). `ng build`/`ng test`/
`ng lint` all clean.
VERIFY: real end-to-end browser smoke test against a live docker-compose backend (not just unit tests) —
register -> auto-login -> redirect, inline validation errors, theme rendering correctly, hard-reload
restores session via the cookie, logout -> reload now correctly requires login again (post-fix),
login -> returnUrl redirect. Local port override (4201) used since 4200 was occupied by an unrelated
process on this machine; .env reverted to the documented default (4200) afterward, docker/dev-server
stopped after testing.
Committed locally as 6c9cc21 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).

## BATCH 6 DONE 2026-07-09 — Frontend artisan profile UI
Fixed two Batch 5 gaps first, found re-reading docs/ux-sana3-ma.md: /profile is ARTISAN only (site map),
and post-auth redirect is role-based (buyer->Home, artisan->Profile), not unconditional ->/profile.
authGuard renamed artisanGuard (checks role, non-artisans -> home instead of a 403-on-save form);
login/register redirects fixed; toolbar hides the Profile link for non-artisans.
Real artisan-profile NgRx slice (actions/reducer/effects/selectors) replacing Batch 4's empty placeholder:
load/save against GET/PUT /api/v1/artisan-profiles/me matching the exact Batch 3 backend contract
(ArtisanProfileResponse, 404 = normal empty state per Story 2.3 not an error, 403 NOT_AN_ARTISAN, 400
VALIDATION_FAILED). ArtisanProfileService (HttpClient; JWT interceptor handles auth, no withCredentials
needed unlike the auth endpoints). Profile component: single combined view/edit form matching
docs/ux-sana3-ma.md's wireframe exactly (display name, craft type, region, bio, phone) — pre-filled when a
profile exists, "Complete your profile" prompt when it doesn't, loading state, "Profile updated" toast on
save (exact UX-doc phrase). Extracted shared extractErrorMessage/ApiError util (core/http-error.util.ts,
core/api-error.model.ts) out of auth.effects.ts to avoid duplicating it in artisan-profile.effects.ts.
Tests: 54 frontend (added profile reducer/effects/component, guard role cases, redirect cases) green.
`ng build`/`ng test`/`ng lint` clean (one transient `ng build` network failure fetching Google Fonts,
unrelated to code — noted in .logs/risks.md as a Batch 9 CI consideration).
VERIFY: real end-to-end browser smoke test against a live docker-compose backend — artisan session
auto-redirected to /profile showing the empty-state prompt correctly, filled+saved the form, got the
success toast, reload confirmed the data persisted via GET; fresh buyer registration landed on Home, and
navigating to /profile directly bounced back to Home (artisanGuard blocking a non-artisan). Same local
port workaround as Batch 5 (4201 + matching CORS_ALLOWED_ORIGINS), .env reverted afterward, docker/dev
server stopped after testing.
Committed locally as c3cbea7 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).
