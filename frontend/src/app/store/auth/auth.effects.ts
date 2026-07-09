import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, EMPTY, map, of, switchMap } from 'rxjs';

import { ApiError } from '../../core/auth/auth.models';
import { AuthService } from '../../core/auth/auth.service';
import { AuthActions } from './auth.actions';

function extractErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as ApiError | undefined;
    return body?.error?.message ?? fallback;
  }
  return fallback;
}

@Injectable()
export class AuthEffects {
  private readonly actions$ = inject(Actions);
  private readonly authService = inject(AuthService);

  register$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.register),
      switchMap(({ email, password, role }) =>
        this.authService.register({ email, password, role }).pipe(
          map((response) => AuthActions.registerSuccess({ response })),
          catchError((error) =>
            of(
              AuthActions.registerFailure({
                message: extractErrorMessage(error, 'Registration failed. Please try again.'),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      switchMap(({ email, password }) =>
        this.authService.login({ email, password }).pipe(
          map((response) => AuthActions.loginSuccess({ response })),
          catchError((error) =>
            of(
              AuthActions.loginFailure({
                message: extractErrorMessage(error, 'Incorrect email or password.'),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  refreshToken$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.refreshToken),
      switchMap(() =>
        this.authService.refresh().pipe(
          map((response) => AuthActions.refreshTokenSuccess({ response })),
          catchError(() => of(AuthActions.refreshTokenFailure())),
        ),
      ),
    ),
  );

  // The reducer already clears client state synchronously on `logout` — this just tells the
  // backend to expire the httpOnly refresh cookie so the session can't be silently restored.
  logout$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(AuthActions.logout),
        switchMap(() => this.authService.logout().pipe(catchError(() => EMPTY))),
      ),
    { dispatch: false },
  );
}
