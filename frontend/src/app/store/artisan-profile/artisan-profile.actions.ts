import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { ArtisanProfileResponse, UpsertArtisanProfileRequest } from '../../core/artisan-profile/artisan-profile.models';

export const ArtisanProfileActions = createActionGroup({
  source: 'Artisan Profile',
  events: {
    'Load Profile': emptyProps(),
    'Load Profile Success': props<{ response: ArtisanProfileResponse }>(),
    // No profile yet is a normal empty state (Story 2.3), not an error.
    'Load Profile Not Found': emptyProps(),
    'Load Profile Failure': props<{ message: string }>(),

    'Save Profile': props<UpsertArtisanProfileRequest>(),
    'Save Profile Success': props<{ response: ArtisanProfileResponse }>(),
    'Save Profile Failure': props<{ message: string }>(),
  },
});
