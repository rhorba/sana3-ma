# UX Foundation: Sana3.ma
**PRD Reference**: docs/prd-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: UX Designer

## 1. User Personas (minimal — sprint 1)
| Persona | Role | Goal | Pain Point |
|---|---|---|---|
| Yassine, artisan | ARTISAN | Create a trustworthy profile buyers can find | No existing digital presence beyond word-of-mouth |
| Amina, buyer | BUYER | Have an account ready for future browsing/ordering | Needs simple, fast signup |

## 2. Information Architecture / Site Map
```
[App Root]
├── /register
├── /login
├── /browse                   (public — no login required, Sprint 2 Batch 16)
├── /products/:id             (public — product detail, Sprint 2 Batch 16)
├── /cart                     (public — no login required, client-side only, Sprint 3 Batch 24)
└── /profile                  (ARTISAN only — view/edit combined on one screen, Story 2.3)
    └── /profile/products     (ARTISAN only — "My Products" list + add/edit, Sprint 2 Batch 15)
```
(Sprint 1's original sketch had a separate `/profile/edit` sub-route; Story 2.3 combined view+edit into
`/profile` itself, so that entry never existed as built — corrected here rather than left stale.)

## 3. Core User Flows (top 2 journeys, sprint 1)
### Flow 1: Registration → Login
```
[Landing] → [Register form] → [Role select: Buyer/Artisan] → [Submit] → [Auto-login] → [Redirect: Buyer→Home stub / Artisan→Profile edit]
                    ↓ Invalid input
              [Inline field errors] → [Retry]
```

### Flow 2: Artisan Profile Edit
```
[Login] → [Profile page] → [Edit] → [Fill craft type/region/bio/contact] → [Save] → [Success toast] → [Profile view updated]
                                          ↓ Validation error
                                    [Inline errors] → [Retry]
```

## 4. Key Screen Wireframes (text-based)
### Screen: Login
```
┌─────────────────────────────┐
│ Sana3.ma            [logo]  │
├─────────────────────────────┤
│  Email    [____________]    │
│  Password [____________]    │
│         [ Log in ]          │
│  No account? Register →     │
└─────────────────────────────┘
```

### Screen: Artisan Profile Edit
```
┌─────────────────────────────┐
│ ← My Profile                │
├─────────────────────────────┤
│  Display name [__________]  │
│  Craft type   [__________]  │
│  Region       [__________]  │
│  Bio          [__________]  │
│  Phone        [__________]  │
│         [ Save ]             │
└─────────────────────────────┘
```

## 5. Screen States
| Screen | Empty State | Loading | Error | Success |
|---|---|---|---|---|
| Login | n/a | Spinner on submit | "Invalid email or password" inline | Redirect + welcome toast |
| Register | n/a | Spinner on submit | Field-level validation errors | Auto-login + redirect |
| Profile Edit | "Complete your profile" prompt if empty | Skeleton form on load | "Couldn't save, try again" toast | "Profile updated" toast |

### UX Validation Checklist
- [x] Personas match PRD target users (Artisan, Buyer — no unnecessary depth)
- [x] All PRD user stories map to a flow (registration, login, profile edit)
- [x] Wireframes cover happy path + at least one error state
- [x] Navigation hierarchy is clear and shallow (3 routes)
