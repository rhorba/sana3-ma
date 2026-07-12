import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Action } from '@ngrx/store';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Observable, Subject } from 'rxjs';

import { ArtisanOrderItemResponse } from '../../core/order/order.models';
import { OrderActions } from '../../store/order/order.actions';
import {
  selectArtisanOrderItems,
  selectArtisanOrderItemsError,
  selectArtisanOrderItemsLoaded,
  selectArtisanOrderItemsLoading,
} from '../../store/order/order.selectors';
import { ArtisanOrders } from './artisan-orders';

describe('ArtisanOrders', () => {
  let component: ArtisanOrders;
  let fixture: ComponentFixture<ArtisanOrders>;
  let store: MockStore;
  let actions$: Subject<Action>;

  const item: ArtisanOrderItemResponse = {
    id: 'item-1',
    orderId: 'order-1',
    orderStatus: 'PLACED',
    shippingAddress: '123 Rue Example, Fes',
    buyerEmail: 'buyer@example.com',
    productId: 'product-1',
    productName: 'Zellige Tile Set',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
    quantity: 2,
    lineTotal: 900,
    completed: false,
    completedAt: null,
    orderCreatedAt: '2026-01-01T00:00:00Z',
  };

  beforeEach(async () => {
    actions$ = new Subject<Action>();

    await TestBed.configureTestingModule({
      imports: [ArtisanOrders],
      providers: [
        provideNoopAnimations(),
        provideMockActions((): Observable<Action> => actions$),
        provideMockStore({
          selectors: [
            { selector: selectArtisanOrderItems, value: [] },
            { selector: selectArtisanOrderItemsLoading, value: false },
            { selector: selectArtisanOrderItemsLoaded, value: false },
            { selector: selectArtisanOrderItemsError, value: null },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(ArtisanOrders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and dispatch listArtisanOrderItems on construction', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(OrderActions.listArtisanOrderItems());
  });

  it('shows the empty-state prompt when loaded with no items', () => {
    store.overrideSelector(selectArtisanOrderItemsLoaded, true);
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).not.toBeNull();
  });

  it('renders a card per item with buyer/shipping info and a Mark completed action', () => {
    store.overrideSelector(selectArtisanOrderItems, [item]);
    createComponent();

    const cards = fixture.nativeElement.querySelectorAll('.item-card');
    expect(cards.length).toBe(1);
    expect(cards[0].textContent).toContain('buyer@example.com');
    expect(cards[0].textContent).toContain('123 Rue Example, Fes');
    expect(cards[0].querySelector('button')).not.toBeNull();
  });

  it('shows a completed badge instead of the action for a completed item', () => {
    store.overrideSelector(selectArtisanOrderItems, [{ ...item, completed: true }]);
    createComponent();

    expect(fixture.nativeElement.querySelector('.completed-badge')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('.item-card button')).toBeNull();
  });

  it('complete dispatches completeArtisanOrderItem', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.complete(item);

    expect(dispatchSpy).toHaveBeenCalledWith(OrderActions.completeArtisanOrderItem({ id: item.id }));
  });

  it('shows a snackbar with the backend message when completeArtisanOrderItemFailure fires', () => {
    createComponent();

    actions$.next(OrderActions.completeArtisanOrderItemFailure({ message: 'Order item is already completed' }));
    fixture.detectChanges();

    expect(document.body.textContent).toContain('Order item is already completed');
  });
});
