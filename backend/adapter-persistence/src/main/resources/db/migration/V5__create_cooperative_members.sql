CREATE TABLE cooperative_members (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id             UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  artisan_profile_id  UUID NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
  role                VARCHAR(20) NOT NULL CHECK (role IN ('OWNER','MEMBER')),
  joined_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cooperative_members_artisan_profile_id ON cooperative_members (artisan_profile_id);

-- Backfill: every existing artisan_profiles row's owning user becomes that profile's OWNER member.
-- This is what lets Batch 31 move authorization off artisan_profiles.user_id entirely.
INSERT INTO cooperative_members (user_id, artisan_profile_id, role, joined_at)
SELECT user_id, id, 'OWNER', created_at
FROM artisan_profiles;

-- user_id lookup is covered by the UNIQUE constraint's implicit index; no extra index needed
