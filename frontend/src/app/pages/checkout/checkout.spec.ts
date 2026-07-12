import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { selectCartItems, selectCartTotalsByCurrency } from '../../store/cart/cart.selectors';
import { CartItem } from '../../store/cart/cart.state';
import { OrderActions } from '../../store/order/order.actions';
import { selectOrderError, selectOrderPlacing, selectPlacedOrder } from '../../store/order/order.selectors';
import { Checkout } from './checkout';

describe('Checkout', () => {
  let component: Checkout;
  let fixture: ComponentFixture<Checkout>;
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

  const order = {
    id: 'order-1',
    buyerUserId: 'buyer-1',
    status: 'PLACED' as const,
    shippingAddress: '123 Rue Example, Fes',
    items: [],
    totals: [{ currency: 'MAD', amount: 900 }],
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-01T00:00:00Z',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Checkout],
      providers: [
        provideNoopAnimations(),
        provideRouter([]),
        provideMockStore({
          selectors: [
            { selector: selectCartItems, value: [] },
            { selector: selectCartTotalsByCurrency, value: [] },
            { selector: selectOrderPlacing, value: false },
            { selector: selectPlacedOrder, value: null },
            { selector: selectOrderError, value: null },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(Checkout);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('dispatches resetPlaceOrderState on construction', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(dispatchSpy).toHaveBeenCalledWith(OrderActions.resetPlaceOrderState());
  });

  it('shows the empty-state prompt when the cart is empty', () => {
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).not.toBeNull();
  });

  it('renders a review row per cart item with the totals', () => {
    store.overrideSelector(selectCartItems, [item]);
    store.overrideSelector(selectCartTotalsByCurrency, [{ currency: 'MAD', amount: 900 }]);
    createComponent();

    const rows = fixture.nativeElement.querySelectorAll('.review-list li');
    expect(rows.length).toBe(1);
    expect(rows[0].textContent).toContain('Zellige Tile Set');
    expect(fixture.nativeElement.querySelector('.totals').textContent).toContain('900');
  });

  it('does not dispatch placeOrder when the shipping address is blank', () => {
    store.overrideSelector(selectCartItems, [item]);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.placeOrder();

    expect(dispatchSpy).not.toHaveBeenCalledWith(expect.objectContaining({ type: 'Order/Place Order' }));
  });

  it('dispatches placeOrder with cart items mapped to productId/quantity', () => {
    store.overrideSelector(selectCartItems, [item]);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.form.setValue({ shippingAddress: '123 Rue Example, Fes' });
    component.placeOrder();

    expect(dispatchSpy).toHaveBeenCalledWith(
      OrderActions.placeOrder({
        shippingAddress: '123 Rue Example, Fes',
        items: [{ productId: 'product-1', quantity: 2 }],
      }),
    );
  });

  it('shows a confirmation with the order id once placed', () => {
    store.overrideSelector(selectPlacedOrder, order);
    createComponent();

    expect(fixture.nativeElement.querySelector('.confirmation').textContent).toContain('order-1');
  });

  it('shows the error message from a failed placement', () => {
    store.overrideSelector(selectCartItems, [item]);
    store.overrideSelector(selectOrderError, 'No product found for id product-1');
    createComponent();

    expect(fixture.nativeElement.querySelector('.error-message').textContent).toContain(
      'No product found for id product-1',
    );
  });
});
