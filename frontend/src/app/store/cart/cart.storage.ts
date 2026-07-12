import { ActionReducer } from '@ngrx/store';

import { cartFeatureKey } from './cart.reducer';
import { CartState, initialCartState } from './cart.state';

const CART_STORAGE_KEY = 'sana3.cart';

function readFromStorage(): CartState {
  try {
    const raw = localStorage.getItem(CART_STORAGE_KEY);
    if (!raw) {
      return initialCartState;
    }
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed?.items) ? parsed : initialCartState;
  } catch {
    return initialCartState;
  }
}

function writeToStorage(state: CartState): void {
  try {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(state));
  } catch {
    // Storage unavailable (quota exceeded, private browsing) — cart just won't persist this session.
  }
}

// No backend cart endpoint (Assumed Default #1, docs/stories-sana3-ma-sprint3.md) — this meta-reducer
// hydrates the cart slice from localStorage on the first action and writes it back after every change,
// so the cart survives a page reload without any server round trip.
//
// Runs the wrapped reducer first (not before) — on the very first dispatch NgRx calls this with
// `state: undefined`, and `state && { ...state, ... }` would short-circuit to `undefined` instead of
// applying the hydrated cart, silently discarding it and then persisting the reducer's own empty
// default right back over the real localStorage value. Overriding the cart slice on the *already
// fully-defaulted* nextState sidesteps that entirely.
export function cartLocalStorageMetaReducer<T extends { [cartFeatureKey]: CartState }>(
  reducer: ActionReducer<T>,
): ActionReducer<T> {
  let hydrated = false;
  return (state, action) => {
    let nextState = reducer(state, action);
    if (!hydrated) {
      hydrated = true;
      nextState = { ...nextState, [cartFeatureKey]: readFromStorage() };
    }
    writeToStorage(nextState[cartFeatureKey]);
    return nextState;
  };
}
