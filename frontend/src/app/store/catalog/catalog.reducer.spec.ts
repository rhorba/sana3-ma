import { CatalogActions } from './catalog.actions';
import { catalogReducer } from './catalog.reducer';
import { CatalogState, initialCatalogState } from './catalog.state';

describe('catalogReducer', () => {
  const product = {
    id: 'product-1',
    artisanProfileId: 'profile-1',
    name: 'Zellige Tile Set',
    description: 'Handmade blue zellige',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
    imageUrl: null,
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-02T00:00:00Z',
  };

  it('returns the initial state for an unknown action', () => {
    expect(catalogReducer(undefined, { type: '@@INIT' })).toEqual(initialCatalogState);
  });

  it('sets loading and clears error on loadProducts', () => {
    const withError: CatalogState = { ...initialCatalogState, error: 'previous error' };
    const state = catalogReducer(withError, CatalogActions.loadProducts());
    expect(state.loading).toBe(true);
    expect(state.error).toBeNull();
  });

  it('populates products and marks loaded on loadProductsSuccess', () => {
    const state = catalogReducer(initialCatalogState, CatalogActions.loadProductsSuccess({ products: [product] }));
    expect(state.products).toEqual([product]);
    expect(state.loading).toBe(false);
    expect(state.loaded).toBe(true);
  });

  it('sets an error on loadProductsFailure', () => {
    const loading: CatalogState = { ...initialCatalogState, loading: true };
    const state = catalogReducer(loading, CatalogActions.loadProductsFailure({ message: 'network error' }));
    expect(state.loading).toBe(false);
    expect(state.error).toBe('network error');
  });

  it('appends the new product on createProductSuccess', () => {
    const withOne: CatalogState = { ...initialCatalogState, products: [product], saving: true };
    const secondProduct = { ...product, id: 'product-2' };
    const state = catalogReducer(withOne, CatalogActions.createProductSuccess({ product: secondProduct }));
    expect(state.products).toEqual([product, secondProduct]);
    expect(state.saving).toBe(false);
  });

  it('replaces the matching product on updateProductSuccess', () => {
    const withOne: CatalogState = { ...initialCatalogState, products: [product], saving: true };
    const updated = { ...product, name: 'Renamed' };
    const state = catalogReducer(withOne, CatalogActions.updateProductSuccess({ product: updated }));
    expect(state.products).toEqual([updated]);
    expect(state.saving).toBe(false);
  });

  it('replaces the matching product on uploadProductImageSuccess', () => {
    const withOne: CatalogState = { ...initialCatalogState, products: [product] };
    const updated = { ...product, imageUrl: '/api/v1/products/images/x.jpg' };
    const state = catalogReducer(withOne, CatalogActions.uploadProductImageSuccess({ product: updated }));
    expect(state.products).toEqual([updated]);
  });

  it('removes the product on deleteProductSuccess', () => {
    const withOne: CatalogState = { ...initialCatalogState, products: [product] };
    const state = catalogReducer(withOne, CatalogActions.deleteProductSuccess({ id: product.id }));
    expect(state.products).toEqual([]);
  });

  it('sets an error and clears saving on createProductFailure/updateProductFailure', () => {
    const saving: CatalogState = { ...initialCatalogState, saving: true };
    const state = catalogReducer(saving, CatalogActions.createProductFailure({ message: "Couldn't create" }));
    expect(state.saving).toBe(false);
    expect(state.error).toBe("Couldn't create");
  });
});
