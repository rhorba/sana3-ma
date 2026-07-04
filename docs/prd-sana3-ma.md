# PRD: Sana3.ma — Moroccan Artisan Marketplace
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: PM | **Status**: Draft

## 1. Problem Statement
Moroccan artisans (zellige, leather, carpets, woodwork) sell through informal channels with no authenticity guarantee, limiting their reach to buyers who can't verify craft origin or quality. There is no digital platform connecting artisans/cooperatives directly to domestic and export buyers with certified provenance.

## 2. Goals & Success Metrics
| Goal | Metric | Target (Sprint 1) |
|---|---|---|
| Artisans can create a verifiable digital identity | # artisan profiles created | 10 test profiles |
| Secure account access for all roles | Auth success rate, no unauthorized access | 100% pass on security tests |
| Foundation ready for catalog/certs (later sprints) | Architecture supports planned features w/o rework | Architecture review sign-off |

## 3. User Stories (Sprint 1 scope)
As an **artisan**, I want to register an account and create my profile, so that buyers can find and trust me.
As a **buyer**, I want to register and log in, so that I can later browse artisans and products.
As an **admin**, I want artisans to authenticate securely, so that platform trust is maintained.

- [ ] Story 1: User registration (artisan or buyer role) with email + password
- [ ] Story 2: User login (JWT-based session)
- [ ] Story 3: Artisan profile creation/edit (name, craft type, region, bio, contact)
- [ ] Story 4: View own artisan profile

## 4. Scope
### In Scope (Sprint 1)
- Registration & login (JWT auth), roles: BUYER, ARTISAN, ADMIN
- Artisan profile CRUD (self-service)
- Docker Compose local/staging deploy
- CI pipeline (lint, test, security scan, build)

### Out of Scope (future sprints)
- Product catalog & browsing
- QR-authenticated craft certificates
- Orders, checkout, CMI/Stripe payment
- DHL export integration
- Cooperative multi-user accounts
- Kubernetes (only if real scale need appears)

## 5. Requirements
### Functional
- FR-1: Users register with email, password, and role (BUYER or ARTISAN)
- FR-2: Users log in and receive a JWT access token (+ refresh token)
- FR-3: Artisan users can create/edit a profile (craft type, region, bio, contact info)
- FR-4: Artisan users can view their own profile
- FR-5: Admin role exists for future moderation (no admin UI required yet)

### Non-Functional
- NFR-1: Performance — API p99 < 300ms for auth/profile endpoints under sprint-1 load (<100 concurrent users)
- NFR-2: Security — passwords hashed (bcrypt), JWT signed, HTTPS enforced, OWASP Top 10 baseline covered
- NFR-3: Accessibility — WCAG AA on Angular forms (labels, contrast, keyboard nav)

## 6. Constraints & Assumptions
- Stack: Java Spring Boot (latest LTS-compatible release) backend, Angular (latest) frontend, PostgreSQL 16 + PostGIS
- Architecture: hexagonal (ports & adapters) + CQRS-lite backend, NgRx frontend (user-chosen comprehensive approach, see architecture doc ADR-1)
- Deploy target sprint 1: Docker Compose only; Kubernetes deferred
- Env vars ship with dev-safe default values in `.env.example` (no secrets requested from user this session)
- Every sprint ends with `git push origin <branch>`; CI must be green before a task is considered done
- Video recording of E2E flows required at each project version completion

## 7. Risks
| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| Comprehensive architecture (hexagonal+CQRS+NgRx) adds sprint-1 overhead vs. simple CRUD need | M | M | Time-box scaffolding; re-evaluate at sprint retro if velocity suffers |
| PostGIS unused until catalog/region features land | H | L | Enable extension now, no schema cost, avoids later migration |
| No remote CI configured yet | M | M | Set up GitHub Actions in DevOps doc before first push |

## 8. Timeline
| Milestone | Target Date |
|---|---|
| PRD Approved | 2026-07-04 |
| Foundation docs approved | 2026-07-04 |
| Architecture Done | 2026-07-04 |
| Implementation Start | 2026-07-05 |
| Sprint 1 (Auth + Artisan Profile) Ready | 2026-07-11 |
