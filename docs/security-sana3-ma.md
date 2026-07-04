# Security Baseline: Sana3.ma
**Architecture Reference**: docs/architecture-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: Security Engineer

## 1. Threat Model (5-Minute)
- **What are we building?** A marketplace auth system + artisan self-service profiles (sprint 1), foundation for a payments/export platform later.
- **Who would attack it?** Opportunistic script kiddies (credential stuffing, scraping), competitors (data scraping), later: fraud actors around payments/export (out of scope this sprint).
- **Worst outcome?** Account takeover, PII leak (email, phone, address), unauthorized profile tampering.

## 2. STRIDE Analysis (top risks only)
| Threat | Component | Mitigation | Status |
|---|---|---|---|
| Spoofing | Login endpoint | Rate limiting, bcrypt + JWT, no username enumeration in error messages | TODO |
| Tampering | Profile update endpoint | Ownership check (JWT subject == profile owner), server-side validation | TODO |
| Repudiation | Auth actions | Structured audit log (login, register, profile update) with timestamps | TODO |
| Info Disclosure | API error responses | Generic error messages, no stack traces in prod, PII excluded from logs | TODO |
| DoS | /auth/login, /auth/register | Rate limiting (per-IP), request size limits | TODO |
| Elevation of Privilege | Role assignment at registration | Role fixed at registration (BUYER/ARTISAN only); ADMIN never self-assignable via API | TODO |

## 3. Authentication Strategy
- **Type**: JWT (access token 15 min, refresh token 7 days, httpOnly secure cookie)
- **MFA**: Not required for sprint 1 (low-risk profile data); revisit before payments/export sprints
- **Password policy**: min 10 chars, bcrypt (cost factor 12), no composition rules beyond length (per current OWASP guidance)
- **Session management**: refresh token httpOnly + SameSite=Strict cookie; access token in memory (not localStorage) on Angular side

## 4. Authorization Model
- **Pattern**: Simple RBAC
- **Roles defined**: BUYER, ARTISAN, ADMIN (ADMIN unused this sprint, reserved for moderation later)
- **Resource-level checks**: Yes — artisan profile endpoints check `profile.userId == authenticatedUser.id`

## 5. Data Protection
- **PII fields**: email, phone, contact address (artisan profile)
- **Encryption at rest**: Postgres volume encryption deferred to hosting provider (Docker Compose dev = not encrypted; note for staging/prod hosting choice)
- **Encryption in transit**: HTTPS enforced in staging/prod (local Docker Compose may run HTTP for simplicity, documented as dev-only)
- **Secrets management**: env vars via `.env` (git-ignored), dev-safe defaults in `.env.example`, real secrets injected via CI/CD secrets store at deploy time — never committed

## 6. Security Requirements for Dev Team
- [ ] All inputs validated server-side (Bean Validation annotations on DTOs)
- [ ] Output encoded for context (Angular auto-escapes templates; JSON API has no HTML injection surface)
- [ ] No secrets in code, logs, or error messages
- [ ] HTTPS only in staging/prod, security headers configured (HSTS, X-Content-Type-Options, X-Frame-Options)
- [ ] Dependencies scanned in CI (Trivy for Maven + npm deps)

### Security Validation Checklist
- [x] Threat model completed and top risks addressed
- [x] Auth strategy chosen and justified (JWT access+refresh)
- [x] Authorization model defined with roles (BUYER/ARTISAN/ADMIN)
- [x] PII fields identified with protection plan
- [x] Security requirements handed off to dev team (see section 6, enforced in EXECUTE + VERIFY phases)
