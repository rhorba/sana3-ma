import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Action } from '@ngrx/store';
import { Observable, firstValueFrom, of, throwError } from 'rxjs';

import { CatalogService } from '../../core/catalog/catalog.service';
import { CatalogActions } from './catalog.actions';
import { CatalogEffects } from './catalog.effects';

describe('CatalogEffects', () => {
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

  const upsertRequest = {
    name: 'Zellige Tile Set',
    description: 'Handmade blue zellige',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
  };

  const publicProduct = {
    id: 'product-1',
    name: 'Zellige Tile Set',
    description: 'Handmade blue zellige',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
    imageUrl: null,
    artisan: { displayName: 'Fatima Zahra', craftType: 'Pottery', region: 'Fes' },
  };

  let actions$: Observable<Action>;
  let catalogService: {
    listMyProducts: ReturnType<typeof vi.fn>;
    createProduct: ReturnType<typeof vi.fn>;
    updateProduct: ReturnType<typeof vi.fn>;
    deleteProduct: ReturnType<typeof vi.fn>;
    uploadImage: ReturnType<typeof vi.fn>;
    searchProducts: ReturnType<typeof vi.fn>;
    getProductDetail: ReturnType<typeof vi.fn>;
  };
  let effects: CatalogEffects;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [
        CatalogEffects,
        provideMockActions(() => actions$),
        { provide: CatalogService, useValue: catalogService },
      ],
    });
    effects = TestBed.inject(CatalogEffects);
  }

  beforeEach(() => {
    catalogService = {
      listMyProducts: vi.fn(),
      createProduct: vi.fn(),
      updateProduct: vi.fn(),
      deleteProduct: vi.fn(),
      uploadImage: vi.fn(),
      searchProducts: vi.fn(),
      getProductDetail: vi.fn(),
    };
  });

  it('loadProducts$ maps a successful fetch to loadProductsSuccess', async () => {
    catalogService.listMyProducts.mockReturnValue(of([product]));
    actions$ = of(CatalogActions.loadProducts());
    setup();

    const result = await firstValueFrom(effects.loadProducts$);

    expect(result).toEqual(CatalogActions.loadProductsSuccess({ products: [product] }));
  });

  it('loadProducts$ maps a failure to loadProductsFailure', async () => {
    catalogService.listMyProducts.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(CatalogActions.loadProducts());
    setup();

    const result = await firstValueFrom(effects.loadProducts$);

    expect(result).toEqual(
      CatalogActions.loadProductsFailure({ message: "Couldn't load your products. Please try again." }),
    );
  });

  it('createProduct$ maps a successful create to createProductSuccess', async () => {
    catalogService.createProduct.mockReturnValue(of(product));
    actions$ = of(CatalogActions.createProduct(upsertRequest));
    setup();

    const result = await firstValueFrom(effects.createProduct$);

    expect(catalogService.createProduct).toHaveBeenCalledWith(upsertRequest);
    expect(result).toEqual(CatalogActions.createProductSuccess({ product }));
  });

  it('updateProduct$ maps a successful update to updateProductSuccess', async () => {
    catalogService.updateProduct.mockReturnValue(of(product));
    actions$ = of(CatalogActions.updateProduct({ id: product.id, ...upsertRequest }));
    setup();

    const result = await firstValueFrom(effects.updateProduct$);

    expect(catalogService.updateProduct).toHaveBeenCalledWith(product.id, upsertRequest);
    expect(result).toEqual(CatalogActions.updateProductSuccess({ product }));
  });

  it('deleteProduct$ maps a successful delete to deleteProductSuccess', async () => {
    catalogService.deleteProduct.mockReturnValue(of(undefined));
    actions$ = of(CatalogActions.deleteProduct({ id: product.id }));
    setup();

    const result = await firstValueFrom(effects.deleteProduct$);

    expect(result).toEqual(CatalogActions.deleteProductSuccess({ id: product.id }));
  });

  it('uploadProductImage$ maps a successful upload to uploadProductImageSuccess', async () => {
    const file = new File(['bytes'], 'photo.jpg', { type: 'image/jpeg' });
    catalogService.uploadImage.mockReturnValue(of(product));
    actions$ = of(CatalogActions.uploadProductImage({ id: product.id, file }));
    setup();

    const result = await firstValueFrom(effects.uploadProductImage$);

    expect(catalogService.uploadImage).toHaveBeenCalledWith(product.id, file);
    expect(result).toEqual(CatalogActions.uploadProductImageSuccess({ product }));
  });

  it('uploadProductImage$ maps a failure to uploadProductImageFailure', async () => {
    catalogService.uploadImage.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 400,
            error: { error: { code: 'UNSUPPORTED_IMAGE_TYPE', message: 'Unsupported image type', details: [] } },
          }),
      ),
    );
    const file = new File(['bytes'], 'doc.pdf', { type: 'application/pdf' });
    actions$ = of(CatalogActions.uploadProductImage({ id: product.id, file }));
    setup();

    const result = await firstValueFrom(effects.uploadProductImage$);

    expect(result).toEqual(CatalogActions.uploadProductImageFailure({ message: 'Unsupported image type' }));
  });

  it('searchProducts$ maps a successful search to searchProductsSuccess', async () => {
    const response = { products: [publicProduct], totalElements: 1, page: 0, pageSize: 20 };
    catalogService.searchProducts.mockReturnValue(of(response));
    actions$ = of(CatalogActions.searchProducts({ craftType: 'Pottery' }));
    setup();

    const result = await firstValueFrom(effects.searchProducts$);

    expect(catalogService.searchProducts).toHaveBeenCalledWith({ craftType: 'Pottery' });
    expect(result).toEqual(CatalogActions.searchProductsSuccess({ response }));
  });

  it('searchProducts$ maps a failure to searchProductsFailure', async () => {
    catalogService.searchProducts.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(CatalogActions.searchProducts({}));
    setup();

    const result = await firstValueFrom(effects.searchProducts$);

    expect(result).toEqual(
      CatalogActions.searchProductsFailure({ message: "Couldn't load products. Please try again." }),
    );
  });

  it('loadProductDetail$ maps a successful fetch to loadProductDetailSuccess', async () => {
    catalogService.getProductDetail.mockReturnValue(of(publicProduct));
    actions$ = of(CatalogActions.loadProductDetail({ id: publicProduct.id }));
    setup();

    const result = await firstValueFrom(effects.loadProductDetail$);

    expect(catalogService.getProductDetail).toHaveBeenCalledWith(publicProduct.id);
    expect(result).toEqual(CatalogActions.loadProductDetailSuccess({ product: publicProduct }));
  });

  it('loadProductDetail$ maps a 404 to loadProductDetailNotFound (empty state, not an error)', async () => {
    catalogService.getProductDetail.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );
    actions$ = of(CatalogActions.loadProductDetail({ id: 'missing' }));
    setup();

    const result = await firstValueFrom(effects.loadProductDetail$);

    expect(result).toEqual(CatalogActions.loadProductDetailNotFound());
  });

  it('loadProductDetail$ maps other failures to loadProductDetailFailure', async () => {
    catalogService.getProductDetail.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 500 })),
    );
    actions$ = of(CatalogActions.loadProductDetail({ id: publicProduct.id }));
    setup();

    const result = await firstValueFrom(effects.loadProductDetail$);

    expect(result).toEqual(
      CatalogActions.loadProductDetailFailure({ message: "Couldn't load this product. Please try again." }),
    );
  });
});
