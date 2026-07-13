import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Action } from '@ngrx/store';
import { Observable, firstValueFrom, of, throwError } from 'rxjs';

import { ArtisanProfileService } from '../../core/artisan-profile/artisan-profile.service';
import { ArtisanProfileActions } from './artisan-profile.actions';
import { ArtisanProfileEffects } from './artisan-profile.effects';

describe('ArtisanProfileEffects', () => {
  const response = {
    id: 'profile-1',
    displayName: 'Yassine Zellige',
    craftType: 'Zellige tiling',
    region: 'Fes',
    bio: 'Third-generation artisan.',
    contactPhone: '+212600000000',
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-02T00:00:00Z',
  };

  const saveRequest = {
    displayName: 'Yassine Zellige',
    craftType: 'Zellige tiling',
    region: 'Fes',
    bio: 'Third-generation artisan.',
    contactPhone: '+212600000000',
  };

  let actions$: Observable<Action>;
  let artisanProfileService: { getMyProfile: ReturnType<typeof vi.fn>; upsertMyProfile: ReturnType<typeof vi.fn> };
  let effects: ArtisanProfileEffects;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [
        ArtisanProfileEffects,
        provideMockActions(() => actions$),
        { provide: ArtisanProfileService, useValue: artisanProfileService },
      ],
    });
    effects = TestBed.inject(ArtisanProfileEffects);
  }

  beforeEach(() => {
    artisanProfileService = {
      getMyProfile: vi.fn(),
      upsertMyProfile: vi.fn(),
    };
  });

  it('loadProfile$ maps a successful fetch to loadProfileSuccess', async () => {
    artisanProfileService.getMyProfile.mockReturnValue(of(response));
    actions$ = of(ArtisanProfileActions.loadProfile());
    setup();

    const result = await firstValueFrom(effects.loadProfile$);

    expect(result).toEqual(ArtisanProfileActions.loadProfileSuccess({ response }));
  });

  it('loadProfile$ maps a 404 to loadProfileNotFound (empty state, not an error)', async () => {
    artisanProfileService.getMyProfile.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 404 })));
    actions$ = of(ArtisanProfileActions.loadProfile());
    setup();

    const result = await firstValueFrom(effects.loadProfile$);

    expect(result).toEqual(ArtisanProfileActions.loadProfileNotFound());
  });

  it('loadProfile$ maps other failures to loadProfileFailure', async () => {
    artisanProfileService.getMyProfile.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(ArtisanProfileActions.loadProfile());
    setup();

    const result = await firstValueFrom(effects.loadProfile$);

    expect(result).toEqual(
      ArtisanProfileActions.loadProfileFailure({ message: "Couldn't load your profile. Please try again." }),
    );
  });

  it('saveProfile$ maps a successful save to saveProfileSuccess', async () => {
    artisanProfileService.upsertMyProfile.mockReturnValue(of(response));
    actions$ = of(ArtisanProfileActions.saveProfile(saveRequest));
    setup();

    const result = await firstValueFrom(effects.saveProfile$);

    expect(artisanProfileService.upsertMyProfile).toHaveBeenCalledWith(saveRequest);
    expect(result).toEqual(ArtisanProfileActions.saveProfileSuccess({ response }));
  });

  it('saveProfile$ maps a failure to saveProfileFailure with the backend message', async () => {
    artisanProfileService.upsertMyProfile.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
            error: { error: { code: 'NOT_AN_ARTISAN', message: 'Only artisans can update a profile', details: [] } },
          }),
      ),
    );
    actions$ = of(ArtisanProfileActions.saveProfile(saveRequest));
    setup();

    const result = await firstValueFrom(effects.saveProfile$);

    expect(result).toEqual(
      ArtisanProfileActions.saveProfileFailure({ message: 'Only artisans can update a profile' }),
    );
  });
});
