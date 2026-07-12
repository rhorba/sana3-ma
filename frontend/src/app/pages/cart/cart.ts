import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { API_ORIGIN } from '../../core/api-origin';
import { CartActions } from '../../store/cart/cart.actions';
import { selectCartItems, selectCartTotalsByCurrency } from '../../store/cart/cart.selectors';

@Component({
  selector: 'app-cart',
  imports: [RouterLink, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './cart.html',
  styleUrl: './cart.scss',
})
export class Cart {
  private readonly store = inject(Store);

  protected readonly apiOrigin = API_ORIGIN;
  protected readonly items = this.store.selectSignal(selectCartItems);
  protected readonly totals = this.store.selectSignal(selectCartTotalsByCurrency);

  updateQuantity(productId: string, quantity: number): void {
    this.store.dispatch(CartActions.updateQuantity({ productId, quantity }));
  }

  remove(productId: string): void {
    this.store.dispatch(CartActions.removeItem({ productId }));
  }
}
