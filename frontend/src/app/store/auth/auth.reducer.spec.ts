import { AuthActions } from './auth.actions';
import { authReducer } from './auth.reducer';
import { AuthState, initialAuthState } from './auth.state';

describe('authReducer', () => {
  const response = {
    userId: 'user-1',
    email: 'artisan@example.com',
    role: 'ARTISAN' as const,
    accessToken: 'access-token',
    expiresInSeconds: 900,
  };

  it('returns the initial state for an unknown action', () => {
    const state = authReducer(undefined, { type: '@@INIT' });
    expect(state).toEqual(initialAuthState);
  });

  it('sets loading and clears error on login/register', () => {
    const withError: AuthState = { ...initialAuthState, error: 'previous error' };
    const state = authReducer(withError, AuthActions.login({ email: 'a@b.com', password: 'password123' }));
    expect(state.loading).toBe(true);
    expect(state.error).toBeNull();
  });

  it('populates user fields on login/register/refresh success', () => {
    const state = authReducer(initialAuthState, AuthActions.loginSuccess({ response }));
    expect(state).toEqual({
      userId: response.userId,
      email: response.email,
      role: response.role,
      accessToken: response.accessToken,
      loading: false,
      error: null,
    });
  });

  it('sets an error message and clears loading on login/register failure', () => {
    const loading: AuthState = { ...initialAuthState, loading: true };
    const state = authReducer(loading, AuthActions.loginFailure({ message: 'Incorrect email or password.' }));
    expect(state.loading).toBe(false);
    expect(state.error).toBe('Incorrect email or password.');
  });

  it('resets to initial state on refresh failure (no session to restore)', () => {
    const authenticated = authReducer(initialAuthState, AuthActions.loginSuccess({ response }));
    const state = authReducer(authenticated, AuthActions.refreshTokenFailure());
    expect(state).toEqual(initialAuthState);
  });

  it('resets to initial state on logout', () => {
    const authenticated = authReducer(initialAuthState, AuthActions.loginSuccess({ response }));
    const state = authReducer(authenticated, AuthActions.logout());
    expect(state).toEqual(initialAuthState);
  });
});
