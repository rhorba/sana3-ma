# ISSUES — Sana3.ma



## ISSUE FOUND+FIXED 2026-07-06 — Batch 2 VERIFY
Manual docker-compose smoke test of the real running backend (not just unit tests) caught a bug unit tests missed:
malformed request bodies (e.g. role=ADMIN, an enum value rejected at deserialization) triggered Tomcat's internal
ERROR dispatch to /error, which re-entered the Spring Security filter chain. Since /error wasn't in the permit-list,
it was rejected 401 (unauthenticated), clobbering the intended 400 Bad Request — client saw 401 with an empty body
instead of a useful 400 validation error. Root cause: @WebMvcTest slice test used addFilters=false, so this filter-chain
interaction was invisible to unit tests. Fix: added .requestMatchers("/error").permitAll() to SecurityConfig
(backend/adapter-web/src/main/java/ma/sana3/adapter/web/security/SecurityConfig.java). Reverified via curl: ADMIN role
and short-password now correctly return 400. Re-ran full test suite after the fix — still green (36/36).
