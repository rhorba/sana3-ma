import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { API_ORIGIN } from '../../core/api-origin';
import { PublicProductResponse } from '../../core/catalog/catalog.models';
import { CartActions } from '../../store/cart/cart.actions';
import { CatalogActions } from '../../store/catalog/catalog.actions';
import {
  selectProductDetail,
  selectProductDetailError,
  selectProductDetailLoading,
  selectProductDetailNotFound,
} from '../../store/catalog/catalog.selectors';

@Component({
  selector: 'app-product-detail',
  imports: [RouterLink, FormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.scss',
})
export class ProductDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(Store);

  protected readonly apiOrigin = API_ORIGIN;
  protected readonly product = this.store.selectSignal(selectProductDetail);
  protected readonly loading = this.store.selectSignal(selectProductDetailLoading);
  protected readonly notFound = this.store.selectSignal(selectProductDetailNotFound);
  protected readonly error = this.store.selectSignal(selectProductDetailError);
  readonly quantity = signal(1);

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.store.dispatch(CatalogActions.loadProductDetail({ id }));
    }
  }

  addToCart(product: PublicProductResponse): void {
    this.store.dispatch(
      CartActions.addItem({
        item: {
          productId: product.id,
          productName: product.name,
          priceAmount: product.priceAmount,
          priceCurrency: product.priceCurrency,
          craftType: product.craftType,
          imageUrl: product.imageUrl,
        },
        quantity: Math.max(1, this.quantity()),
      }),
    );
  }
}
