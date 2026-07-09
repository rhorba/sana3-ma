export interface ArtisanProfileState {
  id: string | null;
  userId: string | null;
  displayName: string | null;
  craftType: string | null;
  region: string | null;
  bio: string | null;
  contactPhone: string | null;
  loading: boolean;
  loaded: boolean;
  saving: boolean;
  error: string | null;
}

export const initialArtisanProfileState: ArtisanProfileState = {
  id: null,
  userId: null,
  displayName: null,
  craftType: null,
  region: null,
  bio: null,
  contactPhone: null,
  loading: false,
  loaded: false,
  saving: false,
  error: null,
};
