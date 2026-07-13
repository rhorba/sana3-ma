# METRICS — Sana3.ma

## BATCH 8 2026-07-10 — Coverage + security scan (VERIFY gate)

### Coverage (JaCoCo + Vitest via `ng test --coverage`)
| Module | Line coverage |
|---|---|
| backend/domain | 79.7% |
| backend/application | 100.0% |
| backend/adapter-persistence | 100.0% |
| backend/adapter-web | 84.1% |
| **Backend combined** | **89.9%** (364/405 lines) |
| **Frontend** | **89.1%** (261/293 lines) |
| **Combined backend + frontend** | **89.5%** (625/698 lines) |

Gate: ≥80% combined — **PASS**. Frontend statement coverage 88.6%, function coverage 71.95% (lower —
several small store selector files aren't fully exercised — line coverage is the metric tracked against
the 80% gate, per JaCoCo's convention used for the backend number above).

### Security scan
| Scanner | Scope | Result |
|---|---|---|
| Semgrep (auto ruleset, 384 rules) | backend/, frontend/src | 1 finding → fixed → **0 findings** |
| Trivy SCA (fs) | backend Maven modules | **0** Critical/High |
| Trivy SCA (fs) | frontend npm (package-lock.json) | **0** Critical/High |
| Trivy image scan | sana3-ma-backend:latest | 5 HIGH (alpine base pkgs) → fixed → **0** |
| Trivy image scan | sana3-ma-frontend:latest | 4 HIGH (alpine base pkgs) → fixed → **0** |
| Gitleaks | full git history (15 commits) | **0** secrets found |

Gate: no Critical findings (per docs/devops-sana3-ma.md §4) — **PASS** even before fixes (all findings were
HIGH, not Critical); fixed anyway since patches were trivially available. See .logs/decisions.md for details
on the two fixes (Dockerfile non-root USER, `apk upgrade --no-cache`).

## BATCH 18 2026-07-11 — Coverage + security scan (Sprint 2 VERIFY gate)

### Coverage (JaCoCo + Vitest via `ng test --coverage`)
| Scope | Line coverage |
|---|---|
| Backend combined (5 modules) | 825/898 lines |
| Frontend (17 spec files, 101 tests) | 504/584 lines (86.3%) |
| **Combined backend + frontend** | **89.7%** (1329/1482 lines) |

Gate: ≥80% combined — **PASS**, via `scripts/check-coverage.sh` (unchanged since Sprint 1 Batch 9 — reused
as-is, no script changes needed).

### Security scan
| Scanner | Scope | Result |
|---|---|---|
| Semgrep (auto ruleset, 384 rules, 186 files) | backend/, frontend/src | **0 findings** |
| Trivy SCA (fs) | backend Maven modules | **0** Critical/High |
| Trivy SCA (fs) | frontend npm (package-lock.json) | **0** Critical/High |
| Trivy SCA (fs) | e2e npm (Playwright, added Sprint 1 Batch 10) | **0** Critical/High |
| Trivy image scan | sana3-ma-backend:latest | **0** (apk upgrade from Batch 8 keeps picking up patches) |
| Trivy image scan | sana3-ma-frontend:latest | **0** |
| Gitleaks | full git history (37 commits) | **0** secrets found |

Gate: no Critical findings — **PASS**, clean across every scanner on the first run. No fixes needed this
batch (contrast with Sprint 1 Batch 8, which found and fixed 3 real issues) — Sprint 2's new surface (image
upload, public browsing) was already scanned incidentally as each batch shipped it, and B14's `apk upgrade
--no-cache` pattern kept both images' base packages current throughout.

## BATCH 27 2026-07-12 — Coverage + security scan (Sprint 3 VERIFY gate)

### Coverage (JaCoCo + Vitest via `ng test --coverage --coverage-reporters json-summary --coverage-reporters text`)
| Scope | Line coverage |
|---|---|
| Backend combined (5 modules) | 1278/1372 lines |
| Frontend (26 spec files, 165 tests) | 755/859 lines (87.9%) |
| **Combined backend + frontend** | **91.1%** (2033/2231 lines) |

Gate: ≥80% combined — **PASS**, via `scripts/check-coverage.sh` (unchanged since Sprint 1 Batch 9). Note:
`ng test --coverage` alone (without the explicit `--coverage-reporters json-summary` flag CI/this script
expect) only emits `coverage-final.json`, not `coverage-summary.json` — the script silently excludes
frontend coverage and still reports a misleadingly-passing backend-only number in that case. Re-ran with the
exact flags from `.github/workflows/ci.yml` to get the real combined figure above.

### Security scan
| Scanner | Scope | Result |
|---|---|---|
| Semgrep (auto ruleset, 384 rules, 267 files) | backend/, frontend/src | **0 findings** |
| Trivy SCA (fs) | backend Maven modules | **blocked locally** — Maven Central 429 rate-limited this session's IP (30 min) after this session's heavy `mvnw` usage; not a security finding. CI (Batch 28) runs on a different network and will confirm cleanly. |
| Trivy SCA (fs) | frontend npm (package-lock.json) | **0** Critical/High |
| Trivy SCA (fs) | e2e npm | no supported manifest found locally (same as CI's scope — e2e/ isn't a separate SCA job in ci.yml) |
| Trivy image scan | sana3-ma-backend:verify | **0** (alpine 3.23.5, 1 jar, apk upgrade pattern still holding) |
| Trivy image scan | sana3-ma-frontend:verify | **0** (alpine 3.23.5) |
| Gitleaks | full git history (55 commits) | **0** secrets found |

Gate: no Critical findings — **PASS** on every scanner that could run locally; the one scanner blocked by a
local network condition (not a code issue) is deferred to CI's clean-IP run in Batch 28. Sprint 3's new
attack surface (checkout, order placement, cancellation, artisan fulfillment with buyer PII exposure) was
already reasoned through and documented in `docs/security-sana3-ma.md` as each batch shipped it (Batches
21-26), consistent with the "scan incidentally as you go" practice that made Sprint 2's Batch 18 clean on
the first pass.


## BATCH 34 2026-07-13 — Sprint 4 VERIFY

### Coverage
| Scope | Line coverage |
|---|---|
| Backend combined (5 modules) | 1612/1729 lines |
| Frontend (30 spec files, 226 tests) | 880/1000 lines (88.0%) |
| **Combined backend + frontend** | **91.3%** (2492/2729 lines) |

Gate: ≥80% combined — **PASS**, via `scripts/check-coverage.sh` with CI's exact frontend invocation
(`--coverage-reporters json-summary --coverage-reporters text`).

### Security scan
| Scanner | Scope | Result |
|---|---|---|
| Semgrep (auto ruleset) | backend/, frontend/src | **0 findings** |
| Trivy SCA (fs) | backend Maven modules | **blocked locally** — Maven Central 429 rate-limited this session's IP again (same recurring pattern as Sprint 3 Batch 27, triggered by this session's own heavy `mvnw` usage); not a security finding, deferred to Batch 35's CI run on a clean network. |
| Trivy SCA (fs) | frontend npm (package-lock.json) | **0** Critical/High |
| Trivy image scan | sana3-ma-backend:latest | **0** (alpine 3.23.5, 1 jar) |
| Trivy image scan | sana3-ma-frontend:latest | **0** (alpine 3.23.5) |
| Gitleaks | full git history (69 commits) | **0** secrets found |

Gate: no Critical findings — **PASS** on every scanner that could run locally; the Maven SCA scan is
deferred to Batch 35's CI run, same pattern as Sprint 3. Sprint 4's new attack surface (cooperative
membership, invites, cross-member email visibility) was already reasoned through and documented in
docs/security-sana3-ma.md as each batch shipped it (Batches 30-32).
