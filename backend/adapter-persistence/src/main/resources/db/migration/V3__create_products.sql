CREATE TABLE products (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  artisan_profile_id UUID NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
  name              VARCHAR(150) NOT NULL,
  description       TEXT,
  price_amount      NUMERIC(10,2) NOT NULL CHECK (price_amount > 0),
  price_currency    VARCHAR(3) NOT NULL DEFAULT 'MAD',
  craft_type        VARCHAR(100) NOT NULL,
  image_url         VARCHAR(500),
  created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Lists an artisan's own products (Story 3.3) and enforces ownership joins on write endpoints
CREATE INDEX idx_products_artisan_profile_id ON products (artisan_profile_id);
