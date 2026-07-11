import { catalogFeature } from './catalog.reducer';

export const {
  selectProducts,
  selectLoading: selectCatalogLoading,
  selectLoaded: selectCatalogLoaded,
  selectSaving: selectCatalogSaving,
  selectError: selectCatalogError,
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
