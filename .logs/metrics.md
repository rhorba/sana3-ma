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

