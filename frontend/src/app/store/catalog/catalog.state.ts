import { ProductResponse } from '../../core/catalog/catalog.models';

export interface CatalogState {
  products: ProductResponse[];
  loading: boolean;
  loaded: boolean;
  saving: boolean;
  error: string | null;
}

export const initialCatalogState: CatalogState = {
  products: [],
  loading: false,
  loaded: false,
  saving: false,
  error: null,
};
