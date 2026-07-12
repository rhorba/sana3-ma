import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';

import { OrderResponse } from '../../core/order/order.models';
import { OrderActions } from '../../store/order/order.actions';
import { selectMyOrders, selectMyOrdersError, selectMyOrdersLoaded, selectMyOrdersLoading } from '../../store/order/order.selectors';

@Component({
  selector: 'app-orders',
  imports: [MatButtonModule, DatePipe],
  templateUrl: './orders.html',
  styleUrl: './orders.scss',
})
export class Orders {
  private readonly store = inject(Store);
  private readonly actions$ = inject(Actions);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly orders = this.store.selectSignal(selectMyOrders);
  protected readonly loading = this.store.selectSignal(selectMyOrdersLoading);
  protected readonly loaded = this.store.selectSignal(selectMyOrdersLoaded);
  protected readonly error = this.store.selectSignal(selectMyOrdersError);

  constructor() {
    this.store.dispatch(OrderActions.listMyOrders());

    this.actions$
      .pipe(ofType(OrderActions.cancelOrderFailure), takeUntilDestroyed())
      .subscribe(({ message }) => this.snackBar.open(message, 'Dismiss', { duration: 5000 }));
  }

  cancel(order: OrderResponse): void {
    if (confirm('Cancel this order?')) {
      this.store.dispatch(OrderActions.cancelOrder({ id: order.id }));
    }
  }
}
