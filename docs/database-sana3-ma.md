# Database Design: Sana3.ma
**Architecture Reference**: docs/architecture-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: DBA

## 1. Database Selection
- **Engine**: PostgreSQL 16 + PostGIS extension
- **Rationale**: YAGNI default is PostgreSQL; PostGIS enabled now (near-zero cost) since artisan region/origin geodata is a near-term roadmap feature (avoids a disruptive extension-add migration later)
- **Hosting**: Dockerized Postgres container (sprint 1); managed Postgres (e.g. RDS/Cloud SQL) to be revisited at DevOps SDR when moving to real staging/prod traffic

## 2. Entity-Relationship Model
```
users ──N:1──> roles                        (via users.role, enum-backed)
users ──0:1──> cooperative_members ──N:1──> artisan_profiles   (Sprint 4: many users per profile)
artisan_profiles ──1:N──> products
users ──1:N──> orders             (buyer)
orders ──1:N──> order_items
products ──0:N──> order_items     (ON DELETE SET NULL — snapshot columns preserve history)
artisan_profiles ──1:N──> order_items
products ──0:1──> craft_certificates   (Sprint 5: at most one certificate per product)
artisan_profiles ──1:N──> craft_certificates
```
As of Sprint 4 (Batch 30), `artisan_profiles` no longer has a direct `user_id` FK — `cooperative_members`
is the sole source of truth for who can act on a profile, with a `UNIQUE(user_id)` constraint preserving
the "one user, one cooperative at a time" rule (Assumed Default #2,
docs/stories-sana3-ma-sprint4.md). The old 1:1 `users -> artisan_profiles` link is superseded, not kept
alongside the new table — see Batch 31 for the migration that drops `artisan_profiles.user_id`.

## 3. Schema Design
```sql
-- Table: users
CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role          VARCHAR(20)  NOT NULL CHECK (role IN ('BUYER','ARTISAN','ADMIN')),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Table: artisan_profiles
CREATE TABLE artisan_profiles (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  display_name  VARCHAR(150) NOT NULL,
  craft_type    VARCHAR(100) NOT NULL,
  region        VARCHAR(100),
  bio           TEXT,
  contact_phone VARCHAR(30),
  location      GEOGRAPHY(POINT, 4326),  -- PostGIS, nullable, populated in later sprint
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Table: products (Sprint 2, Batch 11)
CREATE TABLE products (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  artisan_profile_id UUID NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
  name              VARCHAR(150) NOT NULL,
  description       TEXT,
  price_amount      NUMERIC(10,2) NOT NULL CHECK (price_amount > 0),
  price_currency    VARCHAR(3) NOT NULL DEFAULT 'MAD',
  craft_type        VARCHAR(100) NOT NULL,
  image_url         VARCHAR(500),
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```
No `status`/moderation column — Sprint 2's self-publish default (docs/stories-sana3-ma-sprint2.md) means every
product is immediately live, so there's nothing to track.

```sql
-- Table: orders (Sprint 3, Batch 21)
CREATE TABLE orders (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  buyer_user_id     UUID NOT NULL REFERENCES users(id),
  status            VARCHAR(20) NOT NULL CHECK (status IN ('PLACED','COMPLETED','CANCELLED')),
  shipping_address  TEXT NOT NULL,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Table: order_items (Sprint 3, Batch 21)
CREATE TABLE order_items (
  id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id                 UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id               UUID REFERENCES products(id) ON DELETE SET NULL,
  product_name_snapshot    VARCHAR(150) NOT NULL,
  price_amount_snapshot    NUMERIC(10,2) NOT NULL CHECK (price_amount_snapshot > 0),
  price_currency_snapshot  VARCHAR(3) NOT NULL,
  craft_type_snapshot      VARCHAR(100) NOT NULL,
  artisan_profile_id       UUID NOT NULL REFERENCES artisan_profiles(id),
  quantity                 INTEGER NOT NULL CHECK (quantity > 0),
  completed_at             TIMESTAMPTZ,
  created_at                TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```
Line items snapshot `product_name`/`price`/`currency`/`craft_type` at order time rather than joining live to
`products`, so a product can still be hard-deleted (`ON DELETE SET NULL` on `product_id`) without corrupting
past order history. `artisan_profile_id` is denormalized onto each line item (not derived via `product_id`)
so an artisan's fulfillment queue survives product deletion too, and so each line item can be
completed/cancelled independently per-artisan on a multi-artisan order (YAGNI default per
docs/stories-sana3-ma-sprint3.md — no per-order split-shipment UI yet, but the data model doesn't block it).

```sql
-- Table: cooperative_members (Sprint 4, Batch 30)
CREATE TABLE cooperative_members (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id             UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  artisan_profile_id  UUID NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
  role                VARCHAR(20) NOT NULL CHECK (role IN ('OWNER','MEMBER')),
  joined_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```
`UNIQUE(user_id)` enforces Assumed Default #2 (one cooperative per user at a time) at the schema level, not
just in application code — the same enforcement style Sprint 1 used for `artisan_profiles.user_id`, just
moved to a table that allows many rows per `artisan_profile_id`. Backfilled from every existing
`artisan_profiles` row (owner becomes `role='OWNER'`) in the same migration that creates the table.

```sql
-- Table: craft_certificates (Sprint 5, Batch 37)
CREATE TABLE craft_certificates (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id          UUID NOT NULL UNIQUE REFERENCES products(id) ON DELETE CASCADE,
  artisan_profile_id  UUID NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
  issued_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```
`UNIQUE(product_id)` enforces Assumed Default #1 (one certificate per product listing, not per purchased
unit — docs/stories-sana3-ma-sprint5.md) at the schema level. The certificate's own `id` doubles as its
public verification code (no separate code column) — it's already an unguessable UUID serving as primary
key, so a distinct field would be redundant (Sprint 5 PLAN reasoning). `ON DELETE CASCADE` on both FKs
means a certificate can never outlive the product or profile it certifies, so `VerifyCertificateHandler`
treats a missing product/profile lookup as a data-integrity bug, not a normal "not found."

## 4. Index Strategy
| Table | Index Name | Columns | Query Pattern |
|---|---|---|---|
| users | idx_users_email | (email) | login lookup by email (also enforced by UNIQUE) |
| products | idx_products_artisan_profile_id | (artisan_profile_id) | list an artisan's own products; ownership joins on write endpoints |
| orders | idx_orders_buyer_user_id | (buyer_user_id) | buyer's own order history |
| order_items | idx_order_items_order_id | (order_id) | line items for one order |
| order_items | idx_order_items_artisan_profile_id | (artisan_profile_id) | artisan's fulfillment queue across all orders |
| cooperative_members | idx_cooperative_members_artisan_profile_id | (artisan_profile_id) | list all members of a cooperative (user_id lookup covered by its UNIQUE constraint) |
| cooperative_invites | idx_cooperative_invites_invited_user_id | (invited_user_id) | an invitee's own pending invites |
| craft_certificates | — | (product_id lookup covered by its UNIQUE constraint; id lookup by primary key) | issue-or-fetch and public verification, no extra index needed |

Correction (Sprint 5): the `idx_artisan_profiles_user_id` row previously listed here was stale — Sprint 4
Batch 31 dropped `artisan_profiles.user_id` entirely (see §2 above); removed rather than left inaccurate.

Public browsing/search (Batch 13) currently runs an unindexed `LOWER(craft_type)`/`LOWER(region)` filter
and a `LIKE '%...%'` scan on `name`/`description` — acceptable at sprint-2 data volumes (no test/staging
data set beyond a handful of products), but the first thing to revisit if search feels slow: a
case-insensitive index on `products.craft_type` and `artisan_profiles.region`, and full-text search
(`tsvector`) instead of `LIKE` for the keyword filter.

## 5. Migration Plan
| Migration File | Description | Reversible |
|---|---|---|
| V1__init_users_and_roles.sql | Create `users` table + role check constraint | Yes (DROP TABLE) |
| V2__create_artisan_profiles.sql | Create `artisan_profiles` table + PostGIS extension enable | Yes (DROP TABLE / DROP EXTENSION) |
| V3__create_products.sql | Create `products` table, FK to `artisan_profiles` (ON DELETE CASCADE) | Yes (DROP TABLE) |
| V4__create_orders.sql | Create `orders` + `order_items` tables, snapshot columns, FK to `products` (ON DELETE SET NULL) | Yes (DROP TABLE) |
| V5__create_cooperative_members.sql | Create `cooperative_members` table, backfill from existing `artisan_profiles` owners | Yes (DROP TABLE) |
| V6__drop_artisan_profiles_user_id.sql (Batch 31) | Drop `artisan_profiles.user_id` now that `cooperative_members` is the sole ownership source | No (would need to reconstruct from `cooperative_members` OWNER rows) |
| V7__create_cooperative_invites.sql (Batch 32) | Create `cooperative_invites` table (invite/accept/decline lifecycle) | Yes (DROP TABLE) |
| V8__create_craft_certificates.sql (Batch 37) | Create `craft_certificates` table, `UNIQUE(product_id)` | Yes (DROP TABLE) |

## 6. Access Patterns
| Use Case | Query Pattern | Index Coverage |
|---|---|---|
| Login | SELECT by email | idx_users_email |
| Get own profile | SELECT membership by user_id, then profile by id | membership's UNIQUE(user_id), primary key |
| Update own profile | Same resolution, then UPDATE by id | membership's UNIQUE(user_id), primary key |
| List own products | SELECT by artisan_profile_id | idx_products_artisan_profile_id |
| Issue-or-fetch a certificate | SELECT by product_id | craft_certificates' UNIQUE(product_id) |
| Public certificate verification | SELECT certificate by id, then product/profile by id | primary keys |
| Public browse/search | SELECT products JOIN artisan_profiles, filtered + paginated | none yet (see §4) |
| Product detail | SELECT product by id + artisan_profiles by id | primary keys |
| Place order | INSERT order + order_items in one transaction | primary keys |
| Buyer order history | SELECT orders by buyer_user_id, JOIN order_items by order_id | idx_orders_buyer_user_id, idx_order_items_order_id |
| Artisan fulfillment queue | SELECT order_items by artisan_profile_id | idx_order_items_artisan_profile_id |
| Resolve authenticated user's cooperative (Batch 31 replaces old profile-by-user_id lookup) | SELECT cooperative_members by user_id | UNIQUE(user_id) |
| List a cooperative's members | SELECT cooperative_members by artisan_profile_id | idx_cooperative_members_artisan_profile_id |

## 7. Sensitive Data
- Columns requiring protection: `password_hash` (bcrypt, never returned in API responses), `contact_phone`
  (PII). The "public artisan directory" feature this was flagged against has now arrived (Sprint 2 Batch 13,
  `GET /api/v1/products`) — it still excludes `contact_phone` and `users.email`, since public browsing only
  needed `displayName`/`craftType`/`region` to show "sold by X in Y" on a listing. See
  docs/security-sana3-ma.md §5 for the allowlist-shaped DTO that enforces this structurally.
- `orders.shipping_address` is PII (Sprint 3 Batch 21), and Batch 23's Story 6.3 AC explicitly requires
  artisans to see it ("buyer contact/shipping info") to know where to ship — so
  `GET /api/v1/artisan-profiles/me/orders` intentionally returns it, along with `users.email` as the buyer's
  contact (the only contact field a BUYER-role user record has; `contact_phone` stays ARTISAN-only and is
  never exposed here). Both are scoped strictly to order items the calling artisan's own products appear on
  (`order_items.artisan_profile_id` ownership check) — an artisan never sees another artisan's line items or
  the buyer info behind them.
- Row-level security: not needed yet (single-tenant access pattern — users only ever query their own row via `user_id` from JWT claim)
