# Stories: Sana3.ma — Sprint 4 (Cooperative Multi-User Accounts)
**PRD**: docs/prd-sana3-ma.md (§4 "Out of Scope (future sprints)" — "Cooperative multi-user accounts")
**Architecture**: docs/architecture-sana3-ma.md (hexagonal layering — this lives inside the existing
`artisanprofile` bounded context, not a new module, since it changes that context's ownership model rather
than adding a new domain)
**Continues epic numbering from**: docs/stories-sana3-ma-sprint3.md (Epic 5-6)

## Scope Boundary
In scope: multiple users jointly managing one artisan profile ("cooperative account") — inviting an
existing artisan user to join, listing/removing members, and reworking every existing ownership check so
it works for a shared profile instead of assuming exactly one owning user.
Out of scope, deferred to a later sprint: ownership transfer (an OWNER stepping down / handing off), a
signup-via-invite flow for people who don't have an account yet, granular per-member permissions beyond a
simple OWNER/MEMBER split, and a "belong to multiple cooperatives" model for one user (see Assumed Default
#2). None of these are needed for the core "several artisans run one shop together" use case the PRD
bullet describes.

## Why now, over the other options
The user chose this over the simpler-first recommendation (real payment gateway). It's a materially bigger
change than Sprint 3's work: it touches the identity/authorization model directly, not just a new bounded
context layered on top. Confirmed via research before writing this doc: `artisan_profiles.user_id` is a
hard `UNIQUE` schema constraint today (docs/database-sana3-ma.md), and the exact same ownership check —
"look up the artisan profile by the authenticated user's id, then compare its id to the resource's
`artisan_profile_id`" — is duplicated across 9 handlers (product CRUD/image upload/listing, profile
get/update, order-item complete/list). All 9 are the seam this sprint reworks; nothing else in the codebase
assumes 1:1 ownership (JWT claims are just `sub`/`email`/`role` — no profile id baked in — and the frontend
`artisanGuard` is a pure role check with no profile-specific logic).

## Assumed Defaults (stated, not yet confirmed — flag any you want changed)
1. **A new `cooperative_members` join table replaces the 1:1 constraint**, rather than a separate
   `Cooperative` entity distinct from `ArtisanProfile`. The artisan profile itself *is* the cooperative
   account — introducing a second, parallel concept for the same thing would be a redundant abstraction the
   PRD's use case doesn't call for.
2. **A user can belong to at most one cooperative at a time** (`UNIQUE(user_id)` on the membership table) —
   mirrors today's 1:1 shape, just moved so many users can point at one profile instead of the reverse.
   Avoids building a "switch active cooperative" UI for a need nobody has stated. An artisan who wants to
   leave and join a different cooperative removes themselves first (Story 7.3).
3. **Two roles: OWNER and MEMBER.** OWNER = whoever creates the profile (auto-assigned on first profile
   creation, unchanged UX from Sprint 1). Only an OWNER can invite/remove members. Both OWNER and MEMBER can
   manage products and fulfill orders on the shared profile, and both can edit shared profile fields
   (display name/bio/craft type/region) — collaborative by design, since gatekeeping every field edit behind
   OWNER-only isn't what "run a shop together" implies. No finer-grained permission matrix.
4. **Invites target an existing ARTISAN-role account by email, and require the invitee's acceptance** —
   no signup-via-invite (that's its own flow: token-based registration, email delivery infra this project
   doesn't have yet — YAGNI). If the email doesn't match an existing ARTISAN user, the invite is rejected
   with a clear error at creation time, not silently queued.
   Acceptance is a real gate, not a formality: an OWNER should not be able to unilaterally add someone to
   their cooperative without that person's consent — silently-forced membership would hand the OWNER
   visibility into another user's session (they'd suddenly gain the ability to act as if on behalf of a
   profile they never agreed to join). Pending invites surface to the invitee on login (Story 7.5); no email
   is actually sent (no SMTP/notification infra exists yet, and none is being added this sprint) — this
   sprint's invite is in-app only, by design, same "no new infra unless needed" call as every prior sprint.
5. **An OWNER cannot leave while other members exist**, and there's no ownership-transfer story this sprint
   (see Scope Boundary). If an OWNER is the profile's only member, "leaving" is equivalent to abandoning the
   cooperative entirely — out of scope; deletion of a profile with members isn't a new capability this
   sprint adds.

## New PII/Ownership Consideration
The members list (Story 7.3) exposes each member's email to every other member of the same cooperative —
new cross-user visibility that didn't exist before (today, no user can see another user's email unless
they're fulfilling that user's order, per Sprint 3's documented buyer-PII exposure). This is intentional and
proportionate to "people who jointly run a shop know who else runs it," but should be called out explicitly
in docs/security-sana3-ma.md when built (document-first), same treatment Sprint 3 gave buyer PII.
Invite creation is itself a minor enumeration risk (does this email belong to an ARTISAN account?) — the
handler should return the same class of error for "no such user" and "user isn't an ARTISAN" rather than
distinguishing them, to avoid leaking account existence/role to an inviter probing random emails.

## Epic 7: Cooperative Membership

### Story 7.1: Membership data model + migration
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev / DBA

As the system, I need a membership model, so that multiple users can be associated with one artisan
profile instead of exactly one.

**Acceptance Criteria**:
```gherkin
Given the existing artisan_profiles table with a UNIQUE user_id column
When the migration runs
Then a new cooperative_members table exists (user_id UNIQUE, artisan_profile_id, role, joined_at)
  backfilled from every existing artisan_profiles row (existing owner becomes role=OWNER)
And artisan_profiles.user_id and its UNIQUE constraint are dropped, since membership is now the only
  source of truth for who can act on a profile
```
**Technical Notes**: Flyway migration in one file (backfill INSERT ... SELECT before the DROP COLUMN, same
transaction). `CompositeMembershipRepository`-style port mirroring existing repository conventions.

**Dependencies**: none (foundational for this epic)

---

### Story 7.2: Authorization rework across existing handlers
**Priority**: Must | **Size**: L | **Specialist**: Backend Dev

As an artisan (owner or member), I want every existing artisan-only action to work the same way it did
before, so that the cooperative model is a pure extension, not a regression.

**Acceptance Criteria**:
```gherkin
Given a cooperative with an OWNER and one MEMBER
When either user calls any existing artisan-only endpoint (profile get/update, product CRUD/image upload/
  listing, order-item complete/list)
Then the action succeeds exactly as it would have for a single owner today
And a user with no membership row gets the same 403/404 behavior as today's "no profile" case
```
**Technical Notes**: Swap all 9 handlers' `artisanProfileRepository.findByUserId(...)` call sites for a
`membershipRepository.findByUserId(...)` lookup that resolves to the same `artisan_profile_id`, then reuse
each handler's existing downstream ownership comparison unchanged. This is a refactor of an existing seam,
not new business logic — the risk is missing one of the 9 call sites, so exhaustive test coverage per
handler matters more than for a typical new-feature batch.

**Dependencies**: Story 7.1

---

### Story 7.3: Invite, list, and remove members
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As a cooperative OWNER, I want to invite another artisan and manage who's in my cooperative, so that we can
run the shop together.

**Acceptance Criteria**:
```gherkin
Given a logged-in OWNER
When they invite an existing ARTISAN-role user by email
Then a PENDING invite is created for that user (or a clear rejection if no matching ARTISAN account exists)
And the invited user sees the pending invite next time they view it, and can accept or decline
And accepting creates their membership row (role=MEMBER) and consumes the invite
And a MEMBER can voluntarily remove themselves; an OWNER can remove any MEMBER (not another OWNER)
```
**Technical Notes**: New `cooperative_invites` table (email/user id, artisan_profile_id, status
PENDING/ACCEPTED/DECLINED). No email sending (Assumed Default #4) — invites are visible only via an
authenticated `GET /api/v1/cooperative-invites/me` the invitee polls/sees on login.

**Dependencies**: Story 7.1

---

### Story 7.4: Angular members management UI
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As a cooperative OWNER or MEMBER, I want a members screen, so that I can see who's in the cooperative and
manage invites.

**Acceptance Criteria**:
```gherkin
Given the /profile/members route (ARTISAN only, existing artisanGuard)
When a logged-in member views it
Then they see the member list (email, role) and, if OWNER, an invite form and remove actions
And any authenticated ARTISAN user with a pending invite sees it (accept/decline) somewhere they'll
  actually notice it — e.g. a banner on login, not just buried in a settings page
```
**Technical Notes**: New `cooperative` NgRx slice (membership list, pending invite state), built only for
what this story needs — same incremental-slice pattern every prior sprint used.

**Dependencies**: Story 7.3

---

## Open Questions (not blocking Sprint 4 start, flagged for later)
- **Ownership transfer**: deferred per Assumed Default #5. Revisit if an OWNER genuinely needs to step back
  from a cooperative that has other members — this sprint has no answer for that case beyond "can't happen
  yet."
- **Multi-cooperative membership**: deferred per Assumed Default #2. Revisit only if a real user needs to
  split time across two shops — no evidence that's a real need yet.
- **Signup-via-invite**: deferred per Assumed Default #4. Would need email delivery infra this project
  doesn't have; revisit alongside any future notification-system sprint.

## Sprint Allocation
| Sprint | Stories | Estimated Effort |
|---|---|---|
| Sprint 4 | 7.1, 7.2, 7.3, 7.4 | ~6-8 days |
