import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { API_ORIGIN } from '../../core/api-origin';
import { PublicProductResponse } from '../../core/catalog/catalog.models';
import { CartActions } from '../../store/cart/cart.actions';
import { CatalogActions } from '../../store/catalog/catalog.actions';
import {
  selectBrowseError,
  selectBrowseLoading,
  selectBrowsePage,
  selectBrowsePageSize,
  selectBrowseResults,
  selectBrowseTotalElements,
} from '../../store/catalog/catalog.selectors';

@Component({
  selector: 'app-browse',
  imports: [ReactiveFormsModule, RouterLink, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './browse.html',
  styleUrl: './browse.scss',
})
export class Browse {
  private readonly formBuilder = inject(FormBuilder);
  private readonly store = inject(Store);

  protected readonly apiOrigin = API_ORIGIN;
  protected readonly results = this.store.selectSignal(selectBrowseResults);
  protected readonly totalElements = this.store.selectSignal(selectBrowseTotalElements);
  protected readonly page = this.store.selectSignal(selectBrowsePage);
  protected readonly pageSize = this.store.selectSignal(selectBrowsePageSize);
  protected readonly loading = this.store.selectSignal(selectBrowseLoading);
  protected readonly error = this.store.selectSignal(selectBrowseError);

  readonly filterForm = this.formBuilder.nonNullable.group({
    craftType: [''],
    region: [''],
    minPrice: [''],
    maxPrice: [''],
    q: [''],
  });

  constructor() {
    this.search(0);
  }

  submit(): void {
    this.search(0);
  }

  nextPage(): void {
    this.search(this.page() + 1);
  }

  previousPage(): void {
    this.search(Math.max(0, this.page() - 1));
  }

  hasNextPage(): boolean {
    return (this.page() + 1) * this.pageSize() < this.totalElements();
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
        quantity: 1,
      }),
    );
  }

  private search(page: number): void {
    const { craftType, region, minPrice, maxPrice, q } = this.filterForm.getRawValue();
    this.store.dispatch(
      CatalogActions.searchProducts({
        craftType: craftType || undefined,
        region: region || undefined,
        minPrice: minPrice ? Number(minPrice) : undefined,
        maxPrice: maxPrice ? Number(maxPrice) : undefined,
        q: q || undefined,
        page,
      }),
    );
  }
}
