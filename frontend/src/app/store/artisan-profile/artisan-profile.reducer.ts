import { createFeature, createReducer } from '@ngrx/store';
import { initialArtisanProfileState } from './artisan-profile.state';

export const artisanProfileFeature = createFeature({
  name: 'artisanProfile',
  reducer: createReducer(initialArtisanProfileState),
});

export const {
  name: artisanProfileFeatureKey,
  reducer: artisanProfileReducer,
  selectArtisanProfileState,
} = artisanProfileFeature;
