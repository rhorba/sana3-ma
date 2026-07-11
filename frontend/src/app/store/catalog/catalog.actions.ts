import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { ProductResponse, UpsertProductRequest } from '../../core/catalog/catalog.models';

export const CatalogActions = createActionGroup({
  source: 'Catalog',
  events: {
    'Load Products': emptyProps(),
    'Load Products Success': props<{ products: ProductResponse[] }>(),
    'Load Products Failure': props<{ message: string }>(),

    'Create Product': props<UpsertProductRequest>(),
    'Create Product Success': props<{ product: ProductResponse }>(),
    'Create Product Failure': props<{ message: string }>(),

    'Update Product': props<{ id: string } & UpsertProductRequest>(),
    'Update Product Success': props<{ product: ProductResponse }>(),
    'Update Product Failure': props<{ message: string }>(),

    'Delete Product': props<{ id: string }>(),
    'Delete Product Success': props<{ id: string }>(),
    'Delete Product Failure': props<{ message: string }>(),

    'Upload Product Image': props<{ id: string; file: File }>(),
    'Upload Product Image Success': props<{ product: ProductResponse }>(),
    'Upload Product Image Failure': props<{ message: string }>(),
  },
});
