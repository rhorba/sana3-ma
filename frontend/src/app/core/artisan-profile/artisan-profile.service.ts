import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { ArtisanProfileResponse, UpsertArtisanProfileRequest } from './artisan-profile.models';

@Injectable({ providedIn: 'root' })
export class ArtisanProfileService {
  private readonly http = inject(HttpClient);

  getMyProfile(): Observable<ArtisanProfileResponse> {
    return this.http.get<ArtisanProfileResponse>(`${API_BASE_URL}/artisan-profiles/me`);
  }

  upsertMyProfile(request: UpsertArtisanProfileRequest): Observable<ArtisanProfileResponse> {
    return this.http.put<ArtisanProfileResponse>(`${API_BASE_URL}/artisan-profiles/me`, request);
  }
}
