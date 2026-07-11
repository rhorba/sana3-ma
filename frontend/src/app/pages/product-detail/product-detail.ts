import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { API_ORIGIN } from '../../core/api-origin';
import { CatalogActions } from '../../store/catalog/catalog.actions';
import {
  selectProductDetail,
  selectProductDetailError,
  selectProductDetailLoading,
  selectProductDetailNotFound,
} from '../../store/catalog/catalog.selectors';

@Component({
  selector: 'app-product-detail',
  imports: [RouterLink],
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

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.store.dispatch(CatalogActions.loadProductDetail({ id }));
    }
  }
}
