import { createFeature, createReducer, on } from '@ngrx/store';

import { ArtisanProfileActions } from './artisan-profile.actions';
import { ArtisanProfileState, initialArtisanProfileState } from './artisan-profile.state';

export const artisanProfileFeature = createFeature({
  name: 'artisanProfile',
  reducer: createReducer(
    initialArtisanProfileState,
    on(ArtisanProfileActions.loadProfile, (state): ArtisanProfileState => ({
      ...state,
      loading: true,
      error: null,
    })),
    on(
      ArtisanProfileActions.loadProfileSuccess,
      ArtisanProfileActions.saveProfileSuccess,
      (state, { response }): ArtisanProfileState => ({
        ...state,
        id: response.id,
        displayName: response.displayName,
        craftType: response.craftType,
        region: response.region,
        bio: response.bio,
        contactPhone: response.contactPhone,
        loading: false,
        loaded: true,
        saving: false,
        error: null,
      }),
    ),
    on(ArtisanProfileActions.loadProfileNotFound, (state): ArtisanProfileState => ({
      ...state,
      loading: false,
      loaded: true,
      error: null,
    })),
    on(ArtisanProfileActions.loadProfileFailure, (state, { message }): ArtisanProfileState => ({
      ...state,
      loading: false,
      loaded: false,
      error: message,
    })),
    on(ArtisanProfileActions.saveProfile, (state): ArtisanProfileState => ({
      ...state,
      saving: true,
      error: null,
    })),
    on(ArtisanProfileActions.saveProfileFailure, (state, { message }): ArtisanProfileState => ({
      ...state,
      saving: false,
      error: message,
    })),
  ),
});

export const {
  name: artisanProfileFeatureKey,
  reducer: artisanProfileReducer,
  selectArtisanProfileState,
  selectDisplayName,
  selectCraftType,
  selectRegion,
  selectBio,
  selectContactPhone,
  selectLoading,
  selectLoaded,
  selectSaving,
  selectError,
} = artisanProfileFeature;
