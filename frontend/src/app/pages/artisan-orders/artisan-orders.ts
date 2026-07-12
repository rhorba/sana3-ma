import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';

import { ArtisanOrderItemResponse } from '../../core/order/order.models';
import { OrderActions } from '../../store/order/order.actions';
import {
  selectArtisanOrderItems,
  selectArtisanOrderItemsError,
  selectArtisanOrderItemsLoaded,
  selectArtisanOrderItemsLoading,
} from '../../store/order/order.selectors';

@Component({
  selector: 'app-artisan-orders',
  imports: [MatButtonModule, DatePipe],
  templateUrl: './artisan-orders.html',
  styleUrl: './artisan-orders.scss',
})
export class ArtisanOrders {
  private readonly store = inject(Store);
  private readonly actions$ = inject(Actions);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly items = this.store.selectSignal(selectArtisanOrderItems);
  protected readonly loading = this.store.selectSignal(selectArtisanOrderItemsLoading);
  protected readonly loaded = this.store.selectSignal(selectArtisanOrderItemsLoaded);
  protected readonly error = this.store.selectSignal(selectArtisanOrderItemsError);

  constructor() {
    this.store.dispatch(OrderActions.listArtisanOrderItems());

    this.actions$
      .pipe(ofType(OrderActions.completeArtisanOrderItemFailure), takeUntilDestroyed())
      .subscribe(({ message }) => this.snackBar.open(message, 'Dismiss', { duration: 5000 }));
  }

  complete(item: ArtisanOrderItemResponse): void {
    this.store.dispatch(OrderActions.completeArtisanOrderItem({ id: item.id }));
  }
}
