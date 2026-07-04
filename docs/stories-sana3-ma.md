# Stories: Sana3.ma — Sprint 1
**PRD**: docs/prd-sana3-ma.md
**Architecture**: docs/architecture-sana3-ma.md

## Epic 1: Authentication
Users can register, log in, and receive a JWT session so they can access role-specific features.

### Story 1.1: User registration
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As a visitor, I want to register with email, password, and role, so that I have an account.

**Acceptance Criteria**:
```gherkin
Given a visitor on the registration page
When they submit a valid email, password (≥10 chars), and role BUYER or ARTISAN
Then a user record is created with a bcrypt-hashed password
And they are auto-logged in with a JWT
```
**Technical Notes**: Uses `POST /api/v1/auth/register` (architecture doc §5). Touches `users` table. Security: role field server-validated, never trusts client for ADMIN.

**Dependencies**: none

---

### Story 1.2: User login
**Priority**: Must | **Size**: S | **Specialist**: Backend Dev

As a registered user, I want to log in, so that I can access my account.

**Acceptance Criteria**:
```gherkin
Given a registered user
When they submit correct email and password
Then they receive a JWT access token and a refresh token cookie
```
**Technical Notes**: Uses `POST /api/v1/auth/login`. Security: generic error on wrong credentials (no user enumeration).

**Dependencies**: Story 1.1

---

### Story 1.3: Angular auth UI (register + login)
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As a user, I want registration and login forms, so that I can create and access my account.

**Acceptance Criteria**:
```gherkin
Given the login or register page
When I submit invalid input
Then I see inline field validation errors
```
**Technical Notes**: NgRx `auth` feature slice (per ADR-2), Angular Material form fields (per UI doc). Calls `/auth/register` and `/auth/login`.

**Dependencies**: Story 1.1, 1.2

---

## Epic 2: Artisan Profile
Artisan users can create and view their profile.

### Story 2.1: Create/update artisan profile
**Priority**: Must | **Size**: M | **Specialist**: Backend Dev

As an artisan, I want to create/edit my profile, so that buyers can eventually find me.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan
When they submit display name, craft type, region, bio, phone
Then their artisan_profiles row is created or updated
```
**Technical Notes**: Uses `PUT /api/v1/artisan-profiles/me`. Ownership enforced via JWT subject == user_id.

**Dependencies**: Story 1.1, 1.2

---

### Story 2.2: View own artisan profile
**Priority**: Must | **Size**: S | **Specialist**: Backend Dev

As an artisan, I want to view my profile, so that I can confirm my information is correct.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan with an existing profile
When they request their profile
Then they see their current profile data
```
**Technical Notes**: Uses `GET /api/v1/artisan-profiles/me` (CQRS query handler per ADR-1).

**Dependencies**: Story 2.1

---

### Story 2.3: Angular profile UI
**Priority**: Must | **Size**: M | **Specialist**: Frontend Dev

As an artisan, I want a profile view/edit screen, so that I can manage my information.

**Acceptance Criteria**:
```gherkin
Given a logged-in artisan
When they navigate to /profile
Then they see their profile (or an empty-state prompt) and can edit it
```
**Technical Notes**: NgRx `artisan-profile` feature slice, Angular Material components (per UI doc wireframes).

**Dependencies**: Story 2.1, 2.2

---

## Sprint Allocation
| Sprint | Stories | Estimated Effort |
|---|---|---|
| Sprint 1 | 1.1, 1.2, 1.3, 2.1, 2.2, 2.3 | ~5-6 days |
