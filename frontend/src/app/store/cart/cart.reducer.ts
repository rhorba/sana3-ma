import { createFeature, createReducer, on } from '@ngrx/store';

import { CartActions } from './cart.actions';
import { CartState, initialCartState } from './cart.state';

export const cartFeature = createFeature({
  name: 'cart',
  reducer: createReducer(
    initialCartState,
    on(CartActions.addItem, (state, { item, quantity }): CartState => {
      const existing = state.items.find((i) => i.productId === item.productId);
      if (existing) {
        return {
          ...state,
          items: state.items.map((i) =>
            i.productId === item.productId ? { ...i, quantity: i.quantity + quantity } : i,
          ),
        };
      }
      return { ...state, items: [...state.items, { ...item, quantity }] };
    }),
    on(CartActions.updateQuantity, (state, { productId, quantity }): CartState => ({
      ...state,
      items:
        quantity <= 0
          ? state.items.filter((i) => i.productId !== productId)
          : state.items.map((i) => (i.productId === productId ? { ...i, quantity } : i)),
    })),
    on(CartActions.removeItem, (state, { productId }): CartState => ({
      ...state,
      items: state.items.filter((i) => i.productId !== productId),
    })),
    on(CartActions.clearCart, (): CartState => initialCartState),
  ),
});

export const { name: cartFeatureKey, reducer: cartReducer, selectItems } = cartFeature;
