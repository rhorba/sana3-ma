import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

import { ArtisanProfileService } from '../../core/artisan-profile/artisan-profile.service';
import { extractErrorMessage } from '../../core/http-error.util';
import { ArtisanProfileActions } from './artisan-profile.actions';

@Injectable()
export class ArtisanProfileEffects {
  private readonly actions$ = inject(Actions);
  private readonly artisanProfileService = inject(ArtisanProfileService);

  loadProfile$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ArtisanProfileActions.loadProfile),
      switchMap(() =>
        this.artisanProfileService.getMyProfile().pipe(
          map((response) => ArtisanProfileActions.loadProfileSuccess({ response })),
          catchError((error) => {
            if (error instanceof HttpErrorResponse && error.status === 404) {
              return of(ArtisanProfileActions.loadProfileNotFound());
            }
            return of(
              ArtisanProfileActions.loadProfileFailure({
                message: extractErrorMessage(error, "Couldn't load your profile. Please try again."),
              }),
            );
          }),
        ),
      ),
    ),
  );

  saveProfile$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ArtisanProfileActions.saveProfile),
      switchMap(({ displayName, craftType, region, bio, contactPhone }) =>
        this.artisanProfileService.upsertMyProfile({ displayName, craftType, region, bio, contactPhone }).pipe(
          map((response) => ArtisanProfileActions.saveProfileSuccess({ response })),
          catchError((error) =>
            of(
              ArtisanProfileActions.saveProfileFailure({
                message: extractErrorMessage(error, "Couldn't save, try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
