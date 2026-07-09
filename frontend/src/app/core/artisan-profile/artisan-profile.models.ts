export interface UpsertArtisanProfileRequest {
  displayName: string;
  craftType: string;
  region: string;
  bio: string;
  contactPhone: string;
}

export interface ArtisanProfileResponse {
  id: string;
  userId: string;
  displayName: string;
  craftType: string;
  region: string | null;
  bio: string | null;
  contactPhone: string | null;
  createdAt: string;
  updatedAt: string;
}
