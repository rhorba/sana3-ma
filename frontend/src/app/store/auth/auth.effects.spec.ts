import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Action } from '@ngrx/store';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, firstValueFrom, of, throwError } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';
import { AuthActions } from './auth.actions';
import { AuthEffects } from './auth.effects';

describe('AuthEffects', () => {
  const response = {
    userId: 'user-1',
    email: 'artisan@example.com',
    role: 'ARTISAN' as const,
    accessToken: 'access-token',
    expiresInSeconds: 900,
  };

  let actions$: Observable<Action>;
  let authService: {
    register: ReturnType<typeof vi.fn>;
    login: ReturnType<typeof vi.fn>;
    refresh: ReturnType<typeof vi.fn>;
    logout: ReturnType<typeof vi.fn>;
  };
  let effects: AuthEffects;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [AuthEffects, provideMockActions(() => actions$), { provide: AuthService, useValue: authService }],
    });
    effects = TestBed.inject(AuthEffects);
  }

  beforeEach(() => {
    authService = {
      register: vi.fn(),
      login: vi.fn(),
      refresh: vi.fn(),
      logout: vi.fn(),
    };
  });

  it('login$ maps a successful login to loginSuccess', async () => {
    authService.login.mockReturnValue(of(response));
    actions$ = of(AuthActions.login({ email: 'a@b.com', password: 'password123' }));
    setup();

    const result = await firstValueFrom(effects.login$);

    expect(authService.login).toHaveBeenCalledWith({ email: 'a@b.com', password: 'password123' });
    expect(result).toEqual(AuthActions.loginSuccess({ response }));
  });

  it('login$ maps a failed login to loginFailure with the backend message', async () => {
    authService.login.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 401,
            error: { error: { code: 'UNAUTHORIZED', message: 'Bad credentials', details: [] } },
          }),
      ),
    );
    actions$ = of(AuthActions.login({ email: 'a@b.com', password: 'wrong' }));
    setup();

    const result = await firstValueFrom(effects.login$);

    expect(result).toEqual(AuthActions.loginFailure({ message: 'Bad credentials' }));
  });

  it('register$ maps a successful register to registerSuccess', async () => {
    authService.register.mockReturnValue(of(response));
    actions$ = of(AuthActions.register({ email: 'a@b.com', password: 'password123', role: 'ARTISAN' }));
    setup();

    const result = await firstValueFrom(effects.register$);

    expect(result).toEqual(AuthActions.registerSuccess({ response }));
  });

  it('register$ falls back to a generic message when the backend gives none', async () => {
    authService.register.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(AuthActions.register({ email: 'a@b.com', password: 'password123', role: 'BUYER' }));
    setup();

    const result = await firstValueFrom(effects.register$);

    expect(result).toEqual(AuthActions.registerFailure({ message: 'Registration failed. Please try again.' }));
  });

  it('refreshToken$ maps success to refreshTokenSuccess', async () => {
    authService.refresh.mockReturnValue(of(response));
    actions$ = of(AuthActions.refreshToken());
    setup();

    const result = await firstValueFrom(effects.refreshToken$);

    expect(result).toEqual(AuthActions.refreshTokenSuccess({ response }));
  });

  it('refreshToken$ silently maps failure to refreshTokenFailure (no cookie is a normal state)', async () => {
    authService.refresh.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    actions$ = of(AuthActions.refreshToken());
    setup();

    const result = await firstValueFrom(effects.refreshToken$);

    expect(result).toEqual(AuthActions.refreshTokenFailure());
  });

  it('logout$ calls the backend to expire the refresh cookie (non-dispatching)', async () => {
    authService.logout.mockReturnValue(of(undefined));
    actions$ = of(AuthActions.logout());
    setup();

    await firstValueFrom(effects.logout$);

    expect(authService.logout).toHaveBeenCalled();
  });

  it('logout$ silently swallows a failed backend call', async () => {
    authService.logout.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(AuthActions.logout());
    setup();

    const result = await firstValueFrom(effects.logout$, { defaultValue: 'completed-without-emitting' });

    expect(result).toBe('completed-without-emitting');
  });
});
