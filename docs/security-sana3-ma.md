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
- **Resource-level checks (Sprint 1-3)**: artisan profile endpoints checked `profile.userId ==
  authenticatedUser.id` — a hard 1:1, one user per profile.
- **Resource-level checks (Sprint 4, Batch 31)**: superseded by cooperative membership. Every artisan-only
  endpoint now resolves `cooperative_members.artisan_profile_id` for the authenticated user and compares
  that against the resource, not a direct user-id match — the same check shape, just one level of
  indirection added so multiple users can share one profile. `artisan_profiles.user_id` no longer exists
  (dropped in the same batch); membership is the sole ownership source now.
- **Two-tier permission within a cooperative (Batch 32)**: OWNER (auto-assigned to whoever creates the
  profile) can invite/remove members; both OWNER and MEMBER have equal access to manage products, orders,
  and shared profile fields — no finer-grained permission matrix (see
  docs/stories-sana3-ma-sprint4.md Assumed Default #3).

## 5. Data Protection
- **PII fields**: email, phone, contact address (artisan profile)
- **Public artisan directory (Sprint 2, Batch 13)**: `GET /api/v1/products` and `GET /api/v1/products/{id}`
  are unauthenticated and embed an artisan summary alongside each product. This is the "public artisan
  directory" feature docs/database-sana3-ma.md §7 anticipated — the summary is built from an explicit
  allowlist (`displayName`, `craftType`, `region`), never the full `ArtisanProfile` — `contactPhone` and the
  owning user's `email` are structurally excluded (the response DTO has no field for them, not a
  runtime filter), so there's no risk of a future field addition to `ArtisanProfile` silently leaking into
  the public API.
- **Product image upload (Sprint 2, Batch 14)**: `POST /api/v1/artisan-profiles/me/products/{id}/image`
  accepts a multipart file, `GET /api/v1/products/images/{filename}` serves it back publicly. Mitigations:
  content-type allowlisted to `image/jpeg`, `image/png`, `image/webp` at the application layer (SVG
  deliberately excluded — a common image-upload XSS vector via embedded scripts); the stored filename is
  always server-generated (a random UUID + an extension derived from the *validated* content type, never
  the client-supplied filename or its extension); the serving endpoint always sets its own `Content-Type`
  from the stored file's extension, never reflecting a client-supplied header; path-traversal-safe
  resolution on both write and read (`Path.resolve(...).normalize()` checked against the upload root);
  `spring.servlet.multipart.max-file-size` caps uploads at 5MB.
- **Artisan-facing order fulfillment (Sprint 3, Batch 23)**: `GET /api/v1/artisan-profiles/me/orders`
  deliberately exposes the buyer's `email` and the order's `shippingAddress` — real PII, but Story 6.3's own
  AC requires it so an artisan knows who/where to ship to (a genuine business need, not an oversight).
  Scoped narrowly: an artisan only ever sees `order_items` where `artisanProfileId` matches their own
  profile (never another artisan's lines on a multi-artisan order, and never the buyer's `contactPhone`,
  which doesn't exist on a BUYER-role user record). `POST .../{id}/complete` has the same ownership check
  before allowing a status change.
- **Cooperative membership PII (Sprint 4, Batch 32)**: `GET /api/v1/artisan-profiles/me/members` exposes
  each member's `email` to every other member of the same cooperative — new cross-user visibility that
  didn't exist before Sprint 4 (previously no user could see another user's email except an artisan
  fulfilling that specific buyer's order, per Batch 23 above). Intentional and proportionate — people who
  jointly run a shop reasonably know who else has access to it — but a real PII exposure, not an oversight.
  Invite creation (`POST .../members/invites`) is a minor enumeration surface: the handler returns the same
  `INVITEE_NOT_ELIGIBLE` error whether the email doesn't exist at all or exists but isn't an ARTISAN account,
  rather than distinguishing the two, so an inviter can't use it to probe arbitrary emails for account
  existence/role.
- **Public certificate verification (Sprint 5, Batch 37-38)**: `GET /api/v1/certificates/verify/{code}` is
  unauthenticated by design (that's the point — anyone scanning a QR code can verify it) and returns the
  same public allowlist Sprint 2 Batch 13 already established for product summaries (artisan display name,
  product name/craft type) — never `contactPhone`/email. The code itself is the certificate's own UUID
  primary key, not a separate secret column — its only job is being unguessable enough that verification
  requires actually having scanned/received it, not proving cryptographic authenticity (see
  docs/stories-sana3-ma-sprint5.md Assumed Default #3 for why a signed scheme wasn't needed this sprint). A
  malformed or unknown code both return the same 404, so the endpoint can't be used to distinguish
  "well-formed but wrong" from "garbage input."
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
