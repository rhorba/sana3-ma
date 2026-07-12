import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Action } from '@ngrx/store';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Observable, Subject } from 'rxjs';

import { OrderResponse } from '../../core/order/order.models';
import { OrderActions } from '../../store/order/order.actions';
import { selectMyOrders, selectMyOrdersError, selectMyOrdersLoaded, selectMyOrdersLoading } from '../../store/order/order.selectors';
import { Orders } from './orders';

describe('Orders', () => {
  let component: Orders;
  let fixture: ComponentFixture<Orders>;
  let store: MockStore;
  let actions$: Subject<Action>;

  const order: OrderResponse = {
    id: 'order-1',
    buyerUserId: 'buyer-1',
    status: 'PLACED',
    shippingAddress: '123 Rue Example, Fes',
    items: [
      {
        id: 'item-1',
        productId: 'product-1',
        productName: 'Zellige Tile Set',
        priceAmount: 450,
        priceCurrency: 'MAD',
        craftType: 'Pottery',
        artisanProfileId: 'profile-1',
        quantity: 2,
        lineTotal: 900,
        completed: false,
        completedAt: null,
      },
    ],
    totals: [{ currency: 'MAD', amount: 900 }],
    createdAt: '2026-01-01T00:00:00Z',
    updatedAt: '2026-01-01T00:00:00Z',
  };

  beforeEach(async () => {
    actions$ = new Subject<Action>();

    await TestBed.configureTestingModule({
      imports: [Orders],
      providers: [
        provideNoopAnimations(),
        provideMockActions((): Observable<Action> => actions$),
        provideMockStore({
          selectors: [
            { selector: selectMyOrders, value: [] },
            { selector: selectMyOrdersLoading, value: false },
            { selector: selectMyOrdersLoaded, value: false },
            { selector: selectMyOrdersError, value: null },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(Orders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and dispatch listMyOrders on construction', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(OrderActions.listMyOrders());
  });

  it('shows the empty-state prompt when loaded with no orders', () => {
    store.overrideSelector(selectMyOrdersLoaded, true);
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).not.toBeNull();
  });

  it('renders a card per order with its items and total, and a cancel action for PLACED orders', () => {
    store.overrideSelector(selectMyOrders, [order]);
    createComponent();

    const cards = fixture.nativeElement.querySelectorAll('.order-card');
    expect(cards.length).toBe(1);
    expect(cards[0].textContent).toContain('Zellige Tile Set');
    expect(cards[0].textContent).toContain('900');
    expect(cards[0].querySelector('button')).not.toBeNull();
  });

  it('does not show a cancel action for a cancelled order', () => {
    store.overrideSelector(selectMyOrders, [{ ...order, status: 'CANCELLED' as const }]);
    createComponent();

    expect(fixture.nativeElement.querySelector('.order-card button')).toBeNull();
  });

  it('cancel dispatches cancelOrder after confirmation', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    vi.spyOn(window, 'confirm').mockReturnValue(true);

    component.cancel(order);

    expect(dispatchSpy).toHaveBeenCalledWith(OrderActions.cancelOrder({ id: order.id }));
  });

  it('cancel does not dispatch when the user cancels the confirmation', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    vi.spyOn(window, 'confirm').mockReturnValue(false);

    component.cancel(order);

    expect(dispatchSpy).not.toHaveBeenCalled();
  });

  it('shows a snackbar with the backend message when cancelOrderFailure fires', () => {
    createComponent();

    actions$.next(OrderActions.cancelOrderFailure({ message: 'Cannot cancel an order that already has a fulfilled item' }));
    fixture.detectChanges();

    expect(document.body.textContent).toContain('Cannot cancel an order that already has a fulfilled item');
  });
});
