# Stories: Sana3.ma — Sprint 5 (QR-Authenticated Craft Certificates)
**PRD**: docs/prd-sana3-ma.md (§4 "Out of Scope (future sprints)" — "QR-authenticated craft certificates";
also the problem statement's "no...platform...with certified provenance")
**Architecture**: docs/architecture-sana3-ma.md (ADR-1 names "certification" as an anticipated bounded
context alongside catalog and orders, both now done — this is the third and last of the three contexts
Sprint 1 scaffolded the hexagonal module structure for)
**Continues epic numbering from**: docs/stories-sana3-ma-sprint4.md (Epic 7)

## Scope Boundary
In scope: an artisan (any cooperative member, per Sprint 4's equal-access model) issues a certificate of
authenticity for one of their own product listings; the certificate has a unique verification code; anyone
— buyer or otherwise, no login required — can verify a certificate's authenticity by its code via a public
page, reached by scanning a QR code that encodes that page's URL.
Out of scope, deferred to a later sprint: certificate revocation, re-issuance/versioning, per-unit
certificates tied to a specific purchased item rather than the product listing (see Assumed Default #1),
and any cryptographic signing scheme beyond an unguessable server-issued code (see Assumed Default #3).

## Why now, over the other options
The user chose this over the simpler-first recommendation (real payment gateway) — the same pattern as
Sprint 4's pick of cooperative accounts over payment. This is genuinely greenfield, like Sprint 4 was:
`docs/architecture-sana3-ma.md` ADR-1 named "certification" as one of three bounded contexts anticipated
from Sprint 1 (alongside catalog and orders, both shipped in Sprints 2-3) — this closes out that original
three-context plan rather than being a new, unplanned addition. No existing schema or domain code touches
certificates today (confirmed by grep — zero hits for "certificat" anywhere outside docs and the PRD
bullet).

## Assumed Defaults (stated, not yet confirmed — flag any you want changed)
1. **A certificate is issued per product listing, not per purchased unit or per order.** The PRD's own
   framing ("certified provenance" so buyers "can verify craft origin") is about trusting the artisan/craft
   before a purchase decision, not proving one specific physical item shipped to one specific buyer is
   genuine (that would need Sprint 3's `order_items` as a hard dependency and is a materially different,
   bigger feature — deferred, see Scope Boundary). A product has at most one certificate.
2. **Any cooperative member can issue a certificate for a shared product** (not OWNER-only) — same
   equal-access default Sprint 4 established for product management generally; no reason to gatekeep this
   one action differently.
3. **Verification is by unguessable server-issued code, not cryptographic signing.** The certificate's
   authenticity comes from the code only existing in Sana3.ma's own database (issued to a verified,
   registered artisan) — a public lookup-by-code endpoint is sufficient "authentication" for this sprint's
   threat model. A signed/keyed scheme (e.g., HMAC, asymmetric signatures) is real added security-engineering
   depth this sprint doesn't need yet; revisit if certificates need to be verifiable *offline* or without
   trusting Sana3.ma's server availability.
4. **Issuing is idempotent, not a one-shot action**: calling "issue" on a product that already has one
   returns the existing certificate rather than erroring. Avoids a confusing "already exists" failure mode
   for an artisan who forgot they'd already issued one.
5. **The QR code is rendered client-side** from the verification URL (`/certificates/verify/{code}`) using a
   small new frontend-only npm dependency (the `qrcode` package, MIT-licensed) — no backend QR-image
   generation, no image storage, no new upload/serving surface like Sprint 2's product-image feature
   needed. This is the one new dependency this sprint adds; flagging it here per document-first practice
   rather than adding it silently mid-batch.

## New PII/Ownership Consideration
None beyond what Sprint 2 already established for public product data. The public verification response
exposes the artisan's `displayName` and the product's `name`/`craftType` — the same allowlisted public
summary fields Sprint 2's Batch 13 already decided are safe to expose unauthenticated (never `contactPhone`
or email). No new PII surface.

## Epic 8: Craft Certificates

### Story 8.1: Issue (or fetch existing) a certificate for a product
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As an artisan (or cooperative member), I want to issue a certificate of authenticity for my product, so
that buyers can verify it's genuine.

**Acceptance Criteria**:
```gherkin
Given a logged-in cooperative member and a product they (their cooperative) own
When they request a certificate for that product
Then a certificate is created with a unique verification code, or the existing one is returned if already
  issued
And requesting a certificate for a product belonging to a different cooperative is rejected the same way
  other product actions already are (404, no ownership info disclosed)
```
**Technical Notes**: New `ma.sana3.domain.certification` package (new bounded context, per ADR-1) —
`CraftCertificate` entity (id, productId, artisanProfileId, verificationCode, issuedAt), `UNIQUE(product_id)`
at the schema level enforcing Assumed Default #1. `POST /api/v1/artisan-profiles/me/products/{id}/certificate`,
reusing the membership-based ownership check pattern from Sprint 4 Batch 31.

**Dependencies**: Sprint 2 Story 3.1 (products must exist), Sprint 4 Story 7.2 (membership-based ownership)

---

### Story 8.2: Public certificate verification
**Priority**: Must | **Size**: S | **Specialist**: Backend Dev

As anyone (no account needed), I want to verify a certificate by its code, so that I can trust a craft's
authenticity before buying.

**Acceptance Criteria**:
```gherkin
Given a valid verification code
When an unauthenticated request looks it up
Then the response shows the artisan's display name, the product's name and craft type, and when it was
  issued
And an unknown or malformed code returns a clear "not found" response, not a server error
```
**Technical Notes**: `GET /api/v1/certificates/verify/{code}`, public (no auth), mirrors Sprint 2 Batch 13's
public-product-summary allowlist pattern (no `contactPhone`/email ever included).

**Dependencies**: Story 8.1

---

### Story 8.3: Angular — issue/view certificate with QR code (artisan side)
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As an artisan, I want to issue a certificate from my product list and get a QR code, so that I can share
or print it.

**Acceptance Criteria**:
```gherkin
Given the existing /profile/products page
When a cooperative member issues a certificate for one of their products
Then they see the verification code and a scannable QR code encoding the public verification URL
```
**Technical Notes**: New `certificate` NgRx slice (issue/get, minimal — one action pair, per the
incremental-slice pattern every prior sprint used). QR rendering via the new `qrcode` npm dependency
(Assumed Default #5).

**Dependencies**: Story 8.1

---

### Story 8.4: Angular — public verification page
**Priority**: Must | **Size**: S | **Specialist**: Frontend Dev

As a buyer scanning a certificate's QR code, I want to see a clear verification result, so that I can trust
what I'm buying.

**Acceptance Criteria**:
```gherkin
Given the /certificates/verify/:code route (no login required)
When it loads
Then it shows the artisan/product/issue-date on a valid code, or a clear "not a valid certificate" message
  on an invalid one
```
**Technical Notes**: No route guard (public, like `/browse` and `/products/:id`).

**Dependencies**: Story 8.2

---

## Open Questions (not blocking Sprint 5 start, flagged for later)
- **Per-purchase certificates**: deferred per Assumed Default #1. Revisit if product-level certification
  proves insufficient for real anti-counterfeiting needs (e.g., a bad actor could photograph a genuine QR
  code and pair it with a counterfeit physical item, since the code isn't bound to a specific sale).
- **Cryptographic signing**: deferred per Assumed Default #3. Revisit only if there's a stated need to
  verify a certificate without trusting Sana3.ma's own server (e.g., a customs/export authority wanting
  offline verification — this would also feed into the still-not-built DHL export integration bullet).
- **Certificate revocation**: no story this sprint. Revisit if an artisan ever needs to invalidate a
  previously issued certificate (e.g., issued in error).

## Sprint Allocation
| Sprint | Stories | Estimated Effort |
|---|---|---|
| Sprint 5 | 8.1, 8.2, 8.3, 8.4 | ~5-7 days |
