import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { CartItem } from './cart.state';

export const CartActions = createActionGroup({
  source: 'Cart',
  events: {
    // Snapshot of the product as shown at add-to-cart time (Story 5.1 technical note) — the cart
    // still renders sensibly even if the product changes or is deleted before checkout.
    'Add Item': props<{ item: Omit<CartItem, 'quantity'>; quantity: number }>(),
    'Update Quantity': props<{ productId: string; quantity: number }>(),
    'Remove Item': props<{ productId: string }>(),
    'Clear Cart': emptyProps(),
  },
});
