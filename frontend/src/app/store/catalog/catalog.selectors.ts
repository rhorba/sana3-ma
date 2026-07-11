import { catalogFeature } from './catalog.reducer';

export const {
  selectProducts,
  selectLoading: selectCatalogLoading,
  selectLoaded: selectCatalogLoaded,
  selectSaving: selectCatalogSaving,
  selectError: selectCatalogError,
} = catalogFeature;
