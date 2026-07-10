# Architecture: Sana3.ma
**PRD Reference**: docs/prd-sana3-ma.md
**System Design Reference**: docs/system-design-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: Software Architect

## 1. Overview
Backend: Java Spring Boot, hexagonal (ports & adapters), multi-module Maven, CQRS-lite for artisan-profile reads/writes. Frontend: Angular (latest), standalone components, NgRx store. Chosen by user as the "comprehensive" option in BRAINSTORM, anticipating future multi-team/bounded-context growth (catalog, certs, orders).

## 2. Architecture Decision Records

### ADR-1: Hexagonal + CQRS-lite backend (multi-module Maven)
- **Status**: Accepted
- **Context**: Sprint 1 only needs auth + profile CRUD, but the user wants the codebase ready for multiple bounded contexts (catalog, certification, orders) without a rewrite.
- **Decision**: Multi-module Maven: `domain` (framework-free entities/ports), `application` (use cases, command/query handlers), `adapter-web` (REST controllers), `adapter-persistence` (JPA repository implementations), `bootstrap` (Spring Boot main + wiring).
- **Alternatives**: Single-module layered monolith — simpler, rejected per user's explicit choice.
- **Consequences**: More upfront module/wiring work; clearer boundaries for future bounded contexts (each becomes its own top-level package, later extractable to its own module/service).

### ADR-2: Angular with NgRx store
- **Status**: Accepted
- **Context**: Multiple features (auth, profile, later catalog/orders) will need shared, predictable state.
- **Decision**: NgRx store from sprint 1, feature state slices (`auth`, `artisan-profile`), standalone components (no NgModules, per latest Angular idioms).
- **Alternatives**: Angular signals only — simpler, rejected per user's comprehensive choice.
- **Consequences**: More boilerplate (actions/reducers/effects) for a 2-feature sprint; pays off if state complexity grows.

### ADR-3: JWT authentication (access + refresh token)
- **Status**: Accepted
- **Context**: Stateless API auth needed for SPA + future mobile/API consumers.
- **Decision**: Spring Security + JWT (HS256 dev / RS256-ready), short-lived access token (15 min), refresh token (7 days, stored httpOnly cookie).
- **Alternatives**: Server-side sessions — rejected, doesn't fit stateless REST + future multi-client goal.
- **Consequences**: Must implement refresh flow and token revocation strategy (deferred: revocation list is a later-sprint concern, logged in `.logs/risks.md` if needed).

## 3. System Design
```
[Angular SPA: NgRx store]
        ↓ HTTP/JSON
[adapter-web: AuthController, ArtisanProfileController]
        ↓
[application: RegisterUserCommand, LoginQuery, UpdateProfileCommand, GetProfileQuery]
        ↓
[domain: User, ArtisanProfile, Role — pure Java, no Spring annotations]
        ↓ implements ports
[adapter-persistence: UserJpaRepository, ArtisanProfileJpaRepository]
        ↓
[PostgreSQL 16 + PostGIS]
```

## 4. Data Model
```
User ──1:1──> ArtisanProfile   (only when role = ARTISAN)
User ──N:1──> Role             (BUYER | ARTISAN | ADMIN)
```
Full schema owned by DBA — see docs/database-sana3-ma.md.

## 5. API Design
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | /api/v1/auth/register | Register user (role: BUYER/ARTISAN) | Public |
| POST | /api/v1/auth/login | Login, returns JWT + refresh token | Public |
| POST | /api/v1/auth/refresh | Exchange refresh token for new access token | Refresh token |
| POST | /api/v1/auth/logout | Expire the refresh cookie (Sprint 1 Batch 5) | Refresh token |
| GET | /api/v1/artisan-profiles/me | Get own artisan profile | Required (ARTISAN) |
| PUT | /api/v1/artisan-profiles/me | Create/update own artisan profile | Required (ARTISAN) |
| GET | /api/v1/artisan-profiles/me/products | List own products (Sprint 2 Batch 12) | Required (ARTISAN) |
| POST | /api/v1/artisan-profiles/me/products | Create a product (Sprint 2 Batch 12) | Required (ARTISAN) |
| PUT | /api/v1/artisan-profiles/me/products/{id} | Update own product (Sprint 2 Batch 12) | Required (ARTISAN) |
| DELETE | /api/v1/artisan-profiles/me/products/{id} | Delete own product (Sprint 2 Batch 12) | Required (ARTISAN) |
| GET | /api/v1/products | Public product listing, paginated + filterable (Sprint 2 Batch 13) | Public |
| GET | /api/v1/products/{id} | Public product detail (Sprint 2 Batch 13) | Public |

## 6. Security Considerations
See docs/security-sana3-ma.md for full threat model. Summary:
- Authentication: JWT (access + refresh)
- Authorization: role-based (BUYER, ARTISAN, ADMIN), resource ownership check on profile endpoints
- Data protection: bcrypt password hashing, HTTPS enforced in staging/prod, no PII in logs

## 7. Infrastructure
- Hosting: Docker Compose (local + staging), single host
- Database: PostgreSQL 16 + PostGIS container, Flyway migrations
- CI/CD: GitHub Actions (lint → test/coverage → security scan → build → deploy)
- Monitoring: Spring Boot Actuator health/metrics endpoints, container logs

## 8. Technical Risks
| Risk | Mitigation | Owner |
|---|---|---|
| Multi-module Maven setup slows sprint-1 velocity | Time-box scaffolding to first EXECUTE batch; simplify if it blocks progress | Tech Lead |
| NgRx boilerplate for only 2 features | Keep store minimal (2 feature slices), avoid premature generic abstractions | Frontend Dev |
| JWT refresh/revocation strategy incomplete | Documented as a follow-up risk, not blocking sprint-1 auth happy path | Security Engineer |
