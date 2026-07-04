# System Design: Sana3.ma
**PRD Reference**: docs/prd-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: System Designer

## 1. Non-Functional Requirements
| Attribute | Target | Notes |
|---|---|---|
| Availability | 99% (sprint 1) | Single instance + restart; revisit at scale |
| Latency (p99) | < 300ms | Auth + profile endpoints |
| Throughput | ~50 RPS peak | Sprint-1 test load only |
| Data Volume | < 1 GB | Users + profiles, negligible |
| Retention | Indefinite (user accounts) | No auto-purge needed yet |
| Recovery (RTO) | < 60 min | Manual restore from DB backup |
| Recovery (RPO) | < 24 hr | Daily Postgres dump |

## 2. Component Topology
```
[Browser: Angular SPA]
        ↓ HTTPS (local: HTTP)
[Spring Boot REST API]  ←── JWT auth filter, validation
        ↓
[Application Layer: Use Cases / CQRS Command+Query handlers]
        ↓
[Domain Layer: User, ArtisanProfile — framework-free]
        ↓
[Infrastructure Adapters: JPA Repositories]
        ↓
[PostgreSQL 16 + PostGIS]
```
No CDN, load balancer, or API gateway yet — single Docker Compose host serves sprint 1. No message queue — all flows are synchronous request/response.

## 3. Integration Patterns
| Integration | Pattern | Reason |
|---|---|---|
| Angular ↔ Spring Boot | REST/HTTP + JSON | Simple synchronous CRUD, no need for GraphQL/gRPC yet |
| Backend ↔ Postgres | JPA/Hibernate via repository ports | Standard for hexagonal adapter |

## 4. Scalability Strategy
- Scaling approach: vertical (single container) for sprint 1
- Cache strategy: none — no repeated-read bottleneck yet
- Queue strategy: none — introduce only if async flows (e.g., cert generation, DHL webhooks) arrive in later sprints

## 5. System Design Decision Records

### SDR-1: Deploy target
- **NFR Driver**: Availability/cost — sprint 1 has no real traffic
- **Decision**: Docker Compose (Postgres + Spring Boot + Angular/nginx) on a single host
- **Alternatives**: Kubernetes — rejected for now (no multi-node/scaling need)
- **Re-evaluate when**: concurrent users > ~500, or multi-service orchestration/self-healing becomes a real requirement

### SDR-2: CQRS-lite adoption
- **NFR Driver**: none directly — this is an architectural choice from Software Architect or user preference, not a scale-driven NFR
- **Decision**: Separate command (write) and query (read) handlers within the application layer, still single Postgres schema (no separate read DB)
- **Alternatives**: plain CRUD service — simpler, rejected per user's explicit choice of comprehensive architecture
- **Re-evaluate when**: read/write patterns diverge enough to justify a separate read model, or this proves to be unnecessary overhead at sprint retro
