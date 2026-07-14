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

## SESSION_END 2026-07-09 (continued, Batch 6)
Done this session (same continuous session as above, user said "continue"):
- Before starting Batch 6, re-read docs/ux-sana3-ma.md closely (document-first) and found two real gaps
  in what Batch 5 shipped: the site map explicitly scopes /profile to ARTISAN only, and Flow 1 specifies a
  role-based post-auth redirect (buyer -> Home, artisan -> Profile) — Batch 5 had unconditionally redirected
  everyone to /profile and the guard only checked "logged in", not role. Fixed both: renamed authGuard ->
  artisanGuard (now checks role, redirects non-artisans home instead of letting them hit a 403 on save),
  and login/register success redirects based on role. Also hid the toolbar's Profile link for non-artisans
  (a buyer could otherwise click a dead-end link).
- Implemented the real artisan-profile NgRx slice (actions/reducer/effects/selectors), replacing Batch 4's
  empty placeholder: load/save against GET/PUT /api/v1/artisan-profiles/me, matching the exact backend
  contract from Batch 3 (ArtisanProfileResponse, 404 treated as a normal empty state per Story 2.3 — not an
  error — 403 NOT_AN_ARTISAN, 400 VALIDATION_FAILED). ArtisanProfileService (HttpClient; no withCredentials
  needed here since protected endpoints use the JWT interceptor's Authorization header, not the auth cookie).
- Profile component: single combined view/edit reactive form matching docs/ux-sana3-ma.md's wireframe
  exactly (display name, craft type, region, bio, phone) — pre-filled when a profile exists, a "Complete
  your profile" prompt when it doesn't (empty state), a loading message while fetching, "Profile updated"
  toast on save (exact phrase from the UX doc's screen-states table).
- Extracted a shared extractErrorMessage/ApiError util (core/http-error.util.ts, core/api-error.model.ts)
  out of auth.effects.ts so artisan-profile.effects.ts didn't duplicate it — a real simplification found
  while writing the second effects file, not planned upfront.
- Tests: 54 frontend (added profile reducer/effects/component tests, guard role-based cases, login/register
  redirect cases) all green. `ng build`/`ng test`/`ng lint` clean (one transient `ng build` failure fetching
  Google Fonts over the network mid-session — unrelated to code, retry succeeded; noted in .logs/risks.md as
  a Batch 9 CI consideration, not a bug).
- Real end-to-end browser smoke test against a live backend (not just unit tests): logged in as the
  existing artisan account (session survived from Batch 5's testing) and got auto-redirected register ->
  /profile with the empty-state prompt showing correctly; filled and saved the form, got the "Profile
  updated" toast, reloaded and confirmed the data persisted via GET; registered a fresh buyer account and
  confirmed it lands on Home instead of /profile, and that navigating to /profile directly bounces back
  to Home (artisanGuard blocking a non-artisan). Same local port workaround as Batch 5 (4201 + matching
  CORS_ALLOWED_ORIGINS), reverted .env afterward, docker/dev-server stopped after testing.
- Committed locally as c3cbea7 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7).

Next session: resume at Batch 7 (docker-compose full wiring + local end-to-end smoke test). This is where
the frontend service actually gets added to docker-compose.yml (it's only ever been run via `ng serve` so
far) and where API_BASE_URL needs to become environment-configurable instead of the hardcoded
localhost:8081 dev default in frontend/src/app/core/api.config.ts (flagged as a known placeholder back in
Batch 5). See .logs/activity.md "PLAN 2026-07-05 — Sprint 1 batches" for the full B1-B10 breakdown.

## SESSION_END 2026-07-10 (Batch 7)
Done this session:
- BRAINSTORM/PLAN gates run with the user before executing: chose build-time ARG substitution for
  API_BASE_URL over a runtime nginx env.js approach (YAGNI — no staging/prod split needed this sprint per
  docs/devops-sana3-ma.md).
- Added `frontend` service to docker-compose.yml (build args, FRONTEND_HOST_PORT:-4200:80, depends_on
  backend). frontend/Dockerfile now takes ARG API_BASE_URL and writes it into core/api.config.ts before
  `npm run build`. Fixed a stale .env/.env.example mismatch (API_BASE_URL said :8080, BACKEND_HOST_PORT
  default is :8081) and added FRONTEND_HOST_PORT to both files.
- Real bug caught during the smoke test, not before: nginx's default config has no SPA fallback, so any
  direct navigation or hard reload of an Angular route (e.g. /register, /profile) 404'd — Batches 4-6 only
  ever exercised `ng serve`'s own routing, so this was invisible until the actual container was tested.
  Fixed with frontend/nginx.conf (`try_files $uri $uri/ /index.html;`), wired into the Dockerfile, and
  documented in docs/devops-sana3-ma.md (document-first rule).
- Local-machine-only issue, not a project bug: host port 4200 was already bound by an unrelated project's
  container (atlas-events) on this machine. Remapped FRONTEND_HOST_PORT/CORS_ALLOWED_ORIGINS to 4202 in the
  local .env only (gitignored) — .env.example keeps the clean 4200 default. Logged to .logs/risks.md so a
  future session doesn't mistake this for a project misconfiguration.
- Full end-to-end smoke test against the fully-dockerized stack (postgres + backend + frontend, no
  `ng serve`, no local backend process): registered a fresh artisan account → auto-redirect to /profile
  empty state → filled and saved the profile form → hard-reloaded (real nginx round trip, not an Angular
  router nav) and confirmed both the route resolved correctly and session + profile data persisted →
  logged out → confirmed /profile now redirects to /login?returnUrl=%2Fprofile. All 3 containers verified
  healthy (backend /actuator/health UP, frontend HTTP 200, postgres healthy) throughout. No console errors
  observed during the flow.
- Committed locally as 0c2a73a (not pushed — push happens at sprint SHIP, Batch 10, per rule 7). Containers
  stopped after testing.

Next session: resume at Batch 8 (VERIFY — coverage: JaCoCo + Karma combined ≥80%, security scan: Semgrep/
Trivy/Gitleaks). See .logs/activity.md "PLAN 2026-07-05 — Sprint 1 batches" for the full B1-B10 breakdown.

## SESSION_END 2026-07-10 (Batch 8, same continuous session — user said "yes" to continue)
Done this session:
- Added JaCoCo (jacoco-maven-plugin) to the backend parent pom — none existed before. Note: 0.8.12 (the
  version I first tried) can't instrument Java 25 bytecode ("Unsupported class file major version 69");
  had to bump to 0.8.13, which works.
- Installed @vitest/coverage-v8 in frontend (missing — `ng test --coverage` errored without it) to get
  coverage out of the new Angular 22 unit-test builder.
- Coverage results: backend combined 89.9% line coverage (domain 79.7%, application 100%,
  adapter-persistence 100%, adapter-web 84.1%), frontend 89.1% line coverage (54 tests). Blended
  backend+frontend: 89.5% (625/698 lines) — clears the 80% gate with no new tests needed. Full numbers in
  .logs/metrics.md "BATCH 8" entry.
- Security scan (Semgrep local install, Trivy + Gitleaks via Docker since not installed locally — used a
  persistent `trivy-cache` docker volume so the ~100MB vuln DB isn't re-downloaded every run):
  - Semgrep (384 rules, backend/ + frontend/src): 1 finding — backend/Dockerfile ran the JVM as root, no
    USER directive. Fixed (added non-root `app` user). Re-ran: 0 findings.
  - Trivy SCA (fs scan, backend Maven + frontend npm): 0 Critical/High both. Maven scan initially hit a
    429 from repo.maven.apache.org — fixed by mounting the host's ~/.m2 into the trivy container instead
    of letting it re-resolve everything.
  - Trivy image scan (both built images): 5 HIGH CVEs on backend (libexpat, p11-kit, stale Alpine
    packages), 4 HIGH on frontend (c-ares, libexpat) — none Critical, so technically passed the documented
    gate as-is, but fixes were already published in the Alpine repo so fixed anyway: added
    `RUN apk upgrade --no-cache` to both Dockerfiles' final stage. Rebuilt + rescanned: 0/0.
  - Gitleaks (full git history, 15 commits): 0 secrets.
- Re-verified full stack still boots clean after the Dockerfile changes (non-root user didn't break
  anything — Spring Boot doesn't need root): backend /actuator/health UP, frontend HTTP 200.
- Committed locally as 1679bb1 (not pushed — push happens at sprint SHIP, Batch 10, per rule 7). Containers
  stopped after testing.

Next session: resume at Batch 9 (CI — GitHub Actions workflow: lint/test+coverage-gate/security-scan/build/
deploy-staging per docs/devops-sana3-ma.md §2, push, then monitor CI and fix until green per rule 11). Two
things to carry forward: (1) the Google Fonts network-fetch-during-build risk noted in .logs/risks.md — the
GitHub Actions runner needs outbound internet during `ng build`, or self-host the font; (2) this session's
Maven-429 workaround (mount ~/.m2) is local-only — CI will need its own dependency caching strategy (e.g.
actions/cache on ~/.m2) if Trivy or `mvn` scanning runs there too. See .logs/activity.md "PLAN 2026-07-05 —
Sprint 1 batches" for the full B1-B10 breakdown.

## SESSION_END 2026-07-10 (Batch 9, same continuous session — user said "yes" to continue)
Done this session:
- BRAINSTORM/PLAN gates run with the user: confirmed Spotless over Checkstyle for backend lint, and
  explicitly flagged before executing that this batch's push would be the first real code push (not just
  docs) to the shared repo — user confirmed via the plan-execution gate.
- Added Spotless (google-java-format) to backend/pom.xml. Hit a real compatibility problem: the default
  google-java-format bundled by spotless-maven-plugin 2.44.2 throws NoSuchMethodError on JDK 25's javac
  internals; fixed by pinning google-java-format 1.27.0 and adding the standard --add-exports/--add-opens
  JVM flags via backend/.mvn/jvm.config. Ran spotless:apply — reformatted 69 files (mechanical only, `mvn
  verify` all-green afterward confirms no behavior change).
- Built .github/workflows/ci.yml (5 jobs matching docs/devops-sana3-ma.md §2). Wrote scripts/check-coverage.sh
  to enforce the combined 80% gate (couldn't rely on a single tool since backend=JaCoCo and frontend=Vitest;
  discovered `ng test --coverage-reporters json-summary` was needed to get a machine-readable frontend
  summary — the default reporters don't include one).
  Verified third-party Action versions against the GitHub API before committing to them rather than
  guessing (gh api repos/.../tags) — caught that gitleaks/gitleaks-action is a separate commercially-licensed
  wrapper around the open-source gitleaks CLI; sidestepped that ambiguity by invoking the CLI's own Docker
  image directly instead, same as Batch 8's manual scan.
- Pushed 20 commits (Batches 1-9) to origin/main — the two risks carried forward from Batch 8's session-end
  note (Google Fonts network fetch during `ng build`; Maven 429 rate-limiting) both turned out to be
  non-issues in CI: `actions/setup-java`'s built-in Maven cache avoided the 429, and the Angular build's
  Google Fonts fetch succeeded without incident.
- Monitored the triggered CI run (29083741944) via `gh run watch`: **all 5 jobs passed on the first run**
  (Lint 32s, Test+Coverage Gate 1m23s, Security Scan 58s, Build Docker Images 1m54s, Deploy to Staging 2s).
  No red-CI diagnose-fix-repush cycle was needed.
- Committed locally as 30a34dc (Spotless), fbda9f3 (CI workflow), 8791215 (exec-bit fix) — all pushed.

Next session: resume at Batch 10 (SHIP — Playwright E2E + video recording covering all critical user flows
per rule 9, final push, sprint retro, SESSION_END). This closes out Sprint 1. See .logs/activity.md
"PLAN 2026-07-05 — Sprint 1 batches" for the full B1-B10 breakdown.

## SESSION_END 2026-07-10 (Batch 10, same continuous session — user said "yes" to continue — SPRINT 1 COMPLETE)
Done this session:
- Scaffolded a Playwright e2e/ suite as its own toolchain (not bolted onto the Angular unit-test setup),
  per docs/test-strategy-sana3-ma.md's tooling table. Wrote one continuous test
  (e2e/tests/critical-flows.spec.ts) covering all 4 ATDD scenarios from that doc's §3 in a single browser
  context, so Playwright's video output is one file, not four — matches rule 9's "record a browser test
  session" (singular) wording. Checked the buyer-blocked-from-profile scenario at both the UI level
  (artisanGuard redirect) and the API level (direct PUT /artisan-profiles/me returning 403), since the
  documented Gherkin scenario is worded at the API level ("request the artisan profile edit endpoint") but
  the frontend guard normally prevents the UI from ever making that call.
- Ran it against the full docker-compose stack (postgres+backend+frontend, not `ng serve`) — passed on the
  first attempt, no fixes needed. Saved the recording to `.recordings/v0.1-2026-07-10.webm` (gitignored,
  stays local per the existing binary-artifacts convention — mentioning the path here since it won't be in
  git history: it's on this machine at that path if it needs to be archived elsewhere).
- Updated docs/test-strategy-sana3-ma.md's release gate checklist to all-checked with evidence (commit
  refs, CI run ID, actual coverage numbers) — was all unchecked boxes since the doc was written in the
  first session before any code existed.
- Closed out two risks in .logs/risks.md that Batch 9's CI run empirically resolved (NgRx --legacy-peer-deps
  survives a clean `npm ci`; Google Fonts fetch works fine on GitHub's hosted runners) — both were "watch
  and see" entries from earlier batches, now confirmed non-issues rather than left dangling.
- Wrote the Sprint 1 retro (.logs/activity.md "RETRO 2026-07-10"): what shipped, what deviated from the
  original plan and why (architecture scope choice, Vitest/Karma forced swap, NgRx peer-dep workaround),
  what went well (real smoke-testing every batch caught 2 real bugs unit tests missed; document-first
  caught a real UX gap before Batch 6 shipped), what's carried forward (stateless-JWT revocation limitation,
  accepted; local port contention, a machine note not a project bug), and what was deliberately deferred
  under YAGNI (k8s, staging/prod infra, anything outside Epics 1-2).
- Committed e2e suite as 3de667f. Final push of all Batch 10 work (E2E suite + retro/logs) to origin/main —
  triggered CI once more; monitored to completion, green.

Sprint 1 is complete: all 10 batches shipped, CI green, coverage 89.5%, zero open critical/high security
findings, E2E happy path recorded. Next session starts Sprint 2 planning (new UNDERSTAND/BRAINSTORM/PLAN
cycle with the user — no predetermined batch list exists yet, unlike this sprint's B1-B10).

## SESSION_START 2026-07-10 (continued 2026-07-11) — Sprint 2
User asked "what's the plan for sprint 2", then redirected from an interactive BRAINSTORM (rejected the
AskUserQuestion tool call) to "look to docs folder and do a full sprint backlog to follow" — read the PRD's
"Out of scope (future sprints)" list and picked Product Catalog & Browsing as the natural next sprint
(artisan profiles from Sprint 1 exist to support product listings; everything else in that list — orders,
payments — depends on products existing first). Wrote the full Sprint 2 backlog
(docs/stories-sana3-ma-sprint2.md, Epic 3 + Epic 4, 9 stories) with 4 stated YAGNI defaults for gaps the
foundation docs didn't answer, and a B11-B20 batch plan continuing Sprint 1's numbering. User approved with
"start", then continued through every batch with a plain "yes, continue into B_" — one unbroken session
across a date rollover (2026-07-10 -> 2026-07-11), no restarts.

## SESSION_END 2026-07-11 — Sprint 2 complete (Batches 11-20)
Full batch-by-batch detail is in .logs/activity.md (one entry per batch, "BATCH 11" through "BATCH 20", plus
a full "RETRO 2026-07-11" entry) — not repeated here. Summary: shipped the full product catalog (artisan
self-service CRUD + image upload) and public browsing/search (filterable listing + detail page), Epics 3-4
complete, nothing cut from the planned backlog.
Real bugs found and fixed via live smoke-testing every batch (not by unit tests, which structurally
couldn't have caught most of these): a Postgres/Hibernate `LOWER(null)`->`bytea` type-inference bug in the
search query (Batch 13); the non-root container from Sprint 1 Batch 8 couldn't write to its own upload
directory (Batch 14); the Docker build silently drops any export added to frontend/core/api.config.ts
(Batch 15, a Sprint 1 Batch 7 quirk rediscovered); an NgRx effect leaking the action's `type` field into an
HTTP query param, caught by a genuine failing unit test (Batch 16); image persistence needed proving via a
full container recreation, not just a restart (Batch 17).
Coverage 89.7%, security scan clean on the first pass with zero fixes needed (contrast Sprint 1's Batch 8,
which found three issues) — continuous scanning as each batch shipped paid off. CI required zero workflow
changes across all ten batches, green on every push. E2E: new catalog-flows.spec.ts plus Sprint 1's
critical-flows.spec.ts both green together, confirming no cross-sprint regressions. Video recorded to
`.recordings/v0.2-2026-07-11.webm` (gitignored, local only).
Final push done, CI monitored green. All Sprint 2 work committed and pushed to origin/main.

Next session: no predetermined Sprint 3 plan exists. The one carried-forward open item from this sprint is
geo-radius search ("artisans near me") — flagged in docs/stories-sana3-ma-sprint2.md's Open Question section
as blocked on some future feature actually populating `artisan_profiles.location`. Otherwise, per the PRD's
"Out of Scope (future sprints)" list, the remaining backlog is: orders/checkout/payment, QR-authenticated
craft certificates, DHL export integration, cooperative multi-user accounts — Sprint 3 planning starts fresh
with the user (UNDERSTAND/BRAINSTORM/PLAN), same as how Sprint 2 started.

## SESSION_END 2026-07-13 — Sprint 3 complete (Batches 21-29)
Full batch-by-batch detail in .logs/activity.md ("BATCH 21" through "BATCH 29", plus "RETRO 2026-07-13").
Summary: shipped the full order lifecycle (cart, checkout, order history, cancellation, artisan
fulfillment) — Epics 5-6 complete, nothing cut from docs/stories-sana3-ma-sprint3.md. Two real backend
correctness gaps found via live smoke-testing and fixed same-session (cross-cancel/cross-complete guards,
Batches 23 and 26). Coverage 91.1%, security scan clean, CI green on the first run every batch, E2E suite
(critical-flows + catalog-flows + order-flows) all green together. Video recorded to
`.recordings/v0.3-2026-07-13.webm` (local only). Closed a process gap: docs/test-strategy-sana3-ma.md's
release gate checklist had gone stale since Sprint 1 and wasn't re-touched during Sprint 2 — fixed now with
Sprint 3 evidence.

Next session: no predetermined Sprint 4 plan exists. Remaining PRD "Out of Scope (future sprints)" backlog:
real CMI/Stripe payment gateway integration (deliberately split out of Sprint 3, see its Assumed Default
#4), QR-authenticated craft certificates, DHL export integration, cooperative multi-user accounts.
Geo-radius search ("artisans near me") still carried from Sprint 2, still blocked on nothing populating
`artisan_profiles.location`. Sprint 4 planning starts fresh with the user (UNDERSTAND/BRAINSTORM/PLAN).

## SESSION_END 2026-07-14 — Sprint 4 complete (Batches 30-36)
Full batch-by-batch detail in .logs/activity.md ("BATCH 30" through "BATCH 36", plus "RETRO 2026-07-14").
Summary: shipped cooperative multi-user accounts — membership data model, authorization rework across 9
pre-existing handlers, invites (create/accept/decline/list/remove), and the full frontend UI (members page,
invite form, pending-invite banner shown on every page after login). Epic 7 complete, all 4 stories shipped
as planned. This sprint reworked the ownership model underneath code stable since Sprint 1, rather than
adding a new bounded context on top of stable ground — the PLAN-phase research into the actual JWT
claims/ownership-check duplication paid off with a clean, complete rework (verified live against a real
pre-existing database, not just a fresh test schema). Coverage 91.3%, security scan clean, CI green on the
first run every batch, E2E suite (4 specs) all green together. Video recorded to
`.recordings/v0.4-2026-07-14.webm` (local only).

Next session: no predetermined Sprint 5 plan exists. Remaining PRD "Out of Scope (future sprints)" backlog:
real CMI/Stripe payment gateway integration (deliberately split out of Sprint 3), QR-authenticated craft
certificates, DHL export integration. Geo-radius search still carried from Sprint 2. This sprint's own
deferred items (see docs/stories-sana3-ma-sprint4.md "Open Questions"): ownership transfer,
signup-via-invite, multi-cooperative membership per user. Sprint 5 planning starts fresh with the user
(UNDERSTAND/BRAINSTORM/PLAN).

## SESSION_PAUSE 2026-07-15 — Sprint 5 mid-B43 (SHIP), user asked to stop here
User asked to save state and end the session mid-batch. Sprint 5 (Epic 8: Craft Certificates) is
functionally complete and shipped through CI: Batches 37-42 all done (certificate domain/migration,
public verification endpoint, frontend issue/view UI with QR code, public verification page, VERIFY,
CI green at run 29375650649). Batch 43 (SHIP) has the E2E suite written, verified (alone and as part of
all 5 specs together), and its video saved to `.recordings/v0.5-2026-07-15.webm` — committed as 43919bb.
Not yet done, and the exact resume point for next session:
1. Write the Sprint 5 retro in .logs/activity.md (pattern: see "RETRO 2026-07-14" for Sprint 4's).
2. Write a proper SESSION_END entry in this file replacing/following this pause note.
3. Final push of the commits already made this session (E2E + doc update, hash 43919bb, plus everything
   from Batches 37-42 already pushed earlier) — check `git log origin/main..HEAD` first since some of this
   may already be on origin.
4. Monitor CI on that final push per rule 11.
No code changes needed — everything from Batches 37-42 is already verified and pushed; batch 43 only needs
its closing paperwork (retro, session log, confirm-push) to formally close Sprint 5.
