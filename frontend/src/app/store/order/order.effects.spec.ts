import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Action } from '@ngrx/store';
import { Observable, firstValueFrom, of, throwError } from 'rxjs';

import { OrderService } from '../../core/order/order.service';
import { CartActions } from '../cart/cart.actions';
import { OrderActions } from './order.actions';
import { OrderEffects } from './order.effects';

describe('OrderEffects', () => {
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

  const placeOrderCommand = {
    shippingAddress: '123 Rue Example, Fes',
    items: [{ productId: 'product-1', quantity: 2 }],
  };

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

  let actions$: Observable<Action>;
  let orderService: {
    placeOrder: ReturnType<typeof vi.fn>;
    listMyOrders: ReturnType<typeof vi.fn>;
    cancelOrder: ReturnType<typeof vi.fn>;
    listArtisanOrderItems: ReturnType<typeof vi.fn>;
    completeArtisanOrderItem: ReturnType<typeof vi.fn>;
  };
  let effects: OrderEffects;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [OrderEffects, provideMockActions(() => actions$), { provide: OrderService, useValue: orderService }],
    });
    effects = TestBed.inject(OrderEffects);
  }

  beforeEach(() => {
    orderService = {
      placeOrder: vi.fn(),
      listMyOrders: vi.fn(),
      cancelOrder: vi.fn(),
      listArtisanOrderItems: vi.fn(),
      completeArtisanOrderItem: vi.fn(),
    };
  });

  it('placeOrder$ maps a successful placement to placeOrderSuccess', async () => {
    orderService.placeOrder.mockReturnValue(of(order));
    actions$ = of(OrderActions.placeOrder(placeOrderCommand));
    setup();

    const result = await firstValueFrom(effects.placeOrder$);

    expect(orderService.placeOrder).toHaveBeenCalledWith(placeOrderCommand);
    expect(result).toEqual(OrderActions.placeOrderSuccess({ order }));
  });

  it('placeOrder$ maps a PRODUCT_NOT_FOUND failure to placeOrderFailure with the backend message', async () => {
    orderService.placeOrder.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
            error: { error: { code: 'PRODUCT_NOT_FOUND', message: 'No product found for id product-1', details: [] } },
          }),
      ),
    );
    actions$ = of(OrderActions.placeOrder(placeOrderCommand));
    setup();

    const result = await firstValueFrom(effects.placeOrder$);

    expect(result).toEqual(OrderActions.placeOrderFailure({ message: 'No product found for id product-1' }));
  });

  it('placeOrder$ falls back to a generic message when the backend gives no error body', async () => {
    orderService.placeOrder.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(OrderActions.placeOrder(placeOrderCommand));
    setup();

    const result = await firstValueFrom(effects.placeOrder$);

    expect(result).toEqual(
      OrderActions.placeOrderFailure({ message: "Couldn't place your order. Please try again." }),
    );
  });

  it('clearCartOnPlaceOrderSuccess$ dispatches CartActions.clearCart after a successful order', async () => {
    actions$ = of(OrderActions.placeOrderSuccess({ order }));
    setup();

    const result = await firstValueFrom(effects.clearCartOnPlaceOrderSuccess$);

    expect(result).toEqual(CartActions.clearCart());
  });

  it('listMyOrders$ maps a successful fetch to listMyOrdersSuccess', async () => {
    orderService.listMyOrders.mockReturnValue(of([order]));
    actions$ = of(OrderActions.listMyOrders());
    setup();

    const result = await firstValueFrom(effects.listMyOrders$);

    expect(result).toEqual(OrderActions.listMyOrdersSuccess({ orders: [order] }));
  });

  it('listMyOrders$ maps a failure to listMyOrdersFailure', async () => {
    orderService.listMyOrders.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(OrderActions.listMyOrders());
    setup();

    const result = await firstValueFrom(effects.listMyOrders$);

    expect(result).toEqual(
      OrderActions.listMyOrdersFailure({ message: "Couldn't load your orders. Please try again." }),
    );
  });

  it('cancelOrder$ maps a successful cancel to cancelOrderSuccess', async () => {
    const cancelled = { ...order, status: 'CANCELLED' as const };
    orderService.cancelOrder.mockReturnValue(of(cancelled));
    actions$ = of(OrderActions.cancelOrder({ id: order.id }));
    setup();

    const result = await firstValueFrom(effects.cancelOrder$);

    expect(orderService.cancelOrder).toHaveBeenCalledWith(order.id);
    expect(result).toEqual(OrderActions.cancelOrderSuccess({ order: cancelled }));
  });

  it('cancelOrder$ maps an already-fulfilled-item conflict to cancelOrderFailure with the backend message', async () => {
    orderService.cancelOrder.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            error: {
              error: {
                code: 'ORDER_HAS_COMPLETED_ITEMS',
                message: 'Cannot cancel an order that already has a fulfilled item',
                details: [],
              },
            },
          }),
      ),
    );
    actions$ = of(OrderActions.cancelOrder({ id: order.id }));
    setup();

    const result = await firstValueFrom(effects.cancelOrder$);

    expect(result).toEqual(
      OrderActions.cancelOrderFailure({ message: 'Cannot cancel an order that already has a fulfilled item' }),
    );
  });

  it('listArtisanOrderItems$ maps a successful fetch to listArtisanOrderItemsSuccess', async () => {
    orderService.listArtisanOrderItems.mockReturnValue(of([artisanItem]));
    actions$ = of(OrderActions.listArtisanOrderItems());
    setup();

    const result = await firstValueFrom(effects.listArtisanOrderItems$);

    expect(result).toEqual(OrderActions.listArtisanOrderItemsSuccess({ items: [artisanItem] }));
  });

  it('listArtisanOrderItems$ maps a failure to listArtisanOrderItemsFailure', async () => {
    orderService.listArtisanOrderItems.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(OrderActions.listArtisanOrderItems());
    setup();

    const result = await firstValueFrom(effects.listArtisanOrderItems$);

    expect(result).toEqual(
      OrderActions.listArtisanOrderItemsFailure({ message: "Couldn't load your incoming orders. Please try again." }),
    );
  });

  it('completeArtisanOrderItem$ maps a successful completion to completeArtisanOrderItemSuccess', async () => {
    const completed = { ...artisanItem, completed: true, completedAt: '2026-01-02T00:00:00Z' };
    orderService.completeArtisanOrderItem.mockReturnValue(of(completed));
    actions$ = of(OrderActions.completeArtisanOrderItem({ id: artisanItem.id }));
    setup();

    const result = await firstValueFrom(effects.completeArtisanOrderItem$);

    expect(orderService.completeArtisanOrderItem).toHaveBeenCalledWith(artisanItem.id);
    expect(result).toEqual(OrderActions.completeArtisanOrderItemSuccess({ item: completed }));
  });

  it('completeArtisanOrderItem$ maps an already-completed conflict to completeArtisanOrderItemFailure', async () => {
    orderService.completeArtisanOrderItem.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            error: { error: { code: 'ORDER_ITEM_ALREADY_COMPLETED', message: 'Order item is already completed', details: [] } },
          }),
      ),
    );
    actions$ = of(OrderActions.completeArtisanOrderItem({ id: artisanItem.id }));
    setup();

    const result = await firstValueFrom(effects.completeArtisanOrderItem$);

    expect(result).toEqual(
      OrderActions.completeArtisanOrderItemFailure({ message: 'Order item is already completed' }),
    );
  });
});
