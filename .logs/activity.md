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
