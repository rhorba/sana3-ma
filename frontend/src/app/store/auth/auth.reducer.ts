import { createFeature, createReducer, on } from '@ngrx/store';

import { AuthActions } from './auth.actions';
import { AuthState, initialAuthState } from './auth.state';

export const authFeature = createFeature({
  name: 'auth',
  reducer: createReducer(
    initialAuthState,
    on(AuthActions.register, AuthActions.login, (state): AuthState => ({
      ...state,
      loading: true,
      error: null,
    })),
    on(AuthActions.refreshToken, (state): AuthState => ({
      ...state,
      // Silent — no loading spinner for the bootstrap session-restore attempt.
      error: null,
    })),
    on(
      AuthActions.registerSuccess,
      AuthActions.loginSuccess,
      AuthActions.refreshTokenSuccess,
      (state, { response }): AuthState => ({
        ...state,
        userId: response.userId,
        email: response.email,
        role: response.role,
        accessToken: response.accessToken,
        loading: false,
        error: null,
      }),
    ),
    on(AuthActions.registerFailure, AuthActions.loginFailure, (state, { message }): AuthState => ({
      ...state,
      loading: false,
      error: message,
    })),
    on(AuthActions.refreshTokenFailure, AuthActions.logout, (): AuthState => initialAuthState),
  ),
});

export const {
  name: authFeatureKey,
  reducer: authReducer,
  selectAuthState,
  selectUserId,
  selectEmail,
  selectRole,
  selectAccessToken,
  selectLoading,
  selectError,
} = authFeature;
