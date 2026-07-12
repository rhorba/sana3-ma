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

  listMyOrders$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.listMyOrders),
      switchMap(() =>
        this.orderService.listMyOrders().pipe(
          map((orders) => OrderActions.listMyOrdersSuccess({ orders })),
          catchError((error) =>
            of(
              OrderActions.listMyOrdersFailure({
                message: extractErrorMessage(error, "Couldn't load your orders. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  cancelOrder$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.cancelOrder),
      switchMap(({ id }) =>
        this.orderService.cancelOrder(id).pipe(
          map((order) => OrderActions.cancelOrderSuccess({ order })),
          catchError((error) =>
            of(
              OrderActions.cancelOrderFailure({
                message: extractErrorMessage(error, "Couldn't cancel this order. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  listArtisanOrderItems$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.listArtisanOrderItems),
      switchMap(() =>
        this.orderService.listArtisanOrderItems().pipe(
          map((items) => OrderActions.listArtisanOrderItemsSuccess({ items })),
          catchError((error) =>
            of(
              OrderActions.listArtisanOrderItemsFailure({
                message: extractErrorMessage(error, "Couldn't load your incoming orders. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  completeArtisanOrderItem$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OrderActions.completeArtisanOrderItem),
      switchMap(({ id }) =>
        this.orderService.completeArtisanOrderItem(id).pipe(
          map((item) => OrderActions.completeArtisanOrderItemSuccess({ item })),
          catchError((error) =>
            of(
              OrderActions.completeArtisanOrderItemFailure({
                message: extractErrorMessage(error, "Couldn't mark this item completed. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
