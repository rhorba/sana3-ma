import { OrderActions } from './order.actions';
import { orderFeature } from './order.reducer';
import { initialOrderState } from './order.state';

describe('orderFeature reducer', () => {
  const order = {
    id: 'order-1',
    buyerUserId: 'buyer-1',
    status: 'PLACED' as const,
    shippingAddress: '123 Rue Example, Fes',
    items: [],
    totals: [{ currency: 'MAD', amount: 900 }],
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-01T00:00:00Z',
  };

  it('sets placing on placeOrder', () => {
    const state = orderFeature.reducer(
      initialOrderState,
      OrderActions.placeOrder({ shippingAddress: 'Address', items: [] }),
    );

    expect(state.placing).toBe(true);
    expect(state.error).toBeNull();
  });

  it('stores the placed order on placeOrderSuccess', () => {
    const state = orderFeature.reducer(initialOrderState, OrderActions.placeOrderSuccess({ order }));

    expect(state.placing).toBe(false);
    expect(state.placedOrder).toEqual(order);
    expect(state.error).toBeNull();
  });

  it('stores the error message on placeOrderFailure', () => {
    const state = orderFeature.reducer(
      initialOrderState,
      OrderActions.placeOrderFailure({ message: 'No product found for id product-1' }),
    );

    expect(state.placing).toBe(false);
    expect(state.error).toBe('No product found for id product-1');
  });

  it('resets to the initial state on resetPlaceOrderState', () => {
    const placed = orderFeature.reducer(initialOrderState, OrderActions.placeOrderSuccess({ order }));

    const state = orderFeature.reducer(placed, OrderActions.resetPlaceOrderState());

    expect(state).toEqual(initialOrderState);
  });

  it('stores the list on listMyOrdersSuccess', () => {
    const state = orderFeature.reducer(initialOrderState, OrderActions.listMyOrdersSuccess({ orders: [order] }));

    expect(state.myOrders).toEqual([order]);
    expect(state.myOrdersLoading).toBe(false);
    expect(state.myOrdersLoaded).toBe(true);
  });

  it('replaces the matching order on cancelOrderSuccess', () => {
    const loaded = orderFeature.reducer(initialOrderState, OrderActions.listMyOrdersSuccess({ orders: [order] }));
    const cancelled = { ...order, status: 'CANCELLED' as const };

    const state = orderFeature.reducer(loaded, OrderActions.cancelOrderSuccess({ order: cancelled }));

    expect(state.myOrders).toEqual([cancelled]);
  });

  const artisanItem = {
    id: 'item-1',
    orderId: 'order-1',
    orderStatus: 'PLACED' as const,
    shippingAddress: '123 Rue Example, Fes',
    buyerEmail: 'buyer@example.com',
    productId: 'product-1',
    productName: 'Zellige Tile Set',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
    quantity: 2,
    lineTotal: 900,
    completed: false,
    completedAt: null,
    orderCreatedAt: '2026-01-01T00:00:00Z',
  };

  it('stores the list on listArtisanOrderItemsSuccess', () => {
    const state = orderFeature.reducer(
      initialOrderState,
      OrderActions.listArtisanOrderItemsSuccess({ items: [artisanItem] }),
    );

    expect(state.artisanOrderItems).toEqual([artisanItem]);
    expect(state.artisanOrderItemsLoading).toBe(false);
    expect(state.artisanOrderItemsLoaded).toBe(true);
  });

  it('replaces the matching item on completeArtisanOrderItemSuccess', () => {
    const loaded = orderFeature.reducer(
      initialOrderState,
      OrderActions.listArtisanOrderItemsSuccess({ items: [artisanItem] }),
    );
    const completed = { ...artisanItem, completed: true, completedAt: '2026-01-02T00:00:00Z' };

    const state = orderFeature.reducer(loaded, OrderActions.completeArtisanOrderItemSuccess({ item: completed }));

    expect(state.artisanOrderItems).toEqual([completed]);
  });
});
