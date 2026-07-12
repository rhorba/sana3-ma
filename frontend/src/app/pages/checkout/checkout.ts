import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { selectCartItems, selectCartTotalsByCurrency } from '../../store/cart/cart.selectors';
import { OrderActions } from '../../store/order/order.actions';
import { selectOrderError, selectOrderPlacing, selectPlacedOrder } from '../../store/order/order.selectors';

@Component({
  selector: 'app-checkout',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './checkout.html',
  styleUrl: './checkout.scss',
})
export class Checkout {
  private readonly formBuilder = inject(FormBuilder);
  private readonly store = inject(Store);

  protected readonly items = this.store.selectSignal(selectCartItems);
  protected readonly totals = this.store.selectSignal(selectCartTotalsByCurrency);
  protected readonly placing = this.store.selectSignal(selectOrderPlacing);
  protected readonly placedOrder = this.store.selectSignal(selectPlacedOrder);
  protected readonly error = this.store.selectSignal(selectOrderError);

  readonly form = this.formBuilder.nonNullable.group({
    shippingAddress: ['', Validators.required],
  });

  constructor() {
    // A stale confirmation/error from a previous visit shouldn't linger over a fresh cart.
    this.store.dispatch(OrderActions.resetPlaceOrderState());
  }

  placeOrder(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { shippingAddress } = this.form.getRawValue();
    this.store.dispatch(
      OrderActions.placeOrder({
        shippingAddress,
        items: this.items().map((item) => ({ productId: item.productId, quantity: item.quantity })),
      }),
    );
  }
}
