import { createFeature, createReducer, on } from '@ngrx/store';

import { OrderActions } from './order.actions';
import { initialOrderState, OrderState } from './order.state';

export const orderFeature = createFeature({
  name: 'order',
  reducer: createReducer(
    initialOrderState,
    on(OrderActions.placeOrder, (state): OrderState => ({
      ...state,
      placing: true,
      error: null,
    })),
    on(OrderActions.placeOrderSuccess, (state, { order }): OrderState => ({
      ...state,
      placing: false,
      placedOrder: order,
      error: null,
    })),
    on(OrderActions.placeOrderFailure, (state, { message }): OrderState => ({
      ...state,
      placing: false,
      error: message,
    })),
    on(OrderActions.resetPlaceOrderState, (): OrderState => initialOrderState),
  ),
});

export const {
  name: orderFeatureKey,
  reducer: orderReducer,
  selectPlacing,
  selectPlacedOrder,
  selectError,
} = orderFeature;
