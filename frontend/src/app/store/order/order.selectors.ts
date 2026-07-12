import { orderFeature } from './order.reducer';

export const {
  selectPlacing: selectOrderPlacing,
  selectPlacedOrder,
  selectError: selectOrderError,
} = orderFeature;
