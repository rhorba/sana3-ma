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
});
