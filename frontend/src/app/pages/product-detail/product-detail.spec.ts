import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { PublicProductResponse } from '../../core/catalog/catalog.models';
import { CartActions } from '../../store/cart/cart.actions';
import { CatalogActions } from '../../store/catalog/catalog.actions';
import {
  selectProductDetail,
  selectProductDetailError,
  selectProductDetailLoading,
  selectProductDetailNotFound,
} from '../../store/catalog/catalog.selectors';
import { ProductDetail } from './product-detail';

describe('ProductDetail', () => {
  let component: ProductDetail;
  let fixture: ComponentFixture<ProductDetail>;
  let store: MockStore;

  const product: PublicProductResponse = {
    id: 'product-1',
    name: 'Zellige Tile Set',
    description: 'Handmade blue zellige',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
    imageUrl: null,
    artisan: { displayName: 'Fatima Zahra', craftType: 'Pottery', region: 'Fes' },
  };

  async function configure(id: string): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [ProductDetail],
      providers: [
        provideRouter([]),
        provideMockStore({
          selectors: [
            { selector: selectProductDetail, value: null },
            { selector: selectProductDetailLoading, value: false },
            { selector: selectProductDetailNotFound, value: false },
            { selector: selectProductDetailError, value: null },
          ],
        }),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id }) } },
        },
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  }

  function createComponent(): void {
    fixture = TestBed.createComponent(ProductDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and dispatch loadProductDetail with the route id', async () => {
    await configure('product-1');
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(CatalogActions.loadProductDetail({ id: 'product-1' }));
  });

  it('shows a loading message while loading', async () => {
    await configure('product-1');
    store.overrideSelector(selectProductDetailLoading, true);
    createComponent();

    expect(fixture.nativeElement.textContent).toContain('Loading product');
  });

  it('shows the not-found prompt when the product does not exist', async () => {
    await configure('missing');
    store.overrideSelector(selectProductDetailNotFound, true);
    createComponent();

    expect(fixture.nativeElement.querySelector('.not-found-prompt')).not.toBeNull();
  });

  it('renders the product and artisan summary when found', async () => {
    await configure('product-1');
    store.overrideSelector(selectProductDetail, product);
    createComponent();

    expect(fixture.nativeElement.textContent).toContain('Zellige Tile Set');
    expect(fixture.nativeElement.textContent).toContain('Sold by Fatima Zahra');
    expect(fixture.nativeElement.textContent).toContain('Fes');
  });

  it('dispatches addItem with the selected quantity when Add to Cart is clicked', async () => {
    await configure('product-1');
    store.overrideSelector(selectProductDetail, product);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.quantity.set(3);
    fixture.nativeElement.querySelector('.add-to-cart button').click();

    expect(dispatchSpy).toHaveBeenCalledWith(
      CartActions.addItem({
        item: {
          productId: 'product-1',
          productName: 'Zellige Tile Set',
          priceAmount: 450,
          priceCurrency: 'MAD',
          craftType: 'Pottery',
          imageUrl: null,
        },
        quantity: 3,
      }),
    );
  });
});
