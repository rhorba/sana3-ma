CREATE TABLE craft_certificates (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id          UUID NOT NULL UNIQUE REFERENCES products(id) ON DELETE CASCADE,
  artisan_profile_id  UUID NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
  issued_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- product_id lookup (issue-or-fetch) is covered by its UNIQUE constraint's implicit index.
-- id lookup (public verification) is covered by the primary key.
