import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { ArtisanOrderItemResponse, OrderResponse, PlaceOrderLineItemRequest } from '../../core/order/order.models';

export const OrderActions = createActionGroup({
  source: 'Order',
  events: {
    'Place Order': props<{ shippingAddress: string; items: PlaceOrderLineItemRequest[] }>(),
    'Place Order Success': props<{ order: OrderResponse }>(),
    'Place Order Failure': props<{ message: string }>(),
    // Dispatched when /checkout mounts so a stale confirmation/error from a previous order
    // doesn't linger over a fresh cart.
    'Reset Place Order State': emptyProps(),

    'List My Orders': emptyProps(),
    'List My Orders Success': props<{ orders: OrderResponse[] }>(),
    'List My Orders Failure': props<{ message: string }>(),

    'Cancel Order': props<{ id: string }>(),
    'Cancel Order Success': props<{ order: OrderResponse }>(),
    'Cancel Order Failure': props<{ message: string }>(),

    'List Artisan Order Items': emptyProps(),
    'List Artisan Order Items Success': props<{ items: ArtisanOrderItemResponse[] }>(),
    'List Artisan Order Items Failure': props<{ message: string }>(),

    'Complete Artisan Order Item': props<{ id: string }>(),
    'Complete Artisan Order Item Success': props<{ item: ArtisanOrderItemResponse }>(),
    'Complete Artisan Order Item Failure': props<{ message: string }>(),
  },
});
