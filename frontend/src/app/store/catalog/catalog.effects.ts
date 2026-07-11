import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

import { CatalogService } from '../../core/catalog/catalog.service';
import { extractErrorMessage } from '../../core/http-error.util';
import { CatalogActions } from './catalog.actions';

@Injectable()
export class CatalogEffects {
  private readonly actions$ = inject(Actions);
  private readonly catalogService = inject(CatalogService);

  loadProducts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.loadProducts),
      switchMap(() =>
        this.catalogService.listMyProducts().pipe(
          map((products) => CatalogActions.loadProductsSuccess({ products })),
          catchError((error) =>
            of(
              CatalogActions.loadProductsFailure({
                message: extractErrorMessage(error, "Couldn't load your products. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  createProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.createProduct),
      switchMap(({ name, description, priceAmount, priceCurrency, craftType }) =>
        this.catalogService.createProduct({ name, description, priceAmount, priceCurrency, craftType }).pipe(
          map((product) => CatalogActions.createProductSuccess({ product })),
          catchError((error) =>
            of(
              CatalogActions.createProductFailure({
                message: extractErrorMessage(error, "Couldn't create the product. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  updateProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.updateProduct),
      switchMap(({ id, name, description, priceAmount, priceCurrency, craftType }) =>
        this.catalogService.updateProduct(id, { name, description, priceAmount, priceCurrency, craftType }).pipe(
          map((product) => CatalogActions.updateProductSuccess({ product })),
          catchError((error) =>
            of(
              CatalogActions.updateProductFailure({
                message: extractErrorMessage(error, "Couldn't save the product. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  deleteProduct$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.deleteProduct),
      switchMap(({ id }) =>
        this.catalogService.deleteProduct(id).pipe(
          map(() => CatalogActions.deleteProductSuccess({ id })),
          catchError((error) =>
            of(
              CatalogActions.deleteProductFailure({
                message: extractErrorMessage(error, "Couldn't delete the product. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  uploadProductImage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CatalogActions.uploadProductImage),
      switchMap(({ id, file }) =>
        this.catalogService.uploadImage(id, file).pipe(
          map((product) => CatalogActions.uploadProductImageSuccess({ product })),
          catchError((error) =>
            of(
              CatalogActions.uploadProductImageFailure({
                message: extractErrorMessage(error, "Couldn't upload the image. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
