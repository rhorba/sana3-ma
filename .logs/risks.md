# RISKS — Sana3.ma

## RISK 2026-07-08 — NgRx installed against Angular 22 via --legacy-peer-deps
NgRx 21.1.1 (latest as of this session) peer-depends on Angular ^21; frontend scaffold uses Angular 22.0.5
(latest via `ng new`, no 21-line pin requested). Installed with --legacy-peer-deps per user decision (Batch 4).
Untested upstream combination — watch for subtle runtime/build issues in store/effects/devtools during Batch 5/6
(auth/profile NgRx slices). If issues surface, fallback: downgrade to Angular 21.x or wait for NgRx's Angular-22
release and re-pin. Added frontend/.npmrc (legacy-peer-deps=true) so the conflict doesn't also break `ng add`
schematics or CI's `npm ci` (Batch 9) — remove once NgRx ships Angular 22 support.


