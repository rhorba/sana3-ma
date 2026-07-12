import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { PublicProductResponse } from '../../core/catalog/catalog.models';
import { CartActions } from '../../store/cart/cart.actions';
import { CatalogActions } from '../../store/catalog/catalog.actions';
import {
  selectBrowseError,
  selectBrowseLoading,
  selectBrowsePage,
  selectBrowsePageSize,
  selectBrowseResults,
  selectBrowseTotalElements,
} from '../../store/catalog/catalog.selectors';
import { Browse } from './browse';

describe('Browse', () => {
  let component: Browse;
  let fixture: ComponentFixture<Browse>;
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Browse],
      providers: [
        provideNoopAnimations(),
        provideRouter([]),
        provideMockStore({
          selectors: [
            { selector: selectBrowseResults, value: [] },
            { selector: selectBrowseTotalElements, value: 0 },
            { selector: selectBrowsePage, value: 0 },
            { selector: selectBrowsePageSize, value: 20 },
            { selector: selectBrowseLoading, value: false },
            { selector: selectBrowseError, value: null },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(Browse);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and dispatch searchProducts on construction', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(
      CatalogActions.searchProducts({
        craftType: undefined,
        region: undefined,
        minPrice: undefined,
        maxPrice: undefined,
        q: undefined,
        page: 0,
      }),
    );
  });

  it('shows the empty-state prompt when no results', () => {
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).not.toBeNull();
  });

  it('renders a card per result with the artisan summary', () => {
    store.overrideSelector(selectBrowseResults, [product]);
    createComponent();

    const cards = fixture.nativeElement.querySelectorAll('.product-card');
    expect(cards.length).toBe(1);
    expect(cards[0].textContent).toContain('Zellige Tile Set');
    expect(cards[0].textContent).toContain('Fatima Zahra');
  });

  it('dispatches searchProducts with filter values on submit', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.filterForm.setValue({ craftType: 'Pottery', region: 'Fes', minPrice: '100', maxPrice: '500', q: 'tile' });
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(dispatchSpy).toHaveBeenCalledWith(
      CatalogActions.searchProducts({
        craftType: 'Pottery',
        region: 'Fes',
        minPrice: 100,
        maxPrice: 500,
        q: 'tile',
        page: 0,
      }),
    );
  });

  it('nextPage dispatches searchProducts for the next page when more results exist', () => {
    store.overrideSelector(selectBrowseTotalElements, 25);
    store.overrideSelector(selectBrowsePage, 0);
    store.overrideSelector(selectBrowsePageSize, 20);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    expect(component.hasNextPage()).toBe(true);
    component.nextPage();

    expect(dispatchSpy).toHaveBeenCalledWith(
      expect.objectContaining({ page: 1 }),
    );
  });

  it('dispatches addItem when Add to Cart is clicked', () => {
    store.overrideSelector(selectBrowseResults, [product]);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    fixture.nativeElement.querySelector('.product-card button').click();

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
        quantity: 1,
      }),
    );
  });
});
