import { createSelector } from '@ngrx/store';

import { artisanProfileFeature } from './artisan-profile.reducer';

export const {
  selectDisplayName,
  selectCraftType,
  selectRegion,
  selectBio,
  selectContactPhone,
  selectLoading: selectProfileLoading,
  selectLoaded: selectProfileLoaded,
  selectSaving: selectProfileSaving,
  selectError: selectProfileError,
} = artisanProfileFeature;

export const selectHasProfile = createSelector(
  selectDisplayName,
  (displayName): boolean => displayName !== null,
);

export const selectProfileForm = createSelector(
  selectDisplayName,
  selectCraftType,
  selectRegion,
  selectBio,
  selectContactPhone,
  (displayName, craftType, region, bio, contactPhone) => ({
    displayName: displayName ?? '',
    craftType: craftType ?? '',
    region: region ?? '',
    bio: bio ?? '',
    contactPhone: contactPhone ?? '',
  }),
);
