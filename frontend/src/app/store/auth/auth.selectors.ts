import { createSelector } from '@ngrx/store';

import { authFeature } from './auth.reducer';

export const {
  selectAccessToken,
  selectUserId,
  selectEmail,
  selectRole,
  selectLoading: selectAuthLoading,
  selectError: selectAuthError,
} = authFeature;

export const selectIsAuthenticated = createSelector(
  selectAccessToken,
  (accessToken): boolean => accessToken !== null,
);

export const selectCurrentUser = createSelector(
  selectUserId,
  selectEmail,
  selectRole,
  (userId, email, role) => (userId && email && role ? { userId, email, role } : null),
);
