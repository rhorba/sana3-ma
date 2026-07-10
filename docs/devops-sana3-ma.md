# DevOps Foundation: Sana3.ma
**Architecture**: docs/architecture-sana3-ma.md
**Security**: docs/security-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: DevOps/DevSecOps

## 1. Environment Strategy
| Environment | Purpose | Deploy Trigger |
|---|---|---|
| local | Development | `docker compose up` |
| staging | QA / Preview | PR merge to `main` (future: GitHub Actions job) |
| production | Live users | Manual tag / approved release (not needed sprint 1) |

## 2. CI Pipeline (GitHub Actions)
```yaml
stages:
  - lint            # checkstyle/spotless (backend), eslint (frontend)
  - test            # JUnit+Testcontainers (backend), Vitest (frontend); fail CI if coverage < 80%
  - security-scan   # SAST (Semgrep), SCA (Trivy), secrets (Gitleaks)
  - build           # Docker images: backend, frontend
  - deploy-staging  # auto on PR merge to main
```
CI must stay green: if a push turns CI red, stop other work, diagnose, fix, push again, repeat until green (per project rules).

## 3. Infrastructure
- **Hosting**: Docker Compose, single host (sprint 1); no Kubernetes unless SDR-1 trigger in system-design doc fires
- **Compute**: containers (backend, frontend/nginx, postgres)
- **Database**: containerized Postgres 16 + PostGIS, volume-mounted for persistence
- **Secrets**: `.env` file (git-ignored) with dev-safe defaults from `.env.example`; CI secrets via GitHub Actions repository secrets
- **Monitoring**: Spring Boot Actuator `/actuator/health` + `/actuator/metrics`; container logs via `docker compose logs`

## 4. Security Scanning Gates
| Scanner | Scan Type | Fail Threshold |
|---|---|---|
| Semgrep | SAST — code vulnerabilities | Critical findings |
| Trivy | SCA — dependency CVEs (Maven + npm) | Critical CVEs |
| Gitleaks | Secrets detection | Any secrets found |

## 5. Docker Setup
```dockerfile
# backend/Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
```dockerfile
# frontend/Dockerfile
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json .npmrc ./
RUN npm ci
COPY . .
RUN npm run build
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```
Note: Angular project is named `frontend` (not `sana3-ma-frontend`), and the new Angular application builder
nests static output under `dist/frontend/browser/`. `.npmrc` (legacy-peer-deps=true) must be copied into the
build stage — see risks.md for why. `nginx.conf` adds a `try_files $uri $uri/ /index.html;` SPA fallback —
without it, direct navigation or a hard reload on any client-side route (e.g. `/register`) 404s at the nginx
level, since those paths don't exist as files.
`docker-compose.yml` wires: `postgres` (postgis/postgis image), `backend`, `frontend`, all reading from `.env`.
`API_BASE_URL` is baked into the static build via a Docker build ARG (see frontend/Dockerfile) — the browser
calls the backend directly at `http://localhost:${BACKEND_HOST_PORT}`, not through an nginx proxy.

## 6. Monitoring Baseline
| Signal | Tool | Alert Threshold |
|---|---|---|
| Logs | `docker compose logs` (sprint 1); centralized logging deferred | Error rate spike (manual watch, sprint 1) |
| Metrics | Spring Actuator | Latency p99 > 300ms (manual check, sprint 1) |
| Uptime | Manual / health endpoint | n/a — no SLO yet |

### DevOps Validation Checklist
- [x] All 3 environments defined with deploy triggers
- [x] CI pipeline covers lint + test (with coverage gate) + security scan + build + deploy
- [x] Coverage gate configured (< 80% fails CI)
- [x] Secrets management strategy confirmed (`.env.example` defaults, no hardcoded secrets)
- [x] Monitoring baseline defined (manual thresholds acceptable at sprint-1 scale)
