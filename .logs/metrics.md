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

