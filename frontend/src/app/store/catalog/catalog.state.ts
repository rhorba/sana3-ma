import { ProductResponse, PublicProductResponse } from '../../core/catalog/catalog.models';

export interface CatalogState {
  products: ProductResponse[];
  loading: boolean;
  loaded: boolean;
  saving: boolean;
  error: string | null;

  // Public browsing (Story 4.4)
  browseResults: PublicProductResponse[];
  browseTotalElements: number;
  browsePage: number;
  browsePageSize: number;
  browseLoading: boolean;
  browseError: string | null;

  // Public product detail (Story 4.5)
  productDetail: PublicProductResponse | null;
  productDetailLoading: boolean;
  productDetailNotFound: boolean;
  productDetailError: string | null;
}

export const initialCatalogState: CatalogState = {
  products: [],
  loading: false,
  loaded: false,
  saving: false,
  error: null,

  browseResults: [],
  browseTotalElements: 0,
  browsePage: 0,
  browsePageSize: 20,
  browseLoading: false,
  browseError: null,

  productDetail: null,
  productDetailLoading: false,
  productDetailNotFound: false,
  productDetailError: null,
};
