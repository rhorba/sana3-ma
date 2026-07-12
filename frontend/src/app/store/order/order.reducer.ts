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

    on(OrderActions.listMyOrders, (state): OrderState => ({
      ...state,
      myOrdersLoading: true,
      myOrdersError: null,
    })),
    on(OrderActions.listMyOrdersSuccess, (state, { orders }): OrderState => ({
      ...state,
      myOrders: orders,
      myOrdersLoading: false,
      myOrdersLoaded: true,
      myOrdersError: null,
    })),
    on(OrderActions.listMyOrdersFailure, (state, { message }): OrderState => ({
      ...state,
      myOrdersLoading: false,
      myOrdersError: message,
    })),
    on(OrderActions.cancelOrderSuccess, (state, { order }): OrderState => ({
      ...state,
      myOrders: state.myOrders.map((existing) => (existing.id === order.id ? order : existing)),
    })),
    on(OrderActions.cancelOrderFailure, (state, { message }): OrderState => ({
      ...state,
      myOrdersError: message,
    })),

    on(OrderActions.listArtisanOrderItems, (state): OrderState => ({
      ...state,
      artisanOrderItemsLoading: true,
      artisanOrderItemsError: null,
    })),
    on(OrderActions.listArtisanOrderItemsSuccess, (state, { items }): OrderState => ({
      ...state,
      artisanOrderItems: items,
      artisanOrderItemsLoading: false,
      artisanOrderItemsLoaded: true,
      artisanOrderItemsError: null,
    })),
    on(OrderActions.listArtisanOrderItemsFailure, (state, { message }): OrderState => ({
      ...state,
      artisanOrderItemsLoading: false,
      artisanOrderItemsError: message,
    })),
    on(OrderActions.completeArtisanOrderItemSuccess, (state, { item }): OrderState => ({
      ...state,
      artisanOrderItems: state.artisanOrderItems.map((existing) => (existing.id === item.id ? item : existing)),
    })),
    on(OrderActions.completeArtisanOrderItemFailure, (state, { message }): OrderState => ({
      ...state,
      artisanOrderItemsError: message,
    })),
  ),
});

export const {
  name: orderFeatureKey,
  reducer: orderReducer,
  selectPlacing,
  selectPlacedOrder,
  selectError,
  selectMyOrders,
  selectMyOrdersLoading,
  selectMyOrdersLoaded,
  selectMyOrdersError,
  selectArtisanOrderItems,
  selectArtisanOrderItemsLoading,
  selectArtisanOrderItemsLoaded,
  selectArtisanOrderItemsError,
} = orderFeature;
