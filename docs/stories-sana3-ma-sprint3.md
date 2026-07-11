# Stories: Sana3.ma — Sprint 3 (Orders & Checkout)
**PRD**: docs/prd-sana3-ma.md (§4 "Out of Scope (future sprints)" — "Orders, checkout, CMI/Stripe payment")
**Architecture**: docs/architecture-sana3-ma.md (ADR-1 names "orders" as an anticipated bounded context,
alongside catalog — done in Sprint 2 — and certification, still future)
**Continues epic numbering from**: docs/stories-sana3-ma.md (Epic 1-2), docs/stories-sana3-ma-sprint2.md
(Epic 3-4)

## Scope Boundary
In scope: buyer-side cart, checkout (order placement), order history (buyer), order visibility + status
updates (artisan/seller side).
Out of scope, deferred to a later sprint: a **real** CMI/Stripe payment gateway integration, QR-authenticated
craft certificates, DHL export integration, cooperative multi-user accounts. The PRD bundles "Orders,
checkout, CMI/Stripe payment" as one bullet, but real payment gateway integration is a materially different
kind of work (external merchant account setup, sandbox/production credential handling, PCI-adjacent
concerns) from building the order lifecycle itself — splitting it out is a scope decision worth confirming
with you, not something to bundle silently. This sprint builds the full checkout *flow* ending in a placed
order; the payment step is explicitly a placeholder (see Assumed Defaults #4).

## Assumed Defaults (stated, not yet confirmed — flag any you want changed)
1. **Cart is client-side only** (NgRx + localStorage persistence so it survives a reload), not a backend
   `carts` table. Checkout submits the whole cart as one request; nothing is persisted server-side until an
   order is actually placed. Avoids a new bounded context/table for something with no cross-device-sync
   requirement stated anywhere.
2. **Order items snapshot product details** (name, price, currency, craft type) at order time rather than
   living-joining to `products`. This is what actually resolves the note Sprint 2 left itself ("Hard delete
   [on products] — revisit when Sprint 3 orders exist"): a product can still be safely hard-deleted later
   without corrupting historical orders, because the order doesn't depend on the product row still existing.
3. **Any authenticated user can place an order**, not just BUYER — an artisan buying another artisan's
   product is a real scenario in a peer marketplace, and there's no stated reason to block it. (Creating a
   *product* stays ARTISAN-only, unchanged.)
4. **No real payment gateway this sprint.** Checkout places the order directly (status `PLACED`) with no
   payment step — the closest real-world equivalent is cash-on-delivery/pay-on-pickup, common for a
   regional artisan marketplace. `docs/security-sana3-ma.md`'s "foundation for a payments/export platform
   later" framing already anticipated payments as a distinct, later hardening pass (MFA, PCI-adjacent
   review) — bundling a real gateway into this sprint would undercut that.
5. **Simple order status set**: `PLACED`, `COMPLETED`, `CANCELLED` — not a granular shipping/fulfillment
   state machine (`CONFIRMED`/`SHIPPED`/`OUT_FOR_DELIVERY`/etc.). Granular fulfillment tracking is exactly
   what the *later* DHL export integration sprint would need to drive; building it now would be speculative.
   Artisans mark an order `COMPLETED` (fulfilled it themselves, e.g. handed off in person/local shipping);
   buyers can `CANCEL` only while still `PLACED`.

## New PII/Ownership Consideration
An order links a buyer, one or more artisans (via their products), and (once addresses exist) a shipping
address. No shipping address field exists yet anywhere in the schema — Sprint 1/2 never captured one. This
sprint needs a minimal one (free-text, on the order itself, not a reusable address book — YAGNI) purely so
an artisan fulfilling an order knows where to send it. `docs/security-sana3-ma.md` should be updated to
reflect this as a new PII field once built (document-first, in the batch that adds it).

## Epic 5: Cart & Checkout (Buyer-Side)
Buyers can add products to a cart and place an order.

### Story 5.1: Cart (client-side)
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As a buyer, I want to add products to a cart while browsing, so that I can order several items at once.

**Acceptance Criteria**:
```gherkin
Given a visitor browsing /browse or a product detail page
When they add a product to their cart (with a quantity)
Then the cart updates and persists across a page reload
And the cart is visible/editable (change quantity, remove an item) from a dedicated view
```
**Technical Notes**: Pure frontend — new `cart` NgRx feature slice, localStorage-backed (see Assumed
Default #1). No backend endpoint. Cart entries reference `productId` + a snapshot of what was shown at
add-to-cart time (name/price/image) so the cart still renders sensibly if a product changes before checkout.

**Dependencies**: Sprint 2 Story 4.1 (browsing), 4.2 (product detail)

---

### Story 5.2: Checkout — place an order
**Priority**: Must | **Size**: L | **Specialist**: Backend Dev

As a buyer, I want to submit my cart as an order, so that artisans know what to prepare/ship.

**Acceptance Criteria**:
```gherkin
Given a logged-in user with items in their cart
When they submit checkout with a shipping address
Then an order is created with status PLACED, one order_item per cart line (snapshotting product details),
  and a total computed server-side (never trusting a client-supplied total)
And each product referenced must still exist and be valid at checkout time, or that line is rejected
  with a clear error (product may have been deleted/changed since it was added to the cart)
```
**Technical Notes**: `POST /api/v1/orders`, body = `{ shippingAddress, items: [{productId, quantity}] }`.
New `order` bounded context mirroring `catalog`'s hexagonal layering. Total price is computed from the
*current* product price at checkout time (not whatever the client's cart thinks it is) — this is the one
place server-side trust actually matters here, everything else is a snapshot for display/history purposes.

**Dependencies**: Story 5.1, Sprint 2 Story 3.1 (products must exist to order)

---

### Story 5.3: Angular checkout UI
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As a buyer, I want a checkout screen, so that I can review my order before placing it.

**Acceptance Criteria**:
```gherkin
Given items in the cart
When the buyer opens /checkout
Then they see a review of items + total, a shipping address field, and a "Place order" action
And a successful order clears the cart and shows a confirmation with the order id
And a per-line rejection (Story 5.2) is shown clearly, not just a generic failure
```
**Technical Notes**: New `order` NgRx feature slice (place order, load my orders). Route `/checkout`,
requires authentication (any role — Assumed Default #3) but not the artisan guard.

**Dependencies**: Story 5.1, 5.2

---

## Epic 6: Order Visibility & Fulfillment
Buyers see their order history; artisans see and act on orders containing their products.

### Story 6.1: Buyer order history
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As a buyer, I want to see my past orders, so that I can track what I've bought.

**Acceptance Criteria**:
```gherkin
Given a logged-in buyer with past orders
When they request their order list
Then they see each order's status, items, total, and placed-at date
And they can view one order's full detail
```
**Technical Notes**: `GET /api/v1/orders/me`, `GET /api/v1/orders/me/{id}`. CQRS-lite query handler, same
pattern as Sprint 1/2's `Get*` handlers.

**Dependencies**: Story 5.2

---

### Story 6.2: Buyer cancels a pending order
**Priority**: Should | **Size**: S | **Specialist**: Backend Dev

As a buyer, I want to cancel an order I no longer want, so that I'm not stuck with an unwanted purchase.

**Acceptance Criteria**:
```gherkin
Given a logged-in buyer with an order they own, still PLACED
When they cancel it
Then its status becomes CANCELLED
And attempting to cancel an order that's already COMPLETED or CANCELLED is rejected
```
**Technical Notes**: `POST /api/v1/orders/me/{id}/cancel`. Ownership check (order.buyerId == caller),
status-transition guard in the domain entity itself (illegal transitions throw, not just a controller-level
`if`).

**Dependencies**: Story 6.1

---

### Story 6.3: Artisan views and fulfills orders for their products
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As an artisan, I want to see orders containing my products, so that I know what to prepare and ship.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan with products that have been ordered
When they request their incoming orders
Then they see each relevant order_item (not necessarily the whole order, if it spans multiple artisans'
  products) with buyer contact/shipping info and quantity/product ordered
And they can mark their portion COMPLETED
```
**Technical Notes**: `GET /api/v1/artisan-profiles/me/orders`, `POST /api/v1/artisan-profiles/me/orders/{id}/complete`.
An order spanning multiple artisans' products (a buyer orders from 2 different artisans in one checkout) is
allowed per Story 5.2 — each artisan only sees/acts on their own `order_items` within that order, not the
whole order or the other artisan's lines. Reuses `ArtisanProfileRepository` ownership pattern from Sprint 2.

**Dependencies**: Story 5.2, Sprint 1 Story 2.1 (must have an artisan profile)

---

### Story 6.4: Angular order history UI (buyer)
**Priority**: Must | **Size**: S | **Specialist**: Frontend Dev

As a buyer, I want an order history screen, so that I can track and manage my orders.

**Acceptance Criteria**:
```gherkin
Given the /orders route
When a logged-in buyer views it
Then they see their orders (status, items, total) with a cancel action on PLACED orders
```
**Technical Notes**: Reuses the `order` NgRx slice from Story 5.3.

**Dependencies**: Story 6.1, 6.2, 5.3

---

### Story 6.5: Angular seller orders UI (artisan)
**Priority**: Must | **Size**: S | **Specialist**: Frontend Dev

As an artisan, I want an incoming-orders screen, so that I can see and fulfill what's been ordered.

**Acceptance Criteria**:
```gherkin
Given the /profile/orders route (ARTISAN only)
When a logged-in artisan views it
Then they see order_items for their products with a "Mark completed" action
```
**Technical Notes**: New route alongside `/profile/products`, same `artisanGuard`.

**Dependencies**: Story 6.3

---

## Open Questions (not blocking Sprint 3 start, flagged for later)
- **Real payment gateway** (CMI/Stripe): explicitly deferred per Assumed Default #4. Revisit as its own
  sprint once the order lifecycle above is proven — the API surface (`POST /api/v1/orders`) is designed so
  a payment step can be inserted between "order created" and "order confirmed" later without reshaping what
  Sprint 3 builds.
- **Reusable address book**: this sprint's shipping address is a one-off free-text field per order, not a
  saved/reusable address. Revisit if repeat buyers find re-entering it annoying enough to matter.
- **Multi-currency**: products already carry a currency per Sprint 2 (`priceCurrency`, defaulting `MAD`),
  and an order could in principle span products in different currencies from different artisans. This
  sprint does **not** attempt currency conversion or a single order-level total currency — the order total
  is computed per-currency (an order could show "450 MAD + $20 USD" as two totals) rather than pretending a
  single blended total is meaningful. Flagging this now so it isn't accidentally "fixed" by silently
  assuming everything is MAD.

## Sprint Allocation
| Sprint | Stories | Estimated Effort |
|---|---|---|
| Sprint 3 | 5.1, 5.2, 5.3, 6.1, 6.2, 6.3, 6.4, 6.5 | ~8-10 days |
