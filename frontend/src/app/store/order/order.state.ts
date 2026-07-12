import { OrderResponse } from '../../core/order/order.models';

export interface OrderState {
  placing: boolean;
  placedOrder: OrderResponse | null;
  error: string | null;
}

export const initialOrderState: OrderState = {
  placing: false,
  placedOrder: null,
  error: null,
};
