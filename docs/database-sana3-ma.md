# Database Design: Sana3.ma
**Architecture Reference**: docs/architecture-sana3-ma.md
**Version**: 1.0 | **Date**: 2026-07-04 | **Author**: DBA

## 1. Database Selection
- **Engine**: PostgreSQL 16 + PostGIS extension
- **Rationale**: YAGNI default is PostgreSQL; PostGIS enabled now (near-zero cost) since artisan region/origin geodata is a near-term roadmap feature (avoids a disruptive extension-add migration later)
- **Hosting**: Dockerized Postgres container (sprint 1); managed Postgres (e.g. RDS/Cloud SQL) to be revisited at DevOps SDR when moving to real staging/prod traffic

## 2. Entity-Relationship Model
```
users ──1:1──> artisan_profiles   (only when users.role = 'ARTISAN')
users ──N:1──> roles              (via users.role, enum-backed)
```

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
```

## 4. Index Strategy
| Table | Index Name | Columns | Query Pattern |
|---|---|---|---|
| users | idx_users_email | (email) | login lookup by email (also enforced by UNIQUE) |
| artisan_profiles | idx_artisan_profiles_user_id | (user_id) | fetch own profile by authenticated user (also enforced by UNIQUE) |

## 5. Migration Plan
| Migration File | Description | Reversible |
|---|---|---|
| V1__init_users_and_roles.sql | Create `users` table + role check constraint | Yes (DROP TABLE) |
| V2__create_artisan_profiles.sql | Create `artisan_profiles` table + PostGIS extension enable | Yes (DROP TABLE / DROP EXTENSION) |

## 6. Access Patterns
| Use Case | Query Pattern | Index Coverage |
|---|---|---|
| Login | SELECT by email | idx_users_email |
| Get own profile | SELECT by user_id | idx_artisan_profiles_user_id |
| Update own profile | UPDATE by user_id | idx_artisan_profiles_user_id |

## 7. Sensitive Data
- Columns requiring protection: `password_hash` (bcrypt, never returned in API responses), `contact_phone` (PII, excluded from any public-facing endpoint until a later "public artisan directory" feature explicitly requires it)
- Row-level security: not needed yet (single-tenant access pattern — users only ever query their own row via `user_id` from JWT claim)
