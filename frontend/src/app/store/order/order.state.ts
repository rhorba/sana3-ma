import { ArtisanOrderItemResponse, OrderResponse } from '../../core/order/order.models';

export interface OrderState {
  placing: boolean;
  placedOrder: OrderResponse | null;
  error: string | null;

  // Buyer order history (Story 6.4)
  myOrders: OrderResponse[];
  myOrdersLoading: boolean;
  myOrdersLoaded: boolean;
  myOrdersError: string | null;

  // Artisan fulfillment queue (Story 6.5)
  artisanOrderItems: ArtisanOrderItemResponse[];
  artisanOrderItemsLoading: boolean;
  artisanOrderItemsLoaded: boolean;
  artisanOrderItemsError: string | null;
}

export const initialOrderState: OrderState = {
  placing: false,
  placedOrder: null,
  error: null,

  myOrders: [],
  myOrdersLoading: false,
  myOrdersLoaded: false,
  myOrdersError: null,

  artisanOrderItems: [],
  artisanOrderItemsLoading: false,
  artisanOrderItemsLoaded: false,
  artisanOrderItemsError: null,
};
