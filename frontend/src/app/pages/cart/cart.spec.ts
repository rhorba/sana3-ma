import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { CartActions } from '../../store/cart/cart.actions';
import { selectCartItems, selectCartTotalsByCurrency } from '../../store/cart/cart.selectors';
import { CartItem } from '../../store/cart/cart.state';
import { Cart } from './cart';

describe('Cart', () => {
  let component: Cart;
  let fixture: ComponentFixture<Cart>;
  let store: MockStore;

  const item: CartItem = {
    productId: 'product-1',
    productName: 'Zellige Tile Set',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
    imageUrl: null,
    quantity: 2,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cart],
      providers: [
        provideNoopAnimations(),
        provideRouter([]),
        provideMockStore({
          selectors: [
            { selector: selectCartItems, value: [] },
            { selector: selectCartTotalsByCurrency, value: [] },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(Cart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('shows the empty-state prompt when the cart is empty', () => {
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).not.toBeNull();
  });

  it('renders a row per cart item with its line total', () => {
    store.overrideSelector(selectCartItems, [item]);
    store.overrideSelector(selectCartTotalsByCurrency, [{ currency: 'MAD', amount: 900 }]);
    createComponent();

    const rows = fixture.nativeElement.querySelectorAll('.cart-item');
    expect(rows.length).toBe(1);
    expect(rows[0].textContent).toContain('Zellige Tile Set');
    expect(fixture.nativeElement.querySelector('.cart-totals').textContent).toContain('900');
  });

  it('shows a link to checkout when the cart has items', () => {
    store.overrideSelector(selectCartItems, [item]);
    createComponent();

    expect(fixture.nativeElement.querySelector('.checkout-link')).not.toBeNull();
  });

  it('does not show a link to checkout when the cart is empty', () => {
    createComponent();

    expect(fixture.nativeElement.querySelector('.checkout-link')).toBeNull();
  });

  it('dispatches removeItem when Remove is clicked', () => {
    store.overrideSelector(selectCartItems, [item]);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    fixture.nativeElement.querySelector('.cart-item button').click();

    expect(dispatchSpy).toHaveBeenCalledWith(CartActions.removeItem({ productId: 'product-1' }));
  });

  it('dispatches updateQuantity when the quantity input changes', () => {
    store.overrideSelector(selectCartItems, [item]);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    const input: HTMLInputElement = fixture.nativeElement.querySelector('.cart-item input');
    input.value = '5';
    input.dispatchEvent(new Event('change'));

    expect(dispatchSpy).toHaveBeenCalledWith(
      CartActions.updateQuantity({ productId: 'product-1', quantity: 5 }),
    );
  });
});
