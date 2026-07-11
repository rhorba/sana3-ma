# Stories: Sana3.ma — Sprint 2 (Product Catalog & Browsing)
**PRD**: docs/prd-sana3-ma.md (§4 "Out of Scope (future sprints)" — "Product catalog & browsing" is first item)
**Architecture**: docs/architecture-sana3-ma.md (ADR-1 explicitly names "catalog" as an anticipated bounded context)
**Continues epic numbering from**: docs/stories-sana3-ma.md (Sprint 1: Epic 1 Auth, Epic 2 Artisan Profile)

## Scope Boundary
In scope: artisan self-service product CRUD, public browsing/search, single product image.
Out of scope (unchanged from PRD, still future sprints): cart, orders, checkout, payment, QR certificates,
DHL export, cooperative accounts. Product catalog & browsing is listed as a distinct PRD bullet from
"Orders, checkout, payment" — this sprint stays on the catalog/browsing side of that line.

## Assumed Defaults (stated, not yet confirmed — flag any you want changed)
These fill gaps the foundation docs don't answer, since Sprint 1 never specified product-catalog details.
Each is the YAGNI-simplest option that doesn't block a sane Sprint 3 (orders):
1. **Browsing is public, no login required** — matches PRD's buyer story ("register... to *later* browse")
   and standard marketplace discovery UX. Product detail pages are public; nothing requiring auth in this
   sprint (that's what "orders" will need).
2. **Self-publish, no moderation** — matches the existing artisan-profile pattern (Story 2.1). ADMIN role
   stays reserved/unused, as it already is per docs/security-sana3-ma.md §4.
3. **One image per product, stored on a local disk volume** (not S3/MinIO) — YAGNI; the existing Docker
   Compose setup already has a `sana3-postgres-data` named volume pattern to copy. Revisit if/when deploying
   beyond a single host becomes real (same reasoning docs/database-sana3-ma.md already used for deferring
   managed Postgres).
4. **Product category = free-text `craftType`, defaulting to the artisan's own `craft_type`** but editable
   per-product (an artisan selling mostly pottery might list one leather item) — avoids building a taxonomy
   admin screen this sprint.

## New Public-Facing PII Consideration
docs/database-sana3-ma.md §7 excludes `artisan_profiles.contact_phone` from public endpoints "until a later
'public artisan directory' feature explicitly requires it" — product browsing is that feature. Product
listings/detail will expose a public-safe artisan summary (`displayName`, `craftType`, `region` only —
never `contactPhone` or `email`). Security doc to be updated in the batch that builds the public endpoints
(document-first, per project rule 12).

## Epic 3: Product Catalog (Artisan Self-Service)
Artisan users can create, edit, and manage their own product listings.

### Story 3.1: Create/update product
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As an artisan, I want to add and edit products, so that buyers can eventually find what I sell.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan
When they submit name, description, price, currency, craft type, and optionally an image
Then a products row is created or updated, owned by their artisan_profiles row
And price is stored as a positive decimal with an explicit currency (default MAD)
```
**Technical Notes**: `POST/PUT /api/v1/artisan-profiles/me/products[/{id}]`. Ownership via JWT subject ->
artisan_profiles.user_id -> products.artisan_profile_id, same pattern as Story 2.1. New `products` table
(Flyway V3), domain package `ma.sana3.domain.catalog` per ADR-1's bounded-context convention.

**Dependencies**: Story 2.1 (artisan must have a profile before listing products)

---

### Story 3.2: Delete/archive product
**Priority**: Must | **Size**: S | **Specialist**: Backend Dev

As an artisan, I want to remove a product, so that buyers don't see items I no longer sell.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan with a product they own
When they delete it
Then it no longer appears in their product list or public browsing
```
**Technical Notes**: `DELETE /api/v1/artisan-profiles/me/products/{id}`. Hard delete (no orders reference
products yet this sprint, so no soft-delete/FK-integrity need — revisit when Sprint 3 orders exist).

**Dependencies**: Story 3.1

---

### Story 3.3: List own products
**Priority**: Must | **Size**: S | **Specialist**: Backend Dev

As an artisan, I want to see all my products, so that I can manage my catalog.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan
When they request their product list
Then they see all their products, including drafts (n/a this sprint — self-publish, so just "all")
```
**Technical Notes**: `GET /api/v1/artisan-profiles/me/products`. CQRS query handler per ADR-1, same pattern
as Story 2.2.

**Dependencies**: Story 3.1

---

### Story 3.4: Angular "My Products" UI
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As an artisan, I want a screen to manage my products, so that I don't need the API directly.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan on /profile/products
When they add, edit, or delete a product
Then the list updates and matches what a buyer would see on the public listing
```
**Technical Notes**: New NgRx `catalog` feature slice (per ADR-2's anticipated future slice), Angular
Material form + image upload control. Route guarded by the existing `artisanGuard`.

**Dependencies**: Story 3.1, 3.2, 3.3

---

## Epic 4: Public Browsing & Search
Anyone (no login required) can browse and search published products.

### Story 4.1: Public product listing
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As a visitor, I want to browse products, so that I can discover artisans and crafts.

**Acceptance Criteria**:
```gherkin
Given any visitor (authenticated or not)
When they request the product listing
Then they see paginated products with name, price, craft type, and the owning artisan's public summary
  (displayName, craftType, region — never contactPhone or email)
```
**Technical Notes**: `GET /api/v1/products` — public, paginated (page/size query params, default 20).
Public-safe DTO composition per the security note above.

**Dependencies**: Story 3.1

---

### Story 4.2: Product detail
**Priority**: Must | **Size**: S | **Specialist**: Backend Dev

As a visitor, I want to view a single product's full detail, so that I can decide if I'm interested.

**Acceptance Criteria**:
```gherkin
Given any visitor
When they request a specific product by id
Then they see its full detail (description, image, price, artisan public summary)
And a request for a nonexistent product returns 404
```
**Technical Notes**: `GET /api/v1/products/{id}` — public.

**Dependencies**: Story 4.1

---

### Story 4.3: Search and filter
**Priority**: Should | **Size**: M | **Specialist**: Backend Dev

As a visitor, I want to filter/search products, so that I can find what I'm looking for faster.

**Acceptance Criteria**:
```gherkin
Given the product listing endpoint
When a visitor supplies craftType, region, minPrice, maxPrice, and/or a keyword (q, matches name/description)
Then only matching published products are returned, combinable (AND semantics)
```
**Technical Notes**: Extends Story 4.1's endpoint with query params rather than a separate endpoint. Region
filter joins to `artisan_profiles.region` (not products) — no geo/PostGIS radius search this sprint (that's
a real feature, not a query-param tweak; explicitly deferred, see Open Question below).

**Dependencies**: Story 4.1

---

### Story 4.4: Angular browse/search UI
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As a visitor, I want a product grid with filters, so that I can browse the marketplace.

**Acceptance Criteria**:
```gherkin
Given the /browse route
When a visitor applies filters or a search term
Then the grid updates to match, with an empty-state message if nothing matches
```
**Technical Notes**: New public route (no guard). Reuses the `catalog` NgRx slice from Story 3.4 (shared
product model, separate "browse" vs "mine" selectors).

**Dependencies**: Story 4.1, 4.3

---

### Story 4.5: Angular product detail page
**Priority**: Must | **Size**: S | **Specialist**: Frontend Dev

As a visitor, I want a product detail page, so that I can see everything about one item.

**Acceptance Criteria**:
```gherkin
Given a product id in the URL (/products/:id)
When the page loads
Then it shows full product detail and the artisan's public summary
And an invalid id shows a not-found state instead of an error
```
**Technical Notes**: Public route. Links from the browse grid (Story 4.4).

**Dependencies**: Story 4.2, 4.4

---

## Open Question (not blocking Sprint 2 start, flagged for Sprint 3 planning)
Geo-radius search ("artisans near me") using the already-enabled PostGIS `artisan_profiles.location` column
is natural future scope for this epic but is **not** included above — `location` is still unpopulated (no
story in Sprint 1 or this backlog sets it), so there's nothing to search against yet. Revisit once either
this or a later sprint adds a way to capture artisan location.

## Sprint Allocation
| Sprint | Stories | Estimated Effort |
|---|---|---|
| Sprint 2 | 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 4.5 | ~7-9 days |

## Release Gate Criteria (mirrors docs/test-strategy-sana3-ma.md §5's format for Sprint 1)
- [x] All 9 stories' acceptance scenarios pass (Playwright, e2e/tests/catalog-flows.spec.ts, 2026-07-11 —
      product CRUD, public browse/filter, product detail, not-found state, delete removes from both owner
      list and public browsing)
- [x] Combined unit + integration coverage ≥ 80% (89.7% at Batch 18, re-confirmed by CI at Batch 19)
- [x] No critical/high security findings open (Semgrep/Trivy/Gitleaks clean on the first run — see
      .logs/metrics.md Batch 18, no fixes needed unlike Sprint 1)
- [x] E2E happy path passes and is recorded (`.recordings/v0.2-2026-07-11.webm`)
- [x] CI green on the branch before SHIP (run 29157209571, all 5 jobs, first try)
- [x] Sprint 1's regression suite (e2e/tests/critical-flows.spec.ts) still passes unmodified — confirms
      Sprint 2 introduced no regressions to auth/profile
