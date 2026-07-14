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

## BATCH 7 2026-07-10 — docker-compose full wiring + local end-to-end smoke test
Added the `frontend` service to docker-compose.yml (build args from API_BASE_URL, port
FRONTEND_HOST_PORT:-4200:80, depends_on backend). API_BASE_URL is baked into the static Angular build via a
Docker build ARG in frontend/Dockerfile (writes core/api.config.ts before `npm run build`) — chosen over a
runtime nginx env.js approach per YAGNI, since staging/prod aren't needed this sprint (docs/devops-sana3-ma.md
§1). Fixed a stale .env/.env.example value (API_BASE_URL pointed at :8080, but BACKEND_HOST_PORT default is
:8081 — never caught because nothing exercised the full container stack before this batch) and added
FRONTEND_HOST_PORT.
Real bug found and fixed during the smoke test: nginx's default config has no SPA fallback, so direct
navigation or a hard reload on any Angular client-side route (e.g. /register, /profile) 404'd at the nginx
level — only `ng serve`'s own dev-server routing had been exercised in Batches 4-6, so this never surfaced
until the actual container was tested. Added frontend/nginx.conf (`try_files $uri $uri/ /index.html;`),
wired it into the Dockerfile, documented it in docs/devops-sana3-ma.md.
Local environment note: port 4200 was already bound by an unrelated project's container on this machine
(atlas-events); remapped FRONTEND_HOST_PORT/CORS_ALLOWED_ORIGINS to 4202 in the local .env only (gitignored
— .env.example keeps the clean 4200 default for other machines).
Full end-to-end smoke test against the fully-dockerized stack (postgres + backend + frontend, no `ng serve`,
no local backend process): registered a fresh artisan account, auto-redirected to /profile empty state,
filled and saved the profile form, hard-reloaded (full nginx round trip, not an Angular router nav) and
confirmed both the SPA route resolved and the session + profile data persisted, logged out, confirmed
direct navigation to /profile now redirects to /login?returnUrl=%2Fprofile. All 3 containers verified
healthy (backend /actuator/health UP, frontend 200, postgres healthy) before and after.
Containers stopped after testing. Not yet committed — see next session.

## BATCH 9 2026-07-10 — CI (GitHub Actions)
Added Spotless (google-java-format, pinned to 1.27.0 + JVM add-exports/add-opens flags in
backend/.mvn/jvm.config — needed for it to run under JDK 25) as the backend lint tool, chosen over
Checkstyle for being auto-fixable with minimal config. Reformatted the whole backend to a clean baseline
(mechanical diff, no logic changes).
Built .github/workflows/ci.yml: 5 jobs (lint, test, security-scan, build, deploy-staging) matching
docs/devops-sana3-ma.md §2 exactly. Test job enforces the combined coverage gate via a new
scripts/check-coverage.sh (blends backend JaCoCo line coverage + frontend Vitest json-summary line
coverage, fails under 80%). Security-scan runs Semgrep CLI directly (not the marketplace action — avoided
an unverified action interface), Trivy via the official aquasecurity/trivy-action@v0.36.0 (verified the tag
exists via `gh api` before committing to it), and Gitleaks via its open-source Docker image directly
(the gitleaks/gitleaks-action marketplace wrapper is a separate commercially-licensed product — sidestepped
that ambiguity by running the same `docker run zricethezav/gitleaks` invocation used for manual scans in
Batch 8). Build job builds both images and re-runs a Trivy image scan (Critical-only) as defense in depth.
Deploy-staging is an explicit placeholder (echoes why) since no staging infra exists yet per docs §1 — the
job exists so CI's shape matches the documented 5 stages, not to actually deploy anything.

## PUSH 2026-07-10
Branch: main | Commits: 20 (449b7ad..8791215) — first push of application code since the foundation-docs
push on 2026-07-05. Covers all of Batches 1-9 (backend scaffold/auth/profile, frontend scaffold/auth-UI/
profile-UI, docker-compose wiring, coverage+security verify, CI pipeline).
CI run 29083741944 triggered automatically: **all 5 jobs green on the first run** (Lint 32s, Test+Coverage
Gate 1m23s, Security Scan 58s, Build Docker Images 1m54s, Deploy to Staging 2s — ~4min total). No red-CI
diagnose/fix cycle was needed (rule 11 monitoring requirement satisfied trivially).

## BATCH 10 2026-07-10 — SHIP
Added a Playwright e2e/ suite (separate toolchain from the Angular unit tests, per
docs/test-strategy-sana3-ma.md's tooling table) with one continuous test covering all 4 documented ATDD
scenarios in a single browser context, producing a single video per rule 9. Ran it against the full
docker-compose stack (not `ng serve`) — passed on the first attempt. Saved to
`.recordings/v0.1-2026-07-10.webm` (gitignored, stays local — see risks.md pattern already established for
binaries). Updated docs/test-strategy-sana3-ma.md's release gate checklist to all-checked with evidence
links.

## RETRO 2026-07-10 — Sprint 1 (Batches 1-10)
**Delivered**: full auth (register/login/refresh/logout) + artisan profile CRUD, backend (hexagonal Maven
multi-module, Spring Boot 4.1/Java 25) and frontend (Angular 22 standalone + NgRx), fully wired through
docker-compose, ≥80% combined coverage, clean security scan, green CI, and a recorded E2E happy-path video.
Sprint scope (docs/stories-sana3-ma.md Epics 1-2) shipped in full — nothing cut.

**What deviated from the plan, and why** (all previously logged in .logs/decisions.md/risks.md, collected
here for a single retro view):
- Architecture chosen COMPREHENSIVE (hexagonal + CQRS-lite backend, full NgRx frontend) over the
  YAGNI-recommended Simple, per explicit user request anticipating future multi-team growth — heavier than
  strictly required for auth+profile CRUD, worth re-evaluating if that growth doesn't materialize by Sprint 3.
- Vitest replaced Karma for frontend unit tests (Angular 22 dropped Karma scaffolding upstream) — a tooling
  substitution forced by the framework, not a scope change; test syntax unaffected.
- NgRx 21.1.1 installed against Angular 22 via --legacy-peer-deps (NgRx hadn't shipped Angular-22 support
  yet at scaffold time) — flagged as a risk in Batch 4, monitored through Batches 5-9, closed clean at
  Batch 9 once CI's fresh `npm ci` proved it wasn't just a local-cache artifact.
- Two real bugs were found by *running* things rather than by unit tests, and fixed same-session rather
  than deferred: no server-side logout endpoint (Batch 5 — cookie stayed valid after "logout"), and nginx
  missing a SPA fallback (Batch 7 — direct navigation/hard-reload 404'd). Both only surfaced because every
  batch ended with a real browser or docker-compose smoke test instead of stopping at green unit tests —
  worth keeping as a standing practice, not just a Sprint 1 habit.
- Backend container ran as root and both images carried stale-but-patchable Alpine CVEs (Batch 8) — fixed
  same-session even though neither blocked the documented Critical-only gate, because the fixes were free.

**What went well**: every batch ended with a real end-to-end verification (browser or docker-compose), not
just unit tests passing — this is what caught both real bugs above. Document-first held up in practice, not
just as a rule: re-reading docs/ux-sana3-ma.md before Batch 6 caught two real gaps in what Batch 5 shipped
(profile scoped to ARTISAN-only, role-based redirect) before they became bugs a user would hit. CI went
green on the first push with zero fix cycles, largely because local Semgrep/Trivy/Gitleaks runs in Batch 8
already caught and fixed what CI would have caught anyway.

**Carried forward into Sprint 2** (see .logs/risks.md for full detail): stateless JWT has no server-side
session revocation beyond cookie TTL (accepted architectural limitation, not a bug — revisit only if a
compliance requirement demands real revocation); local dev machine has port contention with other unrelated
projects (4200/8080/5432 all taken) — not a project bug, just a standing note for whoever runs this locally
next.

**Not done / explicitly deferred** (YAGNI, not oversight): Kubernetes, staging deploy target (deploy-staging
CI job is a placeholder), production environment, mail/notifications, search, payments — none were in
Sprint 1 scope per docs/stories-sana3-ma.md.

## PLAN 2026-07-10 — Sprint 2 backlog (product catalog & browsing)
Full backlog written to docs/stories-sana3-ma-sprint2.md (Epic 3: Product Catalog self-service, Epic 4:
Public Browsing & Search — 9 stories total). PRD explicitly separates "Product catalog & browsing" from
"Orders, checkout, payment" as two future-scope bullets; this sprint stays on the catalog/browsing side.
Four scope gaps the foundation docs didn't answer were filled with stated YAGNI defaults (flagged in the
stories doc for override, not silently assumed): public no-login browsing, self-publish with no moderation,
single image on local disk (not S3/MinIO), free-text craft-type category defaulting from the artisan's own
profile. Also flagged a new PII consideration the catalog forces: public product listings need an
artisan-summary DTO (displayName/craftType/region only, never contactPhone/email) — this is the "public
artisan directory" trigger docs/database-sana3-ma.md §7 was already anticipating.

Batch breakdown (continuing numbering from Sprint 1's B1-B10):
B11 backend catalog domain + Flyway V3 (products table), domain/application scaffolding per ADR-1's
    bounded-context convention (new `ma.sana3.domain.catalog` package)
B12 backend product CRUD (artisan self-service: create/update/delete/list own) — Stories 3.1-3.3
B13 backend public browsing/search (list published, detail, filters) — Stories 4.1-4.3, includes the
    public-safe artisan-summary DTO and a docs/security-sana3-ma.md update (document-first)
B14 backend image upload (single image, local disk volume)
B15 frontend catalog NgRx slice + "My Products" UI — Story 3.4
B16 frontend public browse/search UI + product detail page — Stories 4.4-4.5
B17 docker-compose wiring for the image volume + any new env vars
B18 VERIFY: coverage (JaCoCo+Vitest) >=80%, security scan (Semgrep/Trivy/Gitleaks)
B19 CI: extend .github/workflows/ci.yml if new scanners/steps are needed, push, monitor+fix until green
B20 SHIP: Playwright E2E (browse, search, product CRUD), video recording, sprint retro, final push,
    SESSION_END

Not yet user-confirmed — this is the PLAN artifact for review before EXECUTE starts (project rule 5).

## BATCH 11 2026-07-10 — backend catalog domain scaffold
User confirmed the Sprint 2 plan ("start") — EXECUTE begins. New `ma.sana3.domain.catalog` package: `Product`
entity and `ProductRepository` port, built by directly mirroring `artisanprofile`'s exact style (final
entity, `create`/`withDetails`, narrow port with only the methods Stories 3.1-3.3 need — `save`, `findById`,
`findByArtisanProfileId`, `deleteById`; browsing/search query methods deferred to Batch 13 when that story
actually needs them, not guessed now). Flyway V3 migration: `products` table, FK to `artisan_profiles` with
`ON DELETE CASCADE`, no `status` column (self-publish default from the stories doc means every product is
immediately live — nothing to track). `mvn verify` green, including adapter-persistence's Testcontainers
suite, which is the real check that V3's SQL is valid (Spring context won't start if a migration fails).
Committed as 9630b0f (Sprint 2 plan docs) and 224ca86 (this batch).

## BATCH 12 2026-07-10 — artisan self-service product CRUD (Stories 3.1-3.3)
Full application/persistence/web stack for `POST/PUT/DELETE/GET /api/v1/artisan-profiles/me/products[/{id}]`,
built by mirroring the artisanprofile bounded context's exact layering. Cross-bounded-context reuse decision:
product handlers inject `ArtisanProfileRepository` to resolve `userId -> artisanProfileId` and reuse
`NotAnArtisanException`/`ProfileNotFoundException` from the artisanprofile application package rather than
duplicating them — same precondition ("must be an artisan with a profile") applies to creating a product as
to creating the profile itself. Update/delete on another artisan's product returns 404 (not 403) — same
no-info-disclosure pattern the security doc already established elsewhere. `ProductExceptionHandler` needed
its own `@RestControllerAdvice(basePackages="ma.sana3.adapter.web.catalog")` — Spring's basePackages scoping
means the artisanprofile package's advice doesn't catch exceptions thrown from a different package's
controller, even for the same exception classes.
24 new tests, full `mvn verify` green. Also smoke-tested the real endpoints against the dockerized stack
(not just tests): full create/list/update/delete flow for an artisan, confirmed a buyer gets 403
NOT_AN_ARTISAN on both create and list.
Committed as dd63eee.

## BATCH 13 2026-07-10 — public product browsing and search (Stories 4.1-4.3)
`GET /api/v1/products` (paginated, filterable by craftType/region/price range/keyword) and
`GET /api/v1/products/{id}`, both public (no auth). Cross-aggregate composition (a product plus its owning
artisan's public summary) handled in the application layer: extended `ArtisanProfileRepository` with
`findById`/`findByIds` (free — both already provided by Spring Data's `JpaRepository`, no new query needed),
batch-fetch artisan profiles for a results page to avoid N+1. `PublicProductSummary` only carries an
allowlist (displayName/craftType/region) — `contactPhone`/email have no field on the DTO at all, so there's
no runtime filter that could be forgotten or bypassed.
Real bug caught by the persistence test suite (not written into it — a genuine test failure surfaced it):
PostgreSQL/Hibernate can't infer a null bind parameter's type inside `LOWER(:param)` and silently picks
`bytea`, so any unset filter crashed with "function lower(bytea) does not exist". Fixed by pre-lowercasing
filter values in Java before binding, so JPQL only ever calls `LOWER()` on a column (well-typed) — never on
a parameter.
Also hit a test-architecture limit: `PublicProductControllerTest` (a narrow `@WebMvcTest` slice) can't
`@Import` the real `SecurityConfig` to verify its `permitAll` rule, since `SecurityConfig` is
package-private in a different package (`ma.sana3.adapter.web.security`). Scoped that test to controller
logic only (`@AutoConfigureMockMvc(addFilters=false)`) and verified the actual security rule — and the PII
exclusion, filters, pagination, and 404 handling — via a live smoke test against the running containerized
backend instead.
Document-first updates: docs/security-sana3-ma.md (formalizes the public artisan directory PII decision),
docs/database-sana3-ma.md (V3 schema, index/access-pattern notes), docs/architecture-sana3-ma.md (API
table) — the latter two also backfill Sprint 1's logout endpoint and Batch 12's write endpoints, which were
missed when those batches shipped.
Committed as 909cc9c.

## BATCH 14 2026-07-10 — product image upload and serving
`POST /api/v1/artisan-profiles/me/products/{id}/image` (multipart, ARTISAN-owned) and public
`GET /api/v1/products/images/{filename}`. Storage modeled as a domain port (`ImageStorage`), implemented by
`LocalDiskImageStorage` in adapter-web — content-type allowlisted (jpeg/png/webp, SVG deliberately excluded
as an XSS vector) at the application layer before touching disk; stored filenames are always
server-generated (random UUID + extension from the *validated* content type, never the client's filename);
path-traversal-safe resolution on both write/delete and serve.
Removed the raw client-supplied `imageUrl` field from `UpsertProductRequest`/create+update commands — it
was dead scaffolding from Batch 12 that nothing validated or connected to real storage; leaving it after
building the real mechanism would let a client set an arbitrary disconnected URL. Update now preserves the
existing image on a text-only edit.
Real bug caught by the live smoke test, not any unit test: the backend container has run as non-root since
Batch 8, and `/app` (WORKDIR) is root-owned, so `Files.createDirectories("uploads")` failed with
`AccessDeniedException` on boot — the app never started. Fixed by creating and chowning `/app/uploads` in
the Dockerfile before `USER app`. Full upload→store→serve round trip verified against the running
containerized backend (byte-for-byte content match, correct Content-Type, public no-auth access, unsupported
type correctly rejected with 400).
Document-first updates: docs/architecture-sana3-ma.md (API table), docs/security-sana3-ma.md (upload attack
surface + mitigations), docs/devops-sana3-ma.md (Dockerfile snippet + the non-root/uploads-dir gotcha, so
the next Dockerfile change doesn't reintroduce it).
Committed as a7dd7b3.

## BATCH 15 2026-07-10/11 — catalog NgRx slice + "My Products" UI (Story 3.4)
New `/profile/products` route (artisanGuard-protected): combined add/edit form + a card list of the
artisan's own products (Edit/Delete/per-product image upload). Built by mirroring the artisan-profile
feature's NgRx layering exactly, extended for a list resource instead of a single record — create appends,
update/image-upload replace the matching entry by id, delete filters it out.
Real bug caught by the live smoke test, not any unit test: the Docker build regenerates
`core/api.config.ts` *entirely* from a build ARG (Batch 7) — any other export added to that file is
silently dropped. My first attempt exported `API_ORIGIN` from it (needed to turn the API's relative image
paths into absolute `<img src>` URLs); built fine under `ng serve` (reads the source as-is) but failed the
actual Docker build. Fixed by moving it to a new `core/api-origin.ts` that imports `API_BASE_URL`, and
documented the constraint directly in `api.config.ts`'s comment so a future batch doesn't rediscover it the
hard way.
Live-tested end-to-end against the full containerized stack: register artisan → profile → `/profile/products`
empty state → add product (reflected in list) → edit product (price change persisted) → direct navigation
to `/profile/products` (SPA fallback + guard both correct). Image upload and delete are covered by unit
tests (mocking the `File` object and `window.confirm` respectively) but not re-verified live in-browser —
the browser tool's file-upload capability wasn't available this session, and delete's native `confirm()`
dialog would have blocked further browser automation; the backend upload endpoint itself was already
verified byte-for-byte in Batch 14.
80 frontend tests (was 54), build/lint/test all clean. Updated docs/ux-sana3-ma.md's site map — also
corrects a stale Sprint-1 sketch of a `/profile/edit` sub-route that was never actually built (Story 2.3
combined view+edit into one screen).
Committed as b86d877.

## BATCH 16 2026-07-11 — public browse/search UI + product detail page (Stories 4.4-4.5)
New public routes `/browse` (filterable grid — craftType/region/price-range/keyword/pagination) and
`/products/:id`, neither guarded, matching Batch 13's already-public backend endpoints. Extended the
existing `catalog` NgRx slice rather than adding a new feature — per the Sprint 2 plan's own technical note
(shared product model, separate browse/mine/detail selectors on one state tree).
Real bug caught by a genuine failing test (not written into the test to prove a point — it just failed):
`searchProducts$` passed the whole search *action* straight to `CatalogService.searchProducts()`, leaking
NgRx's `type` field into the HTTP call as a query param. Fixed by destructuring only the filter fields,
matching every other effect in this slice.
Live-tested against the full containerized stack, with data accumulated across this session's earlier
batches still live in the persistent postgres volume: `/browse` lists products across artisans, the
craftType filter narrows correctly (confirmed via network request inspection after two flaky click misses —
browser-automation coordinate flakiness, not an app bug), direct navigation to `/products/:id` renders full
detail + artisan summary, an unknown id shows the not-found prompt, and one product's image (uploaded live
back in Batch 14/15 testing) actually rendered in the grid — real end-to-end proof the upload→browse chain
works, not just each piece in isolation.
101 frontend tests (was 80), build/lint/test all clean. Updated docs/ux-sana3-ma.md's site map.
Committed as f7a4ff1.

## BATCH 17 2026-07-11 — persist product images with a named docker volume
Added `sana3-product-images`, mounted at `/app/uploads` in the backend container — until now `UPLOAD_DIR`
(Batch 14) only wrote to the container's ephemeral filesystem, so any uploaded image was lost the moment
the container was recreated. `UPLOAD_DIR` is hardcoded in docker-compose.yml (not `${...:-}` templated
like the host ports) since it must stay in lockstep with both the Dockerfile's precreated/chowned directory
and the volume mount target — not something meant to be user-configurable.
Verified live, not just assumed correct: uploaded an image, fully removed and recreated the backend
container (`docker compose rm -f backend && up -d backend`, not just a restart), confirmed the pre-existing
image still served correctly (200) and a fresh upload afterward still succeeded — the non-root `app` user's
ownership from Batch 8/14 survives the volume remount since Docker copies the mount point's existing
ownership into a fresh named volume on first use.
Updated docs/devops-sana3-ma.md's infrastructure section.
Committed as 54786e3.

## BATCH 18 2026-07-11 — VERIFY: coverage + security scan (Sprint 2)
Combined backend+frontend line coverage 89.7% (`scripts/check-coverage.sh`, reused unchanged from Sprint 1
Batch 9) — comfortably clears the 80% gate, no new tests needed. Security scan (Semgrep, Trivy SCA on
Maven/frontend-npm/e2e-npm, Trivy image scan on both containers, Gitleaks on full history): **clean across
every scanner on the first run**, no fixes required — unlike Sprint 1's Batch 8, which found and fixed 3
real issues. Sprint 2's new attack surface (image upload, public browsing) had already been scanned
incidentally as each batch shipped it (Batch 8's `apk upgrade --no-cache` pattern also kept both images'
base packages current the whole way through).
Full numbers in .logs/metrics.md "BATCH 18" entry. No code changes this batch — verify-only.

## PUSH 2026-07-11
Branch: main | Commits: 16 (2856105..6735056) — Batches 11-18 (catalog domain, product CRUD, public
browsing/search, image upload, catalog frontend UI, browse/search UI, image volume persistence, VERIFY).
CI run 29157209571 triggered automatically: **all 5 jobs green on the first run** (Lint 24s, Security Scan
56s, Test+Coverage Gate 1m40s, Build Docker Images 2m14s, Deploy to Staging 3s). No red-CI diagnose/fix
cycle needed — the existing Sprint 1 CI pipeline (Batch 9) required zero changes for Sprint 2's new surface
(catalog domain, image upload, public endpoints all just fell out of the same lint/test/security-scan/build
jobs).

## BATCH 20 2026-07-11 — SHIP (Sprint 2)
Added e2e/tests/catalog-flows.spec.ts covering all 9 of Sprint 2's stories in one continuous session:
register+profile → add product → edit product → public browse + craftType filter → product detail with
artisan summary → unknown-id not-found state → delete (native `confirm()` auto-accepted via
`page.on('dialog')`) removes the product from both the owner's list and public browsing. Passed first try
against the full docker-compose stack. Also ran the full e2e/ suite (this file + Sprint 1's
critical-flows.spec.ts, unmodified) together — both green, confirming Sprint 2 introduced no regressions to
auth/profile. Video saved to `.recordings/v0.2-2026-07-11.webm` (gitignored, local only — path noted here
since it won't be in git history). Added a Release Gate Criteria section to
docs/stories-sana3-ma-sprint2.md mirroring Sprint 1's format.

## RETRO 2026-07-11 — Sprint 2 (Batches 11-20)
**Delivered**: full product catalog (artisan self-service CRUD + image upload) and public browsing/search
(filterable listing + detail page), Epics 3-4 in full, nothing cut. Backend: new `catalog` bounded context
(domain/application/adapter-persistence/adapter-web) mirroring `artisanprofile`'s exact hexagonal layering,
a cross-aggregate read path (batch-fetching artisan summaries to avoid N+1), local-disk image storage
behind a domain port. Frontend: catalog NgRx slice extended for both owner (`/profile/products`) and public
(`/browse`, `/products/:id`) views on one state tree. Docker: persistent image volume. CI: zero changes
needed for any of it.

**What deviated from the plan, and why**:
- The 4 YAGNI defaults stated upfront in the Sprint 2 PLAN (public no-login browsing, self-publish/no
  moderation, single local-disk image, free-text craft-type category) all held through to shipping
  unchanged — no story forced a reconsideration.
- Batch 12 reused `NotAnArtisanException`/`ProfileNotFoundException` across the catalog and artisanprofile
  bounded contexts rather than duplicating them — same precondition applies to both, and duplicating would
  have been the premature-abstraction-avoidance principle applied backwards.
- Batch 13's cross-aggregate composition (product + its artisan's public summary) went in the application
  layer via batch-fetch, not a persistence-layer join for the *read model* — but the *filter* query itself
  (region lives on `artisan_profiles`, not `products`) genuinely needed a JPQL join, which surfaced a real
  Postgres/Hibernate bug (`LOWER(null)` inferred as `bytea`) caught by a failing persistence test, not
  written into one.
- Batch 14 removed Batch 12's client-supplied `imageUrl` field once real upload storage existed — dead
  scaffolding that would have been actively misleading (and a minor integrity risk) to leave in place.

**What went well**: the "every batch ends with a live smoke test" practice from Sprint 1 kept paying off —
it caught three real bugs unit tests structurally couldn't have caught (Batch 14's non-root container
couldn't write to its upload dir; Batch 15's Docker build silently drops any export added to
`api.config.ts`; Batch 17's volume persistence needed proving via full container recreation, not a
restart). Security scanning integrated into each batch as it shipped (rather than deferred to one big
end-of-sprint pass) meant Batch 18's VERIFY was clean on the first run, with zero fixes needed — a real
contrast to Sprint 1's Batch 8, which found and fixed three issues at that late stage. CI required zero
workflow changes across all ten batches.

**Carried forward into Sprint 3**: geo-radius search ("artisans near me") is explicitly flagged as an Open
Question in docs/stories-sana3-ma-sprint2.md, not started — `artisan_profiles.location` (PostGIS, enabled
since Sprint 1) is still unpopulated, so there's nothing to search against yet; revisit once some feature
actually captures artisan location. The stateless-JWT revocation limitation from Sprint 1 remains accepted
and unchanged.

**Not done / explicitly deferred** (YAGNI, matches the PRD's own scope split, not oversight): cart, orders,
checkout, payment, QR certificates, DHL export, cooperative multi-user accounts — all still explicitly
future-sprint scope.

## PLAN 2026-07-11 — Sprint 3 backlog (orders & checkout)
Full backlog written to docs/stories-sana3-ma-sprint3.md (Epic 5: Cart & Checkout, Epic 6: Order Visibility
& Fulfillment — 8 stories). Chosen from the PRD's remaining "Out of Scope (future sprints)" list (QR certs,
orders/checkout/payment, DHL export, cooperative accounts) based on three pieces of evidence already in the
docs, not just inference: ADR-1 explicitly named "orders" as an anticipated bounded context alongside
catalog (done) and certification; the PRD's user stories already describe buyers wanting to "browse and
order"; and Sprint 2's own stories doc left itself a note on product hard-delete — "revisit when Sprint 3
orders exist" — meaning Orders was already the assumed next sprint while Sprint 2 was being written.
Split payment out of scope deliberately: the PRD bundles "Orders, checkout, CMI/Stripe payment" as one
bullet, but real payment gateway integration (external merchant accounts, sandbox/production credentials,
PCI-adjacent review) is materially different work from building the order lifecycle — Sprint 3 builds the
full checkout flow ending in a placed order with no real payment step (closest real-world equivalent: cash
on delivery), leaving gateway integration as an explicit Open Question for a later sprint. Five YAGNI
defaults stated for other gaps: client-side-only cart (no backend cart table), order items snapshot product
details rather than live-joining `products` (this is what actually resolves Sprint 2's deferred hard-delete
note), any authenticated user (not just BUYER) can place an order, a simple 3-state order status
(PLACED/COMPLETED/CANCELLED, not granular shipping tracking — that's what a later DHL-export sprint would
actually need), and a one-off free-text shipping address (not a reusable address book).

Batch breakdown (continuing numbering from Sprint 2's B11-B20):
B21 backend orders domain scaffold (Order/OrderItem entities, repository port, Flyway V4 — orders +
    order_items tables) — mirrors Batch 11's catalog scaffold pattern
B22 backend checkout (POST /api/v1/orders — place order from cart items, server-computed total, product
    snapshot, per-line validation) — Story 5.2
B23 backend order history + status transitions (buyer GET/cancel, artisan GET/complete on their own
    order_items) — Stories 6.1-6.3
B24 frontend cart NgRx slice + UI (localStorage-backed, no backend cart) — Story 5.1
B25 frontend checkout UI (review + place order + confirmation) — Story 5.3
B26 frontend order history UI (buyer /orders + artisan /profile/orders) — Stories 6.4-6.5
B27 VERIFY: coverage (JaCoCo+Vitest) >=80%, security scan (Semgrep/Trivy/Gitleaks)
B28 CI: push, monitor+fix until green (existing pipeline from Sprint 1 Batch 9, no changes expected)
B29 SHIP: Playwright E2E (cart, checkout, order history, artisan fulfillment), video recording
    (.recordings/v0.3-[date].webm), sprint retro, final push, SESSION_END

No dedicated docker-compose/infra batch this sprint (unlike Sprint 2's B17) — orders introduce no new
external service or volume, just new tables in the existing Postgres.

Not yet user-confirmed — this is the PLAN artifact for review before EXECUTE starts (project rule 5).

## BATCH 21 2026-07-11 — backend orders domain scaffold
User confirmed the Sprint 3 plan ("ok lets start") — EXECUTE begins. New `ma.sana3.domain.order` package:
`Order` and `OrderItem` entities plus `OrderRepository`/`OrderItemRepository` ports, mirroring the
`catalog`/`artisanprofile` contexts' exact style (final immutable entities, static factory methods,
behavior methods returning new instances, equals/hashCode by id only). `Order.place`/`.cancel`/`.complete`
guard status transitions domain-side, throwing `IllegalOrderStatusTransitionException` outside `PLACED`.
`OrderItem` snapshots `productName`/`priceAmount`/`priceCurrency`/`craftType` at order time (nullable
`productId`, `ON DELETE SET NULL`) rather than live-joining `products` — this is what actually resolves
Sprint 2's deferred product-hard-delete note. `artisanProfileId` is denormalized onto each line item
(not derived via `productId`) so an artisan's fulfillment queue survives product deletion and each line
can be completed independently per-artisan on a multi-artisan order.
Flyway V4 migration: `orders` + `order_items` tables, indexes on `buyer_user_id`, `order_id`, and
`artisan_profile_id`.
13 new domain tests (6 `OrderTest`, 7 `OrderItemTest`), all green. `mvn verify` green across all 5 modules,
including adapter-persistence's Testcontainers suite — confirms V4's SQL is valid.
Document-first update: docs/database-sana3-ma.md (orders/order_items schema, indexes, access patterns, new
`shipping_address` PII note flagging it should stay out of the artisan-facing fulfillment DTO planned for
Batch 23).
Committed as 6ce2b3e.

## BATCH 22 2026-07-11/12 — checkout: place an order (Story 5.2)
`POST /api/v1/orders`, open to any authenticated role (Assumed Default #3 — an artisan can buy another
artisan's product). New `ma.sana3.application.order` package mirroring `catalog`'s hexagonal layering:
`PlaceOrderHandler` resolves each cart line's *current* product via `ProductRepository.findById` (never
trusting a client-supplied price/name/total), snapshots it onto a new `OrderItem`, and computes the total
server-side — grouped per currency (`List<OrderTotal>`), not a single blended total, per Sprint 3's stated
multi-currency Open Question.
Technical decision: `Order.place` + each `OrderItem.create` are saved inside one `@Transactional` handler
method — the first genuinely multi-repository write in this codebase (every prior handler wrote to exactly
one repository), so a mid-checkout DB failure can't leave a placed order with zero items. This required
adding `spring-tx` as an explicit `application` module dependency (previously only pulled in transitively
through `bootstrap`); version is managed by the existing `spring-boot-starter-parent` BOM, no new version to
pin.
A missing/deleted product referenced by a cart line is rejected with a per-line `PRODUCT_NOT_FOUND` error
that names the product id (added an overloaded `ProductNotFoundException(UUID)` constructor — existing
no-arg call sites in the catalog context are unaffected).
16 new tests (3 application Mockito, 8 adapter-persistence Testcontainers across two repository adapters —
including one proving `order_items.product_id` survives the referenced product being hard-deleted, which is
the whole point of the snapshot pattern — and 4 adapter-web `@WebMvcTest`), all green. Fixed a Hibernate
Validator deprecation warning surfaced during the first test run (`@Valid` on a `List` field instead of its
type argument) by switching to `List<@Valid OrderLineItemRequest>`.
VERIFY: live smoke test against the full docker-compose stack (not just tests) — registered an artisan +
buyer, created a product, placed a 3-quantity order and confirmed the response's snapshot fields and
server-computed total (300.00 MAD) matched the product's *current* price; confirmed 404 `PRODUCT_NOT_FOUND`
on an unknown product id (with the id in the message), 400 `VALIDATION_FAILED` on a blank shipping address
and on an empty items list, 401 on an unauthenticated request, and 201 for an artisan buying another
artisan's product (Assumed Default #3).
Document-first update: docs/architecture-sana3-ma.md (API table).
Committed as 9dd57fa.

## BATCH 23 2026-07-11/12 — order history, cancellation, artisan fulfillment (Stories 6.1-6.3)
Buyer: `GET /api/v1/orders/me` (list), `GET /api/v1/orders/me/{id}` (detail — 404 not 403 on someone else's
order, same no-info-disclosure pattern already used elsewhere), `POST /api/v1/orders/me/{id}/cancel`
(domain-guarded PLACED->CANCELLED transition).
Artisan: `GET /api/v1/artisan-profiles/me/orders` lists `order_items` across every order containing the
artisan's own products, batch-fetching the parent orders and buyers to avoid N+1 (mirrors Sprint 2 Batch
13's artisan-summary batch-fetch). Story 6.3's AC explicitly requires buyer contact + shipping info for
fulfillment, so this response deliberately includes `buyerEmail` and `shippingAddress` — a real, intentional
PII exposure, documented in docs/security-sana3-ma.md rather than left implicit. `POST .../{id}/complete`
marks one line item fulfilled, same ownership check (`order_items.artisan_profile_id`).
Domain cleanup: `OrderItem.complete()` now throws a dedicated `OrderItemAlreadyCompletedException` instead
of a bare `IllegalStateException`, matching `Order`'s own `IllegalOrderStatusTransitionException` pattern
(B21's `OrderItemTest` updated to match — still a `RuntimeException` subtype, no other call sites affected).
Both `OrderRepository` and `UserRepository` gained `findByIds(Collection<UUID>)`, free via Spring Data's
`findAllById` — same pattern as Batch 13's `ArtisanProfileRepository.findByIds`.
Real gap found during live smoke testing, fixed same-session: a buyer could cancel an order even after an
artisan had already completed one of its line items, because the domain only guarded the *order's* own
status, not its items'. Flagged to the user rather than silently deciding — user chose to block it.
`CancelMyOrderHandler` now checks its order_items first and rejects with 409 `ORDER_HAS_COMPLETED_ITEMS` if
any is already fulfilled; re-verified live against the dockerized stack after the fix.
27 new tests (5 application handlers x Mockito incl. the new cancel-guard case, 2 adapter-persistence
`findByIds` cases, 2 adapter-web controllers incl. the new `ArtisanOrderController`), all green — full `mvn
verify` across all 5 modules green (58 adapter-web tests total).
VERIFY: full live smoke test against docker-compose — buyer list/detail/cancel, artisan list (confirmed
`buyerEmail`/`shippingAddress` visible) and complete (200, then 409 on a repeat), a second buyer 404'd on
someone else's order, a buyer 403'd on the artisan-only list endpoint, and the cancel-guard fix specifically
re-verified after rebuilding the container.
Document-first updates: docs/architecture-sana3-ma.md (API table), docs/database-sana3-ma.md (§7 revised
now that shipping_address/buyer email are intentionally artisan-visible), docs/security-sana3-ma.md (new
artisan-fulfillment PII section).
Committed as 36248ca.

## BATCH 24 2026-07-12 — cart NgRx slice + UI, localStorage-backed (Story 5.1)
New client-only `cart` feature slice, no backend endpoint (Assumed Default #1) — addItem/updateQuantity/
removeItem/clearCart, each entry snapshotting the product as shown at add-to-cart time so the cart still
renders sensibly if a product changes or is deleted before checkout; adding an already-present product
merges quantity instead of duplicating the row. `selectCartTotalsByCurrency` mirrors the backend's
per-currency order totals (Batch 22's `OrderTotal` grouping), not a single blended total. Persistence via a
`cartLocalStorageMetaReducer` registered on `provideStore`'s `metaReducers`, hydrating the cart slice from
localStorage on the first dispatch and writing it back after every change.
New `/cart` route (quantity edit, remove, per-currency totals, empty-state prompt). "Add to Cart" wired
into `/browse` (fixed quantity 1 per card) and `/products/:id` (quantity field). Toolbar gained a Cart link
with a live item-count badge.
Real bug found during live testing (not caught by unit tests, which mocked the reducer boundary rather
than exercising NgRx's real init sequence), fixed same-session: the meta-reducer's hydration guard was
`state && { ...state, [cartFeatureKey]: readFromStorage() }`, but NgRx calls the root reducer with
`state: undefined` on its very first-ever dispatch — `undefined && ...` short-circuits to `undefined`, so
hydration silently never applied, and the reducer's own empty default got written straight back over
whatever was actually in localStorage. Fixed by running the wrapped reducer first (always returns a
fully-defaulted state) and overriding just the cart slice on that result. Caught by manually driving the
running dev-server app rather than trusting the unit tests alone — browser-automation clicks (via
coordinates or accessibility refs) intermittently failed to register with Angular's click bindings during
this session (a tooling limitation noted before, e.g. Batch 16's "coordinate flakiness"), so verification
used direct DOM `.click()`/`dispatchEvent` calls via the JS console instead, which reproduced the bug
reliably: add item -> full reload -> item silently vanished. Re-verified after the fix: add -> full reload
-> item persists; remove -> full reload -> empty state persists; adding the same product twice merges into
one row at quantity 2, and that merged state also survives a reload.
Existing `cart.storage.spec.ts` tests were updated to call the meta-reducer with `state: undefined` on the
first dispatch (matching NgRx's real bootstrap behavior) rather than a pre-populated state object, which is
what let the original bug slip past the test suite in the first place.
121 frontend tests (was 101), build/lint/test all clean.
Document-first update: docs/ux-sana3-ma.md site map.
Committed as 7921cb8.

## BATCH 25 2026-07-12 — checkout UI: review, place order, confirmation (Story 5.3)
New `/checkout` route guarded by a new generic `authGuard` (any authenticated role — Assumed Default #3),
distinct from the existing ARTISAN-only `artisanGuard`. New `order` NgRx feature slice scoped to just this
batch's need (`placeOrder`) — list-my-orders is deferred to Batch 26 (Story 6.1) rather than built ahead of
the story that needs it, the same incremental-slice pattern Sprint 2 used for `catalog` across Batches 15-16.
`OrderEffects.placeOrder$` posts the cart's current items (mapped to `productId`/`quantity` only — price and
name are never sent, matching Batch 22's server-side-recompute contract) to `POST /api/v1/orders`. A
separate `clearCartOnPlaceOrderSuccess$` effect dispatches `CartActions.clearCart()` only after a real
success, so a per-line rejection (Story 5.2's `PRODUCT_NOT_FOUND`) leaves the cart untouched for the buyer
to fix and retry.
Checkout page: review list + per-currency totals + shipping address form; on success shows a confirmation
with the order id in place of the form; dispatches `OrderActions.resetPlaceOrderState()` on mount so a stale
confirmation/error from a previous visit never leaks into a fresh cart's checkout. Cart page gained the
"Proceed to Checkout" link deferred from Batch 24.
19 new/changed frontend tests (order reducer, order effects incl. the cart-clearing effect, checkout
component, authGuard, cart's new checkout link), build/lint/test all clean.
VERIFY note: browser-automation flakiness continued this session (see Batch 24) — the first browser tab
became entirely unresponsive mid-login (CDP calls timing out) for reasons unrelated to the app; opened a
fresh tab and continued there without issue, consistent with a tooling problem rather than an app bug.
Full live verification against the dockerized stack in the fresh tab: logged in as a real buyer, added a
product to cart, checked out, got a real confirmation with an order id, confirmed the cart cleared in both
the NgRx store and localStorage, and independently verified the placed order via
`GET /api/v1/orders/me/{id}`. Then specifically tested the per-line-rejection AC: added a product to cart,
deleted that product from the backend (simulating "changed since it was added to the cart"), submitted
checkout, and confirmed the backend's exact message ("No product found for id ...") rendered on the page
instead of a generic failure, with the cart left untouched afterward. Also confirmed an unauthenticated
visit to `/checkout` redirects to `/login?returnUrl=%2Fcheckout`.
Document-first update: docs/ux-sana3-ma.md site map.
Committed as a22bfe5.

## BATCH 26 2026-07-12 — order history UI: buyer /orders + artisan /profile/orders (Stories 6.4-6.5)
Extended the `order` NgRx slice from Batch 25 with list/cancel (buyer) and list/complete (artisan) actions,
reducers, and effects — built only when this batch's stories actually needed them, same incremental pattern
as every prior slice extension this sprint.
Buyer `/orders` (any authenticated role, existing `authGuard`): each order card shows status/items/total
with a Cancel action on `PLACED` orders; a snackbar surfaces the backend's exact rejection message (e.g.
Batch 23's `ORDER_HAS_COMPLETED_ITEMS`) instead of a generic failure. Artisan `/profile/orders`
(`artisanGuard`, alongside `/profile/products`): lists `order_items` across all orders containing the
artisan's own products, showing buyer email + shipping address (Batch 23's intentional PII exposure) with a
Mark completed action; completed items show a badge instead of the button.
Real backend gap found live while verifying this batch, symmetric to Batch 23's cancel-guard fix: an artisan
could mark an order item COMPLETED even after the buyer had already cancelled the whole order —
`CompleteArtisanOrderItemHandler` only checked item-level ownership/completion, never the parent order's
status. Flagged to the user rather than silently deciding — user chose to fix it. Handler now fetches the
order before completing the item and rejects with 409 `ORDER_CANCELLED` if it's `CANCELLED`. Fixed and
committed separately (`fix(backend)`, not bundled into this batch's `feat(frontend)` commit) since it's a
backend correctness fix, not a frontend feature; re-verified via curl (before: 200, after: 409) and again
from the artisan orders UI itself (clicking "Mark completed" on a cancelled order's item now correctly
leaves it uncompleted).
24 new/changed frontend tests, plus 2 new backend tests (handler + controller) for the cancel-guard fix —
full `mvn verify` and `ng test`/`lint`/`build` all clean.
VERIFY: full live smoke test against the dockerized stack in a fresh browser tab — buyer with two placed
orders cancels one (flips to CANCELLED, action disappears, other order untouched); artisan sees both
order_items with correct buyer/shipping info, completes one (200, badge appears); then the cancelled-order
completion gap found and re-verified as above.
Document-first update: docs/ux-sana3-ma.md site map.
Committed as 96b4607 (backend fix) and b24fe0b (frontend feature).

## BATCH 27 2026-07-12 — VERIFY: coverage + security scan (Sprint 3)
Combined backend+frontend line coverage 91.1% (`scripts/check-coverage.sh`, unchanged since Sprint 1 Batch
9) — comfortably clears the 80% gate. Caught a reporting gotcha while measuring it: `ng test --coverage`
without the exact `--coverage-reporters json-summary --coverage-reporters text` flags `ci.yml` uses only
writes `coverage-final.json`, not the `coverage-summary.json` the check script reads — it fails silently
(a WARNING, not an error) and reports a backend-only number that still happens to clear 80%, which could
mask a real frontend regression in a less comfortable scenario. Re-ran with CI's exact invocation to get the
real figure; no script change needed, just noted for next time.
Security scan: Semgrep (0 findings), Gitleaks (0 secrets, 55 commits), Trivy SCA on frontend npm (0
Critical/High), Trivy image scans on both freshly-built images (0 Critical). Backend Maven SCA (Trivy fs)
was blocked locally by a Maven Central 429 rate-limit on this session's IP (30-minute block, triggered by
this session's own heavy `mvnw` usage across many batches) — not a security finding; deferred to Batch 28's
CI run, which executes on a different network and isn't subject to the same local throttling.
Full numbers in .logs/metrics.md "BATCH 27" entry. No code changes this batch — verify-only.

## PUSH 2026-07-12
Branch: main | Commits: 15 (8450d83..432c627) — Batches 21-27 (orders domain, checkout, order
history/cancellation/artisan fulfillment, cart, checkout UI, order history UI, VERIFY).
CI run 29202793281 triggered automatically: **all 5 jobs green on the first run** (Lint 30s, Security Scan
1m7s, Test+Coverage Gate 1m51s, Build Docker Images 1m53s, Deploy to Staging 2s — ~4m25s total). No red-CI
diagnose/fix cycle needed. This also resolves Batch 27's deferred item — the backend Trivy SCA scan that was
blocked locally by a Maven Central rate-limit ran clean on CI's network (0 Critical/High), confirming the
local block really was a network throttling artifact and not a masked finding.

## BATCH 29 2026-07-13 — SHIP: Sprint 3 E2E suite + video recording (rule 9)
New `e2e/tests/order-flows.spec.ts`, one continuous Playwright session covering Stories 5.1-5.3 and
6.1-6.5: add-to-cart with a chosen quantity, cart quantity edit, checkout + confirmation with a real order
id and cart clearing, buyer order history + cancel, a second order to drive the fulfillment step, cart
surviving a logout/login (proves it's client-side/localStorage-backed, not session-bound — Batch 24), a
per-line rejection at checkout (product deleted after being added to cart) rendering the backend's exact
message (Batch 25's AC), and artisan fulfillment (buyer email + shipping address visible per Batch 23's
intentional PII exposure, mark completed).
Two real bugs found in the test itself (not the app) while getting it green against the dockerized stack:
a login not awaited before the next step's `page.goto()` aborted the in-flight silent-refresh request
(access token is memory-only by design per docs/security-sana3-ma.md, so every full navigation re-runs
bootstrap auth — navigating away mid-request left the buyer unauthenticated for the next step); and a
duplicate-toast strict-mode violation asserting on the transient "Product added" snackbar twice in a row
while the first was still closing — fixed by asserting on the resulting product card for the second add.
Also fixed Sprint 2's `catalog-flows.spec.ts`, which had started failing for an unrelated reason: this
session's accumulated test data means `/browse`'s default page no longer reliably contains a
freshly-created product. Filtered by the run-unique product name via the existing Search field, same
pattern now used in `order-flows.spec.ts`.
Verified: `order-flows.spec.ts` passes alone, and all three e2e specs (critical-flows, catalog-flows,
order-flows) pass together with no cross-sprint regressions. Video saved to
`.recordings/v0.3-2026-07-13.webm` (gitignored, local only, per rule 9 and the existing binary-artifacts
convention).
Document-first: updated docs/test-strategy-sana3-ma.md's release gate checklist with Sprint 3 evidence
(coverage 91.1%, CI run 29202793281, this batch's recording) — noted as a retroactive fix that Sprint 2
shipped without this checklist being re-touched at the time; closed the gap rather than leaving it stale.
Committed as 19438b3.

## RETRO 2026-07-13 — Sprint 3 complete (Batches 21-29)
Shipped: full order lifecycle — client-side cart (Story 5.1), checkout/place-order with server-side price
recompute (5.2), checkout UI (5.3), buyer order history + cancellation (6.1/6.2/6.4), artisan order
visibility + fulfillment with intentional buyer PII exposure (6.3/6.5). Epics 5-6 complete, nothing cut
from docs/stories-sana3-ma-sprint3.md's planned backlog. Payment gateway integration explicitly stayed out
of scope per the sprint's own scope-boundary note (Assumed Default #4) — orders ship as PLACED with no
payment step, equivalent to cash-on-delivery.
What deviated from plan: two backend correctness gaps were found only via live smoke-testing, not unit
tests, and fixed same-session after flagging to the user rather than silently deciding — Batch 23's
cancel-guard (a buyer could cancel an order after an artisan had completed one of its items) and Batch 26's
symmetric gap (an artisan could complete an item on an order the buyer had already cancelled). Both are now
guarded server-side with explicit 409s (`ORDER_HAS_COMPLETED_ITEMS`, `ORDER_CANCELLED`).
What went well: the incremental-NgRx-slice pattern from Sprint 2 (build only what the current story needs)
carried over cleanly across cart -> checkout -> order-history; continuous security reasoning per-batch again
left Batch 27's scan clean (0 findings) with no fix cycle. A real hydration bug in the cart's localStorage
meta-reducer (Batch 24 — `undefined && ...` short-circuiting on NgRx's real first dispatch) was caught only
by manually driving the running app, not the unit tests that had mocked the reducer boundary; the test was
then fixed to match NgRx's real bootstrap behavior so it can't regress silently again.
Process gap found and closed this batch: docs/test-strategy-sana3-ma.md's release gate checklist was last
touched at Sprint 1 Batch 10 and never re-verified during Sprint 2's SHIP — Sprint 2 shipped correctly (own
evidence exists in .logs/metrics.md and .recordings/) but the doc itself went stale. Updated now with
Sprint 3 evidence and a note explaining the gap; worth remembering to re-touch this doc every sprint's SHIP
going forward, not just Sprint 1's.
Carried forward: no open Sprint 3 stories. Remaining PRD "Out of Scope (future sprints)" backlog: real
CMI/Stripe payment gateway integration (explicitly split out of this sprint, see Assumed Default #4),
QR-authenticated craft certificates, DHL export integration, cooperative multi-user accounts. Geo-radius
search ("artisans near me") also still carried from Sprint 2, still blocked on nothing populating
`artisan_profiles.location`.
Coverage 91.1%, security scan clean (0 Critical/High across Semgrep/Trivy/Gitleaks), CI green on the first
run for every batch this sprint (Batches 21-28), E2E all three specs green together.

## PUSH 2026-07-13
Branch: main | Commit: 901fe19 (Batch 29 logs — E2E suite, retro, session end, test-strategy checklist
update). CI run 29247751687 triggered automatically: **all 5 jobs green on the first run** (Lint 25s,
Security Scan 48s, Test+Coverage Gate 1m58s, Build Docker Images 1m56s, Deploy to Staging 2s). Sprint 3
fully closed out: all batches shipped, CI green, coverage 91.1%, zero open critical/high security findings,
E2E suite (3 specs) green together, video recorded.

## UNDERSTAND 2026-07-13 — Sprint 4 kickoff
User confirmed starting Sprint 4 planning. Re-read docs/prd-sana3-ma.md §"Out of Scope (future sprints)":
remaining bullets are QR-authenticated craft certificates, real CMI/Stripe payment (explicitly split out of
Sprint 3's checkout work — see docs/stories-sana3-ma-sprint3.md Assumed Default #4), DHL export integration,
cooperative multi-user accounts, plus Kubernetes (gated on real scale need, not proposed). Also carried
forward from Sprint 2: geo-radius search, still blocked on nothing populating `artisan_profiles.location`.
No predetermined Sprint 4 scope exists yet — proceeding to BRAINSTORM with the user.

## PLAN 2026-07-13 — Sprint 4 backlog (cooperative multi-user accounts)
Full backlog written to docs/stories-sana3-ma-sprint4.md (Epic 7: Cooperative Membership — 4 stories).
User picked this over the recommended simpler-first option (real payment gateway) during BRAINSTORM.
Researched the current model before writing the plan (fork survey, not guesswork): JWT carries only
sub/email/role, no profile id; `artisan_profiles.user_id` is a hard UNIQUE schema constraint; the exact
same "findByUserId then compare artisan_profile_id" ownership check is duplicated across 9 handlers
(product CRUD/image upload/listing, profile get/update, order-item complete/list); the frontend
`artisanGuard` is a pure role check with no profile-specific logic; docs/ have zero prior scaffolding for
this — genuinely greenfield.
Five Assumed Defaults stated: reuse `artisan_profiles` as the cooperative entity itself rather than a new
parallel `Cooperative` concept; a user belongs to at most one cooperative (UNIQUE(user_id) on the new
membership table, avoiding a multi-cooperative switcher UI); two roles only (OWNER can invite/remove,
both OWNER and MEMBER can manage products/orders/shared profile fields); invites target an existing
ARTISAN account by email and require the invitee's acceptance (no signup-via-invite, no email delivery
infra — in-app only); no ownership-transfer story, an OWNER can't leave while other members exist.
New PII consideration flagged: the members list exposes each member's email to the others (new cross-user
visibility) — proportionate but to be documented in docs/security-sana3-ma.md when built, same treatment
Sprint 3 gave buyer PII. Also flagged an enumeration risk on invite creation (don't distinguish "no such
user" from "not an ARTISAN" in the error).

Batch breakdown (continuing numbering from Sprint 3's B21-B29):
B30 backend membership data model + migration (cooperative_members table, backfill existing 1:1 owners as
    OWNER, drop artisan_profiles.user_id/UNIQUE) — Story 7.1
B31 backend authorization rework across all 9 existing handlers (swap findByUserId call sites for
    membership-based lookup, same downstream ownership checks) — Story 7.2
B32 backend invites (cooperative_invites table, invite/accept/decline, list/remove members) — Story 7.3
B33 frontend cooperative NgRx slice + members management UI (/profile/members, invite form, pending-invite
    banner on login) — Story 7.4
B34 VERIFY: coverage (JaCoCo+Vitest) >=80%, security scan (Semgrep/Trivy/Gitleaks)
B35 CI: push, monitor+fix until green
B36 SHIP: Playwright E2E (invite -> accept -> shared profile/product/order management from both accounts,
    member removal), video recording (.recordings/v0.4-[date].webm), sprint retro, final push, SESSION_END

No dedicated docker-compose/infra batch this sprint — no new external service, just new tables in the
existing Postgres, same as Sprint 3.

Not yet user-confirmed — this is the PLAN artifact for review before EXECUTE starts (project rule 5).

## BATCH 30 2026-07-13 — backend membership data model + migration (Story 7.1)
User confirmed proceeding through all of Sprint 4 without per-batch approval ("just start executing until
b36 dont ask for approval") — EXECUTE begins, batches still individually logged/committed/verified per
project convention, just without stopping to ask.
New `CooperativeMembership` domain entity + `MembershipRole` enum (OWNER/MEMBER) +
`CooperativeMembershipRepository` port in the existing `ma.sana3.domain.artisanprofile` package (per the
plan: this is an ownership-model change to that context, not a new bounded context). Persistence layer
mirrors `ArtisanProfileRepositoryAdapter`'s exact shape (JPA entity, mapper, Spring Data repo, adapter).
Flyway V5 creates `cooperative_members` (UNIQUE(user_id) enforcing Assumed Default #2 at the schema level,
FK to both `users` and `artisan_profiles`, CASCADE on both) and backfills it from every existing
`artisan_profiles` row (owner becomes `role='OWNER'`) in the same migration.
Deliberate sequencing deviation from Story 7.1's AC as written: the AC says this migration also drops
`artisan_profiles.user_id`/its UNIQUE constraint, but doing that now would break the 9 handlers that still
call `ArtisanProfileRepository.findByUserId` (Batch 31's job) — so this batch is purely additive (new table
alongside the untouched old column) and the drop moves to Batch 31, once nothing depends on the old column
anymore. Keeps every commit's `mvn verify` green instead of a broken intermediate state; noted in
docs/database-sana3-ma.md's migration table (V6, attributed to Batch 31) so the plan and reality match.
9 new tests (4 `CooperativeMembershipTest` domain, 5 `CooperativeMembershipRepositoryAdapterTest` incl.
Testcontainers-backed save/find/exists/delete) — all green. Full `mvn verify` across all 5 backend modules
green (Spotless formatting applied once, no logic changes needed).
Document-first update: docs/database-sana3-ma.md (ERD, new table schema, index, migration plan rows V5/V6,
access patterns for resolving a user's cooperative and listing members).
Committed as 819be26.

## BATCH 31 2026-07-13 — authorization rework across 9 handlers (Story 7.2)
Swapped every artisan-only handler's ownership lookup from `ArtisanProfileRepository.findByUserId` (the
old 1:1 assumption) to `CooperativeMembershipRepository.findByUserId(...).map(CooperativeMembership::
artisanProfileId)` — the exact seam identified during PLAN research. 9 call sites: product CRUD
(Create/Update/Delete/UploadImage/ListOwn), profile Get/Update, order-item Complete/List. Five of the nine
(Create/Update/Delete/UploadImage/ListOwnProducts) only ever used `profile.id()`, never any other profile
field, so they no longer need `ArtisanProfileRepository` as a dependency at all — simpler than before, not
just different.
`UpdateArtisanProfileHandler` now does double duty on first profile creation: saves the new
`ArtisanProfile` and, in the same handler call, creates its `CooperativeMembership` row with
`role=OWNER` — this is the one place a membership gets created outside Batch 32's invite flow, matching
Sprint 1's existing "upsert creates on first call" UX with no new user-facing step.
Domain cleanup completing Story 7.1's deferred half (see Batch 30's sequencing note): `ArtisanProfile` no
longer carries a `userId` field at all — removed from the domain entity, JPA entity, entity mapper,
repository port (`findByUserId` gone), web response DTO, and result records. Flyway V6 drops
`artisan_profiles.user_id`/its UNIQUE constraint now that nothing depends on it. This is a real behavior
change worth flagging: the profile API response no longer echoes back a `userId` field at all (previously
always the caller's own id under `/me` anyway) — frontend types will be updated in Batch 33 to match.
23 tests updated (5 handler test suites got an added "member can act too, not just owner" case proving
Story 7.2's actual intent — MEMBER and OWNER have equal product/order/profile-edit access — not just
regression coverage for the refactor). Full `mvn verify` green across all 5 backend modules.
VERIFY note: rebuilt and ran the full dockerized stack — Flyway migrated a real pre-existing database
(already at v4 from Sprints 1-3) straight through v5 and v6 with no manual intervention, confirming the
backfill-then-drop sequence is safe against real data, not just a fresh Testcontainers schema. Live-tested
register -> upsert profile (confirmed a `cooperative_members` row with `role=OWNER` was created) -> create
product -> list own products, all against the running API; confirmed via direct `psql` that
`artisan_profiles.user_id` is genuinely gone from the live schema.
Document-first update: docs/database-sana3-ma.md already anticipated V6 in Batch 30's entry; no further
doc changes needed this batch.
Committed as a245aba.

## BATCH 32 2026-07-13 — invites: create/list/accept/decline, list/remove members (Story 7.3)
New `cooperative_invites` table (Flyway V7) + `CooperativeInvite` domain entity mirroring `Order`'s
status-transition pattern (`accept()`/`decline()` guard against a non-PENDING invite, throwing
`IllegalInviteStatusTransitionException` — same shape as `IllegalOrderStatusTransitionException`).
Six new handlers in `application.artisanprofile`: `InviteMemberHandler` (OWNER-only; validates the invitee
is an existing ARTISAN account, isn't already a member, and doesn't already have a pending invite —
enforcing Assumed Default #4's "accept required" design and the security note's enumeration-safety
requirement by using one shared `InviteeNotEligibleException` for both "no such user" and "not an artisan"),
`ListMyInvitesHandler`, `AcceptInviteHandler` (creates the MEMBER `CooperativeMembership` in the same call —
mirrors how `UpdateArtisanProfileHandler` creates the OWNER membership on first profile save), `DeclineInviteHandler`,
`ListMembersHandler` (batch-fetches member emails via `UserRepository.findByIds`, same N+1-avoidance pattern
as Sprint 2/3's artisan/buyer batch-fetches), `RemoveMemberHandler` (one handler covers both Story 7.3 AC
cases — a MEMBER removing themselves, or an OWNER removing any MEMBER — via a single
`isSelfRemoval || requester.isOwner()` check; an OWNER can never be removed by anyone, self included, per
Assumed Default #5).
New web layer: `CooperativeMemberController` (`/api/v1/artisan-profiles/me/members` — GET list, POST
`/invites`, DELETE `/{userId}`) and `CooperativeInviteController` (`/api/v1/cooperative-invites/me` — GET
list, POST `/{id}/accept`, POST `/{id}/decline`, matching Story 7.3's Technical Notes literally), plus a
`CooperativeExceptionHandler` mapping 7 new exceptions to the right HTTP status (409 for the two
already-X conflicts and the illegal-transition/cannot-remove-owner cases, 404 for not-found, 403 for
not-owner, 400 for the ineligible-invitee case).
35 new tests (2 domain, 21 application across 6 handler suites, 4 persistence incl. Testcontainers, 11 web
MockMvc across 2 controllers) — all green, full `mvn verify` green across all 5 backend modules.
VERIFY: full live smoke test against the rebuilt dockerized stack (Flyway migrated a real running database
straight to v7, no incident). Exercised the complete flow with two real registered users: owner creates
profile -> invites member's email -> member sees the pending invite -> accepts -> members list shows both
with correct roles -> member creates a product on the shared profile -> owner sees that same product
(confirms Batch 31's equal-access rework holds for a real second user, not just the original owner).
Negative-path checks against the live API: member attempting to remove the owner correctly gets 409
CANNOT_REMOVE_OWNER; owner removing the member correctly gets 204; re-inviting that same email afterward
correctly succeeds with a fresh PENDING invite (proves removal doesn't leave a stale pending-invite block).
Document-first updates: docs/database-sana3-ma.md (V7 migration row, index, access pattern), and
docs/security-sana3-ma.md §4 (superseded the stale "profile.userId == authenticatedUser.id" line from
Sprint 1 with the membership-based model, added the OWNER/MEMBER permission-tier note) and §5 (new PII
consideration: members list exposes peer emails; invite-creation enumeration-safety note).
Committed as 7516eb1.

## BATCH 33 2026-07-13 — frontend cooperative NgRx slice + members UI (Story 7.4)
New `cooperative` NgRx feature slice (state/actions/reducer/selectors/effects) mirroring every prior
sprint's incremental-slice pattern — built only for what this story needs (members list, invite, remove,
my-invites, accept/decline), same shape as `order`'s Batch 25/26 growth. `CooperativeService` wraps the six
Batch 32 endpoints. `reloadMembersAfterInvite$` effect re-fetches the members list after a successful invite
so a freshly-sent invite doesn't require a manual page refresh to reflect state (there's nothing to show
yet since the invite is pending, but this keeps the list authoritative rather than optimistically patching it).
New `/profile/members` route (existing `artisanGuard`) + `CooperativeMembers` page: invite form visible only
to the OWNER (`isOwner` computed by finding the current user's own row in the members list — no separate
"am I owner" endpoint needed), a Remove/Leave action gated by `canRemove()` (owner can remove any MEMBER,
a MEMBER can remove themselves, nobody can act on the OWNER row) — mirrors Batch 32's backend
`RemoveMemberHandler` logic exactly at the UI layer.
Pending-invite banner (Story 7.4's AC — "somewhere they'll actually notice it, not buried in a settings
page") lives in the root `App` component, not the members page: an `effect()` dispatches `loadMyInvites`
whenever an authenticated ARTISAN session is active, and a banner renders above `<router-outlet>` on every
page with inline Accept/Decline buttons. This is the same "surface it globally, not on a dedicated route"
choice Sprint 3 made for snackbar errors.
Fixed a stale field left by Batch 31's backend change: `ArtisanProfileResponse.userId` (and the
corresponding NgRx state field) no longer exists in the API response — removed from
`artisan-profile.models.ts`, `artisan-profile.state.ts`, `artisan-profile.reducer.ts`, and their three spec
fixtures. This was a loose end from the backend batch, not new Sprint 4 UI work, closed out here since B33
is the first frontend batch to touch this slice since the backend change.
34 new/changed frontend tests (11 reducer, 10 effects incl. HTTP error-message mapping, 7 page component,
2 app-shell banner, 4 fixed artisan-profile spec files) — 192 total frontend tests, all green. Lint and
production build both clean (new `cooperative-members` lazy chunk, 4.74 kB).
VERIFY: full live browser test against the rebuilt dockerized stack, not just unit tests — registered two
real users via direct API calls, then drove the actual Angular UI: owner's `/profile/members` page correctly
showed the invite form and the OWNER row with no remove action; logged in as the invitee and confirmed the
pending-invite banner rendered on the very first page after login ("UI Test Coop invited you to join." with
working Accept/Decline buttons); clicked Accept; confirmed `/profile/members` now lists both users with
correct roles and only the MEMBER row has a "Leave" action, matching Batch 32's backend rules exactly.
Committed as 24867df.

## BATCH 34 2026-07-13 — VERIFY: coverage + security scan (Sprint 4)
Combined backend+frontend line coverage 91.3% (`scripts/check-coverage.sh`, unchanged since Sprint 1 Batch
9) — comfortably clears the 80% gate. Backend 1612/1729, frontend 880/1000 (226 tests across 30 spec
files, up from 192 before this sprint plus the pre-existing suite).
Security scan: Semgrep (0 findings), Gitleaks (0 secrets, 69 commits), Trivy SCA on frontend npm (0
Critical/High), Trivy image scans on both freshly-built images (0 Critical/High). Backend Maven SCA (Trivy
fs) was blocked locally by a Maven Central 429 rate-limit on this session's IP — the same recurring pattern
as Sprint 3's Batch 27, caused by this session's own heavy `mvnw` usage across many batches; not a security
finding, deferred to Batch 35's CI run which executes on a different network.
No code changes this batch — verify-only.

## BATCH 35 2026-07-13 — CI: push, monitor until green (Sprint 4)
Pushed all 10 Sprint 4 commits (Batches 30-34: membership data model, authorization rework, invites,
frontend UI, verify) to origin/main. CI run 29287742155 triggered automatically: **all 5 jobs green on the
first run** (Lint 25s, Security Scan 1m2s, Test+Coverage Gate 2m16s, Build Docker Images 1m57s, Deploy to
Staging 2s — ~5m40s total). No red-CI diagnose/fix cycle needed. This also resolves Batch 34's deferred
item — the backend Trivy SCA scan that was blocked locally by a Maven Central rate-limit ran clean on CI's
network, confirming the local block was a network throttling artifact and not a masked finding, same
resolution pattern as Sprint 3's Batch 27/28.

## BATCH 36 2026-07-14 — SHIP: Sprint 4 E2E suite + video recording (rule 9)
New `e2e/tests/cooperative-flows.spec.ts`, one continuous Playwright session covering Stories 7.1-7.4:
owner registers and creates the cooperative profile + a product; the future member registers first (an
invite can only target an existing ARTISAN account per Assumed Default #4 — deliberately sequenced this
way, not a simplification); owner invites the member by email; the member sees the pending-invite banner
immediately after logging in and accepts it; the members list shows both users with correct roles and the
invite form is hidden from the non-owner; the member creates a product on the shared profile and the owner
sees it too (proving Batch 31's equal-access rework end-to-end, not just via curl); owner removes the
member; the removed member's next login shows the normal "no profile yet" empty state, not an error,
confirming `GetArtisanProfileHandler`'s 404 is treated as a clean state loss rather than surfacing a scary
failure.
One sequencing bug caught while writing the test (not an app bug): the first draft invited the member's
email before that account existed, which correctly 400'd with `INVITEE_NOT_ELIGIBLE` — same enumeration-safe
error Batch 32 designed — and broke the test. Fixed by registering the member before the invite, matching
the real product's actual constraint rather than working around it.
Verified: `cooperative-flows.spec.ts` passes alone, and all four e2e specs (critical-flows, catalog-flows,
order-flows, cooperative-flows) pass together with no cross-sprint regressions. Video saved to
`.recordings/v0.4-2026-07-14.webm` (gitignored, local only, per rule 9).
Document-first: updated docs/test-strategy-sana3-ma.md's release gate checklist with Sprint 4 evidence
(coverage 91.3%, CI run 29287742155, this batch's recording).
Committed as c978e46.

## RETRO 2026-07-14 — Sprint 4 complete (Batches 30-36)
Shipped: cooperative multi-user accounts end to end — membership data model replacing the old 1:1
artisan-profile-to-user constraint, authorization reworked across all 9 pre-existing artisan-only handlers,
invite/accept/decline/list-members/remove-member, and the full frontend UI (members page, invite form,
pending-invite banner). Epic 7 complete, all 4 planned stories shipped, nothing cut from
docs/stories-sana3-ma-sprint4.md.
This sprint was a materially different shape of work than Sprints 2-3: instead of adding a new bounded
context on top of stable ground, it changed the *ownership model* underneath code that had been stable
since Sprint 1 (artisan profile, product, and order-fulfillment authorization). The PLAN-phase research
(reading the actual JWT claims, the actual duplicated ownership-check pattern across 9 handlers, and
confirming `artisan_profiles.user_id` was a real schema constraint, not just an assumption) paid off during
EXECUTE — no handler was missed, and the "5 of 9 handlers only ever used `profile.id()`, never any other
field" observation from that research simplified the rework rather than just refactoring it 1:1.
What deviated from a strict reading of the plan: Story 7.1's AC said the migration should both create
`cooperative_members` *and* drop `artisan_profiles.user_id` in one step; doing that would have broken the
9 handlers before Batch 31 could fix them, so the drop was deliberately deferred one batch (additive-first,
cleanup-second) to keep every commit's `mvn verify` green. Flagged and reasoned through in Batch 30's log
rather than silently diverging from the written plan.
What went well: continuous live verification, not just unit tests, caught real integration behavior at
every layer — Batch 31's rework was confirmed against a real pre-existing database (Flyway migrated straight
through v5→v6 with real Sprint 1-3 data, not just a fresh Testcontainers schema); Batch 32's invite flow was
exercised with two real registered users end-to-end including the negative paths (can't remove the owner,
re-invite after removal works); Batch 33's UI was driven in an actual browser, not just component specs,
which is how the pending-invite banner and the owner/member permission split were confirmed to actually
render and gate correctly rather than just type-check.
Coverage 91.3%, security scan clean (0 Critical/High across Semgrep/Trivy/Gitleaks — the one scan blocked
locally by Maven Central rate-limiting confirmed clean on CI's network, same recurring pattern as Sprint 3),
CI green on the first run for every batch this sprint (Batches 30-35), E2E all four specs green together.
Carried forward: no open Sprint 4 stories. Deferred by this sprint's own stated scope boundary: ownership
transfer, signup-via-invite, multi-cooperative membership per user (see
docs/stories-sana3-ma-sprint4.md "Open Questions"). Remaining PRD "Out of Scope (future sprints)" backlog:
real CMI/Stripe payment gateway integration, QR-authenticated craft certificates, DHL export integration.
Geo-radius search still carried from Sprint 2, still blocked on nothing populating
`artisan_profiles.location`.

## PUSH 2026-07-14
Branch: main | Commits: 2 (373f9cc..817b03a) — Batch 36 (E2E suite + video + retro + session-end).
CI run 29338362770 triggered automatically: **all 5 jobs green on the first run** (Lint 23s, Security Scan
1m11s, Test+Coverage Gate 2m26s, Build Docker Images 1m52s, Deploy to Staging 3s — ~5m55s total). Sprint 4
fully closed out: all 7 batches (30-36) shipped, CI green throughout, coverage 91.3%, zero open
critical/high security findings, E2E suite (4 specs, all sprints) green together, video recorded.

## UNDERSTAND 2026-07-14 — Sprint 5 kickoff
User confirmed starting Sprint 5 planning. Re-read docs/prd-sana3-ma.md §"Out of Scope (future sprints)":
remaining bullets after Sprint 4 closed out cooperative accounts are QR-authenticated craft certificates,
real CMI/Stripe payment gateway (deferred from Sprint 3's own scope-boundary note, and again implicitly
by Sprint 4 not touching it), DHL export integration, and Kubernetes (gated on real scale need, not
proposed). Also checked Sprint 4's own "Open Questions" (docs/stories-sana3-ma-sprint4.md): ownership
transfer, multi-cooperative membership, signup-via-invite — all explicitly deferred-unless-needed, not
sprint-sized asks on their own. No predetermined Sprint 5 scope exists yet — proceeding to BRAINSTORM.

## PLAN 2026-07-14 — Sprint 5 backlog (QR-authenticated craft certificates)
Full backlog written to docs/stories-sana3-ma-sprint5.md (Epic 8: Craft Certificates — 4 stories). User
picked this over the recommended simpler-first option (real payment gateway) during BRAINSTORM, same
pattern as Sprint 4's pick of cooperative accounts. Chosen from evidence in docs/architecture-sana3-ma.md
ADR-1, which named "certification" as the third of three bounded contexts anticipated from Sprint 1
alongside catalog (Sprint 2) and orders (Sprint 3) — this closes out that original plan rather than adding
scope unplanned. Confirmed genuinely greenfield: zero existing code references "certificat" anywhere.
Five Assumed Defaults stated: certificates are per-product-listing, not per-purchased-unit or per-order
(the PRD's "certified provenance" framing is about pre-purchase trust, not proving one specific shipped
item is genuine — that's a bigger, deferred feature); any cooperative member can issue (not OWNER-only,
matching Sprint 4's equal-access default); verification is by an unguessable server-issued code, not
cryptographic signing (sufficient for this sprint's threat model — a signed scheme is real added depth
deferred until there's a stated need to verify offline/without trusting Sana3.ma's server); issuing is
idempotent (returns the existing certificate rather than erroring if one already exists); the QR code is
rendered client-side via a new `qrcode` npm dependency — flagged explicitly since it's the one new
dependency this sprint adds, no backend image generation/storage needed.
No new PII surface — the public verification response reuses Sprint 2 Batch 13's existing public-summary
allowlist (artisan display name, product name/craft type; never contact info).

Batch breakdown (continuing numbering from Sprint 4's B30-B36):
B37 backend certificate domain + migration + issue-or-fetch handler (new `certification` bounded context,
    mirrors catalog/order's hexagonal layering) — Story 8.1
B38 backend public verification endpoint (unauthenticated, allowlisted response) — Story 8.2
B39 frontend certificate NgRx slice + issue/view UI with QR code in My Products (adds `qrcode` npm dep) —
    Story 8.3
B40 frontend public verification page (no route guard) — Story 8.4
B41 VERIFY: coverage (JaCoCo+Vitest) >=80%, security scan (Semgrep/Trivy/Gitleaks)
B42 CI: push, monitor+fix until green
B43 SHIP: Playwright E2E (issue certificate -> scan/verify -> valid and invalid-code cases), video
    recording (.recordings/v0.5-[date].webm), sprint retro, final push, SESSION_END

No dedicated docker-compose/infra batch this sprint — no new external service, just one new table in the
existing Postgres, same as Sprints 3-4.

Not yet user-confirmed — this is the PLAN artifact for review before EXECUTE starts (project rule 5).

## BATCH 37 2026-07-14 — backend certificate domain + migration + issue-or-fetch (Story 8.1)
New `ma.sana3.domain.certification` package — the third bounded context ADR-1 anticipated from Sprint 1,
alongside catalog (Sprint 2) and orders (Sprint 3). `CraftCertificate` entity: id, productId,
artisanProfileId, issuedAt — deliberately no separate `verificationCode` column; the certificate's own `id`
doubles as its public code (already an unguessable UUID primary key, a distinct field would be redundant —
Assumed Default reasoning, not a shortcut). Flyway V8 creates `craft_certificates` with `UNIQUE(product_id)`
enforcing Assumed Default #1 (one certificate per product listing) at the schema level, both FKs
`ON DELETE CASCADE` so a certificate can never outlive its product or profile.
`IssueCertificateHandler` mirrors Sprint 4's membership-based ownership pattern exactly (any cooperative
member, not owner-only, per Assumed Default #2) and is idempotent per Assumed Default #4: `findByProductId`
first, only `CraftCertificate.issue(...)` + save if none exists yet — re-issuing returns the existing
certificate rather than erroring.
8 new tests (3 domain, 5 application incl. the idempotency case and the cross-cooperative ownership
rejection reusing catalog's existing `ProductNotFoundException`), all green.
Real bug caught in the persistence test itself (not the app) while writing it: the first draft used
`UUID.randomUUID()` for `artisanProfileId` instead of the actual persisted profile id, which correctly
violated the new FK constraint — fixed by having the test helper return both the product and profile ids
it actually persisted.
VERIFY: full live smoke test against the rebuilt dockerized stack — Flyway migrated straight to v8;
issued a certificate for a real product, then issued again and confirmed the exact same certificate id
came back (idempotency holds against a real request, not just a mock).
Document-first update: docs/database-sana3-ma.md (ERD, new table schema, migration/index/access-pattern
rows) — also caught and fixed two stale rows left over from Sprint 4 Batch 31 (an index and two access
patterns still referencing the dropped `artisan_profiles.user_id` column), closing a small doc-accuracy
gap while in the area rather than leaving it.
Committed as 033170e.

## BATCH 38 2026-07-14 — public certificate verification endpoint (Story 8.2)
`VerifyCertificateHandler` (unauthenticated): parses the code as a UUID (a malformed string is caught and
treated as `CertificateNotFoundException`, same 404 as an unknown-but-valid-format code — no distinction,
so the endpoint can't be used to fingerprint "wrong" vs "garbage" input), looks up the certificate, then
joins to `Product`/`ArtisanProfile` for display fields. The two follow-up lookups throw
`IllegalStateException` if missing rather than a normal not-found — the `ON DELETE CASCADE` FKs mean that
can only happen from a real data-integrity bug, not a legitimate empty state.
`GET /api/v1/certificates/verify/{code}`, `SecurityConfig` updated to `permitAll` this path (mirrors the
existing `GET /api/v1/products/**` public rule).
3 new handler tests (valid code, unknown code, malformed code) plus 2 new web-layer tests (valid response,
404 mapping) — the public controller test needed `@AutoConfigureMockMvc(addFilters = false)` since
`SecurityConfig` is package-private and can't be imported into the web slice test, same documented
workaround `PublicProductControllerTest` already uses.
VERIFY: live smoke test against the dockerized stack — verified the certificate issued in Batch 37 by its
real code with no Authorization header at all (confirmed public), then confirmed both an unknown UUID and
a garbage string (`not-a-uuid`) return the same 404 `CERTIFICATE_NOT_FOUND`, not a 400 or 500.
Document-first update: docs/security-sana3-ma.md §5 (new public-verification PII/enumeration-safety note,
same allowlist Sprint 2 Batch 13 established, referencing why a signed scheme wasn't needed).
Committed as 6e5b7dc.

## BATCH 39 2026-07-14 — frontend certificate NgRx slice + issue/view UI with QR code (Story 8.3)
New `certificate` NgRx feature slice — one action pair (`issueCertificate`/success/failure), state keyed
by `productId` (`Record<string, CertificateResponse>`) since a page can show many products each with their
own certificate, same shape reasoning as prior incremental slices. `CertificateService` wraps the Batch 37
issue endpoint.
Integrated into the existing `/profile/products` page rather than a new route — an "Issue Certificate" /
"View Certificate" button per product card (label switches once this session has seen a certificate for
that product; the idempotent backend means clicking either way is safe). QR rendering uses the new
`qrcode` npm dependency's `toString(url, {type:'svg'})` API deliberately, not `toDataURL` — the SVG string
path has no canvas/DOM dependency, so it renders identically in the browser and in Vitest/jsdom with no
canvas polyfill needed. The rendered SVG is sanitized via `DomSanitizer.bypassSecurityTrustHtml` (safe here
since the SVG is generated by our own trusted library from a URL we constructed, never user input) and
bound via `[innerHTML]`.
`angular.json` gained `allowedCommonJsDependencies: ["qrcode"]` to silence a CommonJS-optimization-bailout
build warning — not an error, but worth suppressing since every future build would otherwise print noise
for a dependency this sprint deliberately chose to add.
9 new/changed frontend tests (3 reducer, 3 effects incl. HTTP error-message mapping, 3 new My Products
page cases: issue button dispatches, view mode + verification code once state has a certificate, QR render
+ snackbar on success) — 202 total frontend tests, all green. Lint and production build both clean.
VERIFY: full live browser test against the rebuilt dockerized stack (not just unit tests) — registered a
real artisan via direct API calls, created a real product, then drove the actual UI: clicked "Issue
Certificate," saw the "Certificate issued" snackbar, the button switch to "View Certificate," a real
scannable QR code render inline, and the verification code text appear below it, all end-to-end against
the live backend from Batches 37-38.
Committed as e23500e.

## BATCH 40 2026-07-14 — frontend public verification page (Story 8.4)
Extended the `certificate` slice (not a new one) with a second action pair — `verifyCertificate`/success/
`notFound`/failure, mirroring the exact "not-found is a normal empty state, distinct from a real failure"
pattern `loadProductDetail` already established in the catalog slice (Batch 13). New `/certificates/verify/:code`
route, no guard (public, like `/browse` and `/products/:id`) — `CertificateVerify` page dispatches
`verifyCertificate` with the route's `code` param on construction and renders one of four states: verifying,
a valid result (artisan/product/craft-type/issue-date), an explicit "not a valid certificate" message for
an unknown/malformed code, or a real error message.
17 new/changed frontend tests (5 reducer, 3 effects incl. the 404-vs-other-error distinction, 5 new page
component cases) — 214 total frontend tests, all green. Lint and production build both clean.
VERIFY: full live browser test against the rebuilt dockerized stack, no session/login involved (confirming
the page truly needs none) — navigated directly to the real certificate issued in Batch 39's live test and
saw the correct artisan/product/craft-type/date; navigated to a garbage code and saw the "Not a valid
certificate" message, not an error page or blank screen. Also confirmed the certificate data survived a
full container recreation (backend/frontend rebuilt, postgres untouched), same persistence-proof pattern
Sprint 2 Batch 17 established for product images.
Committed as c74a3c8.

## BATCH 41 2026-07-15 — VERIFY: coverage + security scan (Sprint 5)
Combined backend+frontend line coverage 91.5% (`scripts/check-coverage.sh`) — comfortably clears the 80%
gate. Backend 1731/1856, frontend 963/1087 (214 tests).
Security scan: Semgrep (0 findings), Gitleaks (0 secrets, 82 commits), Trivy SCA on frontend npm incl. the
new `qrcode` dependency (0 Critical/High), Trivy image scan on the backend image (0). The frontend image
scan found 4 real HIGH findings (curl/libcurl CVE-2026-5773, CVE-2026-6276) — new CVEs disclosed against
Alpine packages since this image was last actually rebuilt from scratch, not masked by anything in this
sprint's own code. Fixed by rebuilding with `docker compose build --no-cache frontend`: the Dockerfile
already has `apk upgrade --no-cache` from Sprint 1 Batch 8, it just hadn't re-run against a fresh package
index because Docker's layer cache was reusing the old `apk upgrade` result. Re-scanned clean after the
rebuild — same fix pattern as Batch 8, now a second confirmed instance of it, worth remembering as a
recurring category (a currently-clean image can go stale between sprints purely from new upstream CVE
disclosures, independent of any code change).
Backend Maven SCA (Trivy fs) blocked locally by the same recurring Maven Central rate-limit as Sprints 3-4;
not a security finding, deferred to Batch 42's CI run.

## BATCH 42 2026-07-15 — CI: push, monitor until green (Sprint 5)
Pushed all 9 Sprint 5 commits (Batches 37-41: certificate domain, public verification, frontend issue/view
UI, public verification page, verify) to origin/main. CI run 29375650649 triggered automatically: **all 5
jobs green on the first run** (Lint 36s, Test+Coverage Gate 2m20s, Security Scan 51s, Build Docker Images
2m6s, Deploy to Staging 3s — ~5m56s total). No red-CI diagnose/fix cycle needed. This also resolves Batch
41's deferred item — the backend Trivy SCA scan blocked locally by Maven Central rate-limiting ran clean on
CI's network, confirming the local block was a network throttling artifact and not a masked finding, same
resolution pattern as Sprints 3-4.

## BATCH 43 2026-07-15 (IN PROGRESS — session paused mid-batch) — SHIP: Sprint 5 E2E suite + video
New `e2e/tests/certificate-flows.spec.ts`, one continuous Playwright session covering Stories 8.1-8.4:
artisan registers, creates a profile and a product, issues a certificate (verification code + QR code both
render), re-issuing is confirmed idempotent (same code returned), then logs out and hits the public
`/certificates/verify/{code}` page with no login at all — confirms artisan/product/craft-type render for
the real code, and a separate check confirms an all-zeros unknown UUID shows the "Not a valid certificate"
message rather than an error page.
Verified: `certificate-flows.spec.ts` passes alone, and all five e2e specs (critical-flows, catalog-flows,
order-flows, cooperative-flows, certificate-flows) pass together. One transient failure seen on the first
full-suite run — `cooperative-flows.spec.ts` timed out on a `getByLabel('Email').fill(...)` mid-navigation;
reproduced as a one-off (passed immediately both alone and on a second full-suite run), not a regression
from this batch's changes, consistent with this project's previously-documented browser-automation
flakiness under sequential load rather than an app bug.
Video saved to `.recordings/v0.5-2026-07-15.webm`. docs/test-strategy-sana3-ma.md's release gate checklist
updated with Sprint 5 evidence (coverage 91.5%, CI run 29375650649, this batch's recording).
Remaining for B43 (next session): write the Sprint 5 retro, SESSION_END log entry, final commit, and push.
