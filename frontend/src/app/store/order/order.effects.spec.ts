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

  let actions$: Observable<Action>;
  let orderService: { placeOrder: ReturnType<typeof vi.fn> };
  let effects: OrderEffects;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [OrderEffects, provideMockActions(() => actions$), { provide: OrderService, useValue: orderService }],
    });
    effects = TestBed.inject(OrderEffects);
  }

  beforeEach(() => {
    orderService = { placeOrder: vi.fn() };
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
});
