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

## SESSION_START 2026-07-06
Resumed from previous SESSION_END (2026-07-06). Continuing Sprint 1 at Batch 3: backend artisan profile (CQRS command/query, ownership auth, tests).

## SESSION_END 2026-07-06
Done this session:
- Implemented Batch 3 (backend artisan profile): domain ArtisanProfile/ArtisanProfileRepository, application
  UpdateArtisanProfileCommand/Handler + GetArtisanProfileQuery/Handler (CQRS-lite per ADR-1), adapter-persistence
  JPA entity/repo/adapter (reusing Batch 1's V2 migration), adapter-web ArtisanProfileController
  (PUT/GET /api/v1/artisan-profiles/me) with ownership from the JWT principal and a role=ARTISAN check on writes.
- Tests: 17 new (4 domain, 5 application, 3 persistence Testcontainers, 5 web @WebMvcTest) — full suite now 46
  tests, all green.
- Learned during test-writing: @WebMvcTest needs real security filters active (not addFilters=false) for
  SecurityMockMvcRequestPostProcessors.authentication(...) to populate the SecurityContext, and mutating requests
  need .with(csrf()) since the default (non-custom) security auto-config used by the test slice has CSRF on,
  unlike the app's real stateless SecurityConfig.
- Ran a full docker-compose smoke test (register artisan+buyer, 404 before profile exists, create, get, update
  keeping same id, 403 for buyer role, 401 unauthenticated, 400 validation) — all as expected, no bugs found.
- Committed locally as 9af3ab9 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).

Next session: resume at Batch 4 (frontend scaffold — Angular standalone, Material, NgRx store, routing,
Dockerfile). See .logs/activity.md "PLAN 2026-07-05 — Sprint 1 batches" for the full B1-B10 breakdown.

## SESSION_START 2026-07-08
Resumed from previous SESSION_END (2026-07-06). Continuing Sprint 1 at Batch 4: frontend scaffold (Angular
standalone, Material, NgRx store, routing, Dockerfile).

## SESSION_END 2026-07-08
Done this session:
- Implemented Batch 4 (frontend scaffold): `ng new` (Angular 22, standalone components, SCSS, routing) in
  frontend/, Angular Material (azure-blue theme) with an app-shell toolbar/nav, NgRx store + effects +
  devtools wired in app.config.ts with empty `auth` and `artisanProfile` feature slices (createFeature, no
  actions yet — real logic lands in Batch 5/6 per ADR-2), lazy-loaded routing skeleton (home/login/register/
  profile placeholders + wildcard not-found), ESLint via @angular-eslint/schematics, and frontend/Dockerfile
  (multi-stage node:22-alpine build -> nginx:alpine serve).
- Two real deviations from the approved foundation docs, both surfaced to the user and resolved before
  proceeding (not silently changed):
  1. Angular 22's `ng new` defaults to Vitest, not Jasmine/Karma — Karma is deprecated upstream and no longer
     offered. User chose to adopt Vitest; updated docs/test-strategy-sana3-ma.md and docs/devops-sana3-ma.md
     accordingly (test syntax describe/it/expect unchanged, so no rewrite needed elsewhere).
  2. NgRx's latest release (21.1.1) peer-depends on Angular ^21, but Angular 22 was just scaffolded — NgRx
     hasn't shipped Angular-22 support yet. User chose to stay on Angular 22 and install NgRx with
     --legacy-peer-deps rather than downgrade Angular. Added frontend/.npmrc (legacy-peer-deps=true) so this
     doesn't also break `ng add` schematics or CI's `npm ci` later. Logged as an open risk in .logs/risks.md
     with a fallback plan (downgrade to Angular 21, or re-pin once NgRx catches up) — watch for it during
     Batch 5/6 when real NgRx actions/effects get added.
  3. Also fixed en route: `ng add @angular/material --animations=enabled` did not actually install
     `@angular/animations` (Material 22 still needs it internally even though the app-level animation API is
     being deprecated in favor of `animate.enter`/`animate.leave`) — installed it explicitly, build was
     failing without it. Also corrected the devops doc's guessed Docker output path
     (`dist/sana3-ma-frontend`) to the real one (`dist/frontend/browser` — new Angular builder nests static
     output under `browser/`, and the project is named `frontend` not `sana3-ma-frontend`). Added
     frontend/.dockerignore (missing project convention; excludes node_modules/dist so the Docker build
     context doesn't transfer ~260MB every build).
- Verified locally: `ng build` (bundles + lazy chunks all present), `ng test --watch=false` (7/7 passing,
  6 test files, Vitest), `ng lint` (clean), and a real `docker build` of frontend/Dockerfile end-to-end
  (confirmed the nginx image actually contains the built app at the right path) — test image removed after.
- Committed locally as c825a51 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).

Next session: resume at Batch 5 (frontend auth UI — login/register, NgRx auth slice actions/reducer/effects,
JWT interceptor, route guards, tests). Watch the NgRx/Angular-22 peer-dep risk (.logs/risks.md) once real
effects are added — if anything breaks at runtime, the fallback is documented there. See .logs/activity.md
"PLAN 2026-07-05 — Sprint 1 batches" for the full B1-B10 breakdown.

## SESSION_START 2026-07-08 (continued 2026-07-09)
Resumed from previous SESSION_END (2026-07-08). Continuing Sprint 1 at Batch 5: frontend auth UI
(login/register, NgRx auth slice, JWT interceptor, guards, tests).

## SESSION_END 2026-07-09
Done this session:
- Fixed a Batch 4 gap first: docs/ui-sana3-ma.md specifies a custom terracotta/teal/Inter M3 theme, but
  Batch 4 shipped the azure-blue prebuilt Material theme. Generated a real M3 palette from the brand hex
  colors via `ng generate @angular/material:theme-color` and wired it into styles.scss + Inter via Google
  Fonts. NgRx's Angular-22 peer-dep gap from Batch 4 (.logs/risks.md) held up fine through this — no runtime
  issues yet.
- Implemented the real auth NgRx slice (auth.actions/reducer/effects/selectors.ts), replacing Batch 4's
  empty placeholder reducer: register/login/refreshToken/logout, matching the exact backend contract from
  backend/adapter-web/.../auth/*.java (AuthResponse shape, ApiError envelope, 409/401/400 codes).
- AuthService (HttpClient, withCredentials for the httpOnly refresh cookie), a functional JWT interceptor
  (attaches access token from the store via selectSignal), an authGuard on /profile, and a
  provideAppInitializer that dispatches a silent refreshToken on bootstrap so a page reload restores the
  session instead of bouncing to /login (access token is intentionally memory-only per
  docs/security-sana3-ma.md, so without this every reload would look logged-out).
- Login/Register standalone components: reactive forms, Material fields, inline validation (required/email/
  minlength 10) per Story 1.3's acceptance criteria, role radio group (BUYER/ARTISAN) on register, MatSnackBar
  for auth errors, redirect-on-authenticated (honoring `returnUrl`). Added a logout control to the app shell
  toolbar (not explicitly in Story 1.3, but the auth slice needed a way to actually use it).
- Found a real gap via browser smoke-testing (not caught by unit tests): logout only cleared client-side
  NgRx state — the backend had no logout endpoint, so the httpOnly refresh cookie stayed valid and a reload
  silently logged the user back in; worse, the session was never actually revoked server-side. Surfaced this
  to the user rather than silently shipping around it. User chose to add a minimal backend fix: `POST
  /api/v1/auth/logout` (backend/adapter-web AuthController) that expires the refresh cookie — stateless JWT
  (ADR-3) has no server-side session store to revoke beyond that, which is an accepted limitation, not a bug.
  Added AuthControllerTest coverage for it and a frontend logout$ effect (non-dispatching) that calls it.
- Tests: 30 frontend (reducer, effects incl. logout$, guard, interceptor, Login/Register components, app
  shell) all green, plus 2 new backend AuthControllerTest cases. `ng build`/`ng test`/`ng lint` all clean.
- Real end-to-end smoke test against a live backend + browser (not just unit tests): register → auto-login →
  redirect to /profile, inline validation errors rendering correctly, terracotta/teal theme rendering
  correctly, hard-reload-restores-session via the refresh cookie, logout → reload now correctly requires
  login again (post-fix), login → returnUrl redirect. Used a local port override (4201 + matching
  CORS_ALLOWED_ORIGINS) since 4200 was occupied by an unrelated process on this machine; reverted .env back
  to the documented default (4200) afterward. Docker containers and dev server stopped after testing.
- Committed locally as 6c9cc21 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).

Next session: resume at Batch 6 (frontend profile UI — view/edit, NgRx profile slice, tests). The
artisan-profile NgRx slice is still the empty placeholder from Batch 4 (store/artisan-profile/*.ts) — same
pattern as this batch's auth slice work applies there. See .logs/activity.md "PLAN 2026-07-05 — Sprint 1
batches" for the full B1-B10 breakdown, and docs/stories-sana3-ma.md Epic 2 for acceptance criteria.
