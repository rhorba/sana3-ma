import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { AuthResponse, LoginRequest, RegisterRequest } from '../../core/auth/auth.models';

export const AuthActions = createActionGroup({
  source: 'Auth',
  events: {
    Register: props<RegisterRequest>(),
    'Register Success': props<{ response: AuthResponse }>(),
    'Register Failure': props<{ message: string }>(),

    Login: props<LoginRequest>(),
    'Login Success': props<{ response: AuthResponse }>(),
    'Login Failure': props<{ message: string }>(),

    // Fired once on app bootstrap to restore a session from the httpOnly refresh cookie.
    'Refresh Token': emptyProps(),
    'Refresh Token Success': props<{ response: AuthResponse }>(),
    // No cookie, or it's expired — silently stay logged out, no error surfaced to the user.
    'Refresh Token Failure': emptyProps(),

    Logout: emptyProps(),
  },
});
