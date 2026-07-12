import { orderFeature } from './order.reducer';

export const {
  selectPlacing: selectOrderPlacing,
  selectPlacedOrder,
  selectError: selectOrderError,
  selectMyOrders,
  selectMyOrdersLoading,
  selectMyOrdersLoaded,
  selectMyOrdersError,
  selectArtisanOrderItems,
  selectArtisanOrderItemsLoading,
  selectArtisanOrderItemsLoaded,
  selectArtisanOrderItemsError,
} = orderFeature;
