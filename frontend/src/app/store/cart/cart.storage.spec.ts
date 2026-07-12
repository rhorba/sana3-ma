import { ActionReducer } from '@ngrx/store';

import { cartFeatureKey } from './cart.reducer';
import { cartLocalStorageMetaReducer } from './cart.storage';
import { CartState } from './cart.state';

interface TestState {
  [cartFeatureKey]: CartState;
}

const item: CartState['items'][number] = {
  productId: 'p1',
  productName: 'Tile',
  priceAmount: 10,
  priceCurrency: 'MAD',
  craftType: 'Pottery',
  imageUrl: null,
  quantity: 2,
};

describe('cartLocalStorageMetaReducer', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('hydrates from localStorage on the very first dispatch, where NgRx passes state: undefined', () => {
    const persisted: CartState = { items: [item] };
    localStorage.setItem('sana3.cart', JSON.stringify(persisted));
    // Mirrors createReducer's own behaviour: falls back to a default when state is undefined,
    // exactly what the real root reducer does on NgRx's very first ever dispatch.
    const defaultingReducer: ActionReducer<TestState> = (state) =>
      state ?? { [cartFeatureKey]: { items: [] } };
    const metaReducer = cartLocalStorageMetaReducer(defaultingReducer);

    const result = metaReducer(undefined, { type: '@ngrx/store/init' });

    expect(result[cartFeatureKey]).toEqual(persisted);
  });

  it('falls back to empty state when localStorage has nothing', () => {
    const identityReducer: ActionReducer<TestState> = (state) => state!;
    const metaReducer = cartLocalStorageMetaReducer(identityReducer);

    const result = metaReducer({ [cartFeatureKey]: { items: [] } }, { type: '@ngrx/store/init' });

    expect(result[cartFeatureKey]).toEqual({ items: [] });
  });

  it('writes the resulting cart state back to localStorage for a real action after the initial hydration', () => {
    const nextCartState: CartState = { items: [item] };
    const reducer: ActionReducer<TestState> = (state, action) =>
      action.type === 'Cart/Add Item'
        ? { ...state!, [cartFeatureKey]: nextCartState }
        : (state ?? { [cartFeatureKey]: { items: [] } });
    const metaReducer = cartLocalStorageMetaReducer(reducer);

    // First dispatch is always the store's own init action, exactly like a real app bootstrap —
    // this is what consumes the one-time hydration step.
    metaReducer(undefined, { type: '@ngrx/store/init' });
    metaReducer({ [cartFeatureKey]: { items: [] } }, { type: 'Cart/Add Item' });

    expect(JSON.parse(localStorage.getItem('sana3.cart')!)).toEqual(nextCartState);
  });

  it('ignores malformed localStorage content', () => {
    localStorage.setItem('sana3.cart', 'not-json');
    const identityReducer: ActionReducer<TestState> = (state) => state!;
    const metaReducer = cartLocalStorageMetaReducer(identityReducer);

    const result = metaReducer({ [cartFeatureKey]: { items: [] } }, { type: '@ngrx/store/init' });

    expect(result[cartFeatureKey]).toEqual({ items: [] });
  });
});
