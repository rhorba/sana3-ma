import { createFeature, createReducer, on } from '@ngrx/store';

import { CatalogActions } from './catalog.actions';
import { CatalogState, initialCatalogState } from './catalog.state';

export const catalogFeature = createFeature({
  name: 'catalog',
  reducer: createReducer(
    initialCatalogState,
    on(CatalogActions.loadProducts, (state): CatalogState => ({
      ...state,
      loading: true,
      error: null,
    })),
    on(CatalogActions.loadProductsSuccess, (state, { products }): CatalogState => ({
      ...state,
      products,
      loading: false,
      loaded: true,
      error: null,
    })),
    on(CatalogActions.loadProductsFailure, (state, { message }): CatalogState => ({
      ...state,
      loading: false,
      error: message,
    })),
    on(CatalogActions.createProduct, CatalogActions.updateProduct, (state): CatalogState => ({
      ...state,
      saving: true,
      error: null,
    })),
    on(CatalogActions.createProductSuccess, (state, { product }): CatalogState => ({
      ...state,
      products: [...state.products, product],
      saving: false,
      error: null,
    })),
    on(
      CatalogActions.updateProductSuccess,
      CatalogActions.uploadProductImageSuccess,
      (state, { product }): CatalogState => ({
        ...state,
        products: state.products.map((existing) => (existing.id === product.id ? product : existing)),
        saving: false,
        error: null,
      }),
    ),
    on(
      CatalogActions.createProductFailure,
      CatalogActions.updateProductFailure,
      (state, { message }): CatalogState => ({
        ...state,
        saving: false,
        error: message,
      }),
    ),
    on(CatalogActions.deleteProductSuccess, (state, { id }): CatalogState => ({
      ...state,
      products: state.products.filter((product) => product.id !== id),
    })),
    on(CatalogActions.deleteProductFailure, (state, { message }): CatalogState => ({
      ...state,
      error: message,
    })),
    on(CatalogActions.uploadProductImageFailure, (state, { message }): CatalogState => ({
      ...state,
      error: message,
    })),

    on(CatalogActions.searchProducts, (state): CatalogState => ({
      ...state,
      browseLoading: true,
      browseError: null,
    })),
    on(CatalogActions.searchProductsSuccess, (state, { response }): CatalogState => ({
      ...state,
      browseResults: response.products,
      browseTotalElements: response.totalElements,
      browsePage: response.page,
      browsePageSize: response.pageSize,
      browseLoading: false,
      browseError: null,
    })),
    on(CatalogActions.searchProductsFailure, (state, { message }): CatalogState => ({
      ...state,
      browseLoading: false,
      browseError: message,
    })),

    on(CatalogActions.loadProductDetail, (state): CatalogState => ({
      ...state,
      productDetail: null,
      productDetailLoading: true,
      productDetailNotFound: false,
      productDetailError: null,
    })),
    on(CatalogActions.loadProductDetailSuccess, (state, { product }): CatalogState => ({
      ...state,
      productDetail: product,
      productDetailLoading: false,
      productDetailNotFound: false,
      productDetailError: null,
    })),
    on(CatalogActions.loadProductDetailNotFound, (state): CatalogState => ({
      ...state,
      productDetailLoading: false,
      productDetailNotFound: true,
      productDetailError: null,
    })),
    on(CatalogActions.loadProductDetailFailure, (state, { message }): CatalogState => ({
      ...state,
      productDetailLoading: false,
      productDetailError: message,
    })),
  ),
});

export const {
  name: catalogFeatureKey,
  reducer: catalogReducer,
  selectProducts,
  selectLoading,
  selectLoaded,
  selectSaving,
  selectError,
  selectBrowseResults,
  selectBrowseTotalElements,
  selectBrowsePage,
  selectBrowsePageSize,
  selectBrowseLoading,
  selectBrowseError,
  selectProductDetail,
  selectProductDetailLoading,
  selectProductDetailNotFound,
  selectProductDetailError,
} = catalogFeature;
