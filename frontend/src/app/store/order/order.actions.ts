import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { OrderResponse, PlaceOrderLineItemRequest } from '../../core/order/order.models';

export const OrderActions = createActionGroup({
  source: 'Order',
  events: {
    'Place Order': props<{ shippingAddress: string; items: PlaceOrderLineItemRequest[] }>(),
    'Place Order Success': props<{ order: OrderResponse }>(),
    'Place Order Failure': props<{ message: string }>(),
    // Dispatched when /checkout mounts so a stale confirmation/error from a previous order
    // doesn't linger over a fresh cart.
    'Reset Place Order State': emptyProps(),
  },
});
