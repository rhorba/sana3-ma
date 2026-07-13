CREATE TABLE cooperative_invites (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  artisan_profile_id  UUID NOT NULL REFERENCES artisan_profiles(id) ON DELETE CASCADE,
  invited_user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  status              VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','ACCEPTED','DECLINED')),
  created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  resolved_at         TIMESTAMPTZ
);

CREATE INDEX idx_cooperative_invites_invited_user_id ON cooperative_invites (invited_user_id);
