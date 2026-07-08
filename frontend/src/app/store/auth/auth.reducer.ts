import { createFeature, createReducer } from '@ngrx/store';
import { initialAuthState } from './auth.state';

export const authFeature = createFeature({
  name: 'auth',
  reducer: createReducer(initialAuthState),
});

export const { name: authFeatureKey, reducer: authReducer, selectAuthState } = authFeature;
