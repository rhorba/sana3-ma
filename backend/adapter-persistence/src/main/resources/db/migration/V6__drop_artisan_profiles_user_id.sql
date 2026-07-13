-- cooperative_members (V5) is now the sole source of truth for who can act on a profile.
ALTER TABLE artisan_profiles DROP COLUMN user_id;
