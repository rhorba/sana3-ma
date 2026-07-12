import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

import { extractErrorMessage } from '../../core/http-error.util';
import { OrderService } from '../../core/order/order.service';
import { CartActions } from '../cart/cart.actions';
import { OrderActions } from './order.actions';

@Injectable()
export class OrderEffects {
  private readonly actions$ = inject(Actions);
  private readonly orderService = inject(OrderService);

  placeOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.placeOrder),
      switchMap(({ shippingAddress, items }) =>
        this.orderService.placeOrder({ shippingAddress, items }).pipe(
          map((order) => OrderActions.placeOrderSuccess({ order })),
          catchError((error) =>
            of(
              OrderActions.placeOrderFailure({
                message: extractErrorMessage(error, "Couldn't place your order. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  // A successful order is the one place the cart's job is done — clear it here rather than
  // making every dispatcher of placeOrder remember to also clear the cart.
  clearCartOnPlaceOrderSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.placeOrderSuccess),
      map(() => CartActions.clearCart()),
    ),
  );
}
