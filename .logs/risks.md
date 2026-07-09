# RISKS — Sana3.ma

## RISK 2026-07-08 — NgRx installed against Angular 22 via --legacy-peer-deps
NgRx 21.1.1 (latest as of this session) peer-depends on Angular ^21; frontend scaffold uses Angular 22.0.5
(latest via `ng new`, no 21-line pin requested). Installed with --legacy-peer-deps per user decision (Batch 4).
Untested upstream combination — watch for subtle runtime/build issues in store/effects/devtools during Batch 5/6
(auth/profile NgRx slices). If issues surface, fallback: downgrade to Angular 21.x or wait for NgRx's Angular-22
release and re-pin. Added frontend/.npmrc (legacy-peer-deps=true) so the conflict doesn't also break `ng add`
schematics or CI's `npm ci` (Batch 9) — remove once NgRx ships Angular 22 support.
UPDATE 2026-07-09 (Batch 5): real auth NgRx slice (actions/reducer/effects/selectors) implemented and
exercised end-to-end (unit tests + live browser smoke test against a real backend) with no runtime issues.
Still worth re-checking during Batch 6 (profile slice) and Batch 9 (CI `npm ci`), but no longer a live
blocker — downgrading this from an active concern to routine monitoring.

## RISK 2026-07-09 — Stateless JWT has no server-side session revocation beyond cookie expiry (accepted)
Batch 5 added `POST /api/v1/auth/logout` to expire the httpOnly refresh cookie, closing the gap where
"logout" only cleared client state. But per the stateless JWT design (ADR-3), there is no server-side
token/session store — a refresh token already extracted from the cookie by other means (e.g. XSS, a
compromised proxy) remains valid until its natural 7-day TTL even after "logout". This is an accepted
limitation of the architecture, not a regression, but worth remembering if a future security review or
compliance requirement calls for real revocation (would need a server-side denylist or a move to opaque
session tokens — out of scope for Sprint 1).

