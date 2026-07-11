import { createActionGroup, emptyProps, props } from '@ngrx/store';

import {
  ProductResponse,
  ProductSearchFilters,
  PublicProductPageResponse,
  PublicProductResponse,
  UpsertProductRequest,
} from '../../core/catalog/catalog.models';

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

    'Search Products': props<ProductSearchFilters>(),
    'Search Products Success': props<{ response: PublicProductPageResponse }>(),
    'Search Products Failure': props<{ message: string }>(),

    'Load Product Detail': props<{ id: string }>(),
    'Load Product Detail Success': props<{ product: PublicProductResponse }>(),
    // A bad/deleted id is a normal not-found state (Story 4.5), not an error.
    'Load Product Detail Not Found': emptyProps(),
    'Load Product Detail Failure': props<{ message: string }>(),
  },
});
