CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE artisan_profiles (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  display_name  VARCHAR(150) NOT NULL,
  craft_type    VARCHAR(100) NOT NULL,
  region        VARCHAR(100),
  bio           TEXT,
  contact_phone VARCHAR(30),
  location      GEOGRAPHY(POINT, 4326),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- user_id lookup is covered by the UNIQUE constraint's implicit index; no extra index needed
