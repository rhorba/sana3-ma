# Test Strategy: Sana3.ma
**Stories Reference**: docs/stories-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: Test Architect

## 1. Risk Assessment
| Component | Impact | Frequency | Complexity | Test Level |
|---|---|---|---|---|
| Auth (register/login/refresh) | H | H | M | Maximum |
| Artisan profile CRUD | M | H | L | High |
| JWT filter / authorization | H | H | M | Maximum |

## 2. Test Pyramid Targets
| Layer | Coverage Target | Tooling |
|---|---|---|
| Unit | ≥ 60% of business logic | JUnit 5 + Mockito (backend), Vitest (frontend, via Angular CLI default; Karma is deprecated upstream and no longer scaffolded by `ng new` as of Angular 22 — test syntax (describe/it/expect) unchanged) |
| Integration | ≥ 40% of API + DB layer | Spring Boot Test + Testcontainers (Postgres) |
| E2E | Critical happy paths only | Playwright (with video recording) |
| **Combined gate** | **≥ 80%** — non-negotiable | CI blocks merge if below |

## 3. ATDD Acceptance Scenarios (critical paths)
```gherkin
Feature: Registration and Login

  Scenario: Successful artisan registration
    Given a visitor on the registration page
    When they submit a valid email, password, and role "ARTISAN"
    Then their account is created and they are auto-logged in

  Scenario: Login with wrong password
    Given a registered user
    When they submit their email with an incorrect password
    Then they see a generic "invalid credentials" error and are not logged in

Feature: Artisan Profile

  Scenario: Artisan updates their profile
    Given a logged-in artisan with no existing profile
    When they fill in display name, craft type, and region and save
    Then the profile is created and displayed with the new values

  Scenario: Buyer cannot access artisan profile edit
    Given a logged-in buyer
    When they request the artisan profile edit endpoint
    Then they receive a 403 Forbidden response
```

## 4. Adversarial Checklist (high-risk components only)
- [ ] Input abuse: empty/oversized email & password, unicode in profile fields, SQL-injection-style payloads in text fields
- [ ] Auth abuse: expired token reuse, tampered JWT signature, login brute-force (rate limit check)
- [ ] Race conditions: concurrent profile update requests (last-write-wins acceptable, verify no data corruption)
- [ ] Business logic: role escalation attempt via registration payload (role field tampering to "ADMIN")

## 5. Release Gate Criteria
- [x] All acceptance scenarios pass (Playwright, e2e/tests/critical-flows.spec.ts, 2026-07-10)
- [x] Combined unit + integration coverage ≥ 80% (89.5% at Batch 8, re-confirmed by CI at Batch 9)
- [x] No critical/high security findings open (Semgrep/Trivy/Gitleaks clean — see .logs/metrics.md Batch 8)
- [x] E2E happy path passes and is recorded (`.recordings/v0.1-2026-07-10.webm`, actual date — original
      placeholder date of 07-11 in this doc was a Batch-1-session guess before the real sprint timeline)
- [x] CI green on the branch before SHIP (run 29083741944, all 5 jobs, first try)

### Test Strategy Validation Checklist
- [x] Every story maps to at least one acceptance scenario
- [x] Coverage gate ≥ 80% confirmed and CI-enforced
- [x] Adversarial review planned for high-risk components (auth, authorization)
- [x] Release gate criteria documented and agreed with user
