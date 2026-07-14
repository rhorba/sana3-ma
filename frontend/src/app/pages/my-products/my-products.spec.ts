import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Action } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';

import { ProductResponse } from '../../core/catalog/catalog.models';
import { CatalogActions } from '../../store/catalog/catalog.actions';
import {
  selectCatalogError,
  selectCatalogLoaded,
  selectCatalogLoading,
  selectCatalogSaving,
  selectProducts,
} from '../../store/catalog/catalog.selectors';
import { CertificateActions } from '../../store/certificate/certificate.actions';
import {
  selectByProductId,
  selectIssuingProductId,
} from '../../store/certificate/certificate.selectors';
import { MyProducts } from './my-products';

describe('MyProducts', () => {
  let component: MyProducts;
  let fixture: ComponentFixture<MyProducts>;
  let store: MockStore;
  let actions$: Subject<Action>;

  const product: ProductResponse = {
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

  beforeEach(async () => {
    actions$ = new Subject<Action>();

    await TestBed.configureTestingModule({
      imports: [MyProducts],
      providers: [
        provideNoopAnimations(),
        provideMockActions((): Observable<Action> => actions$),
        provideMockStore({
          selectors: [
            { selector: selectProducts, value: [] },
            { selector: selectCatalogLoading, value: false },
            { selector: selectCatalogLoaded, value: false },
            { selector: selectCatalogSaving, value: false },
            { selector: selectCatalogError, value: null },
            { selector: selectByProductId, value: {} },
            { selector: selectIssuingProductId, value: null },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(MyProducts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and dispatch loadProducts on construction', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(CatalogActions.loadProducts());
  });

  it('shows a loading message while loading', () => {
    store.overrideSelector(selectCatalogLoading, true);
    createComponent();

    expect(fixture.nativeElement.textContent).toContain('Loading your products');
  });

  it('shows the empty-state prompt when loaded with no products', () => {
    store.overrideSelector(selectCatalogLoaded, true);
    store.overrideSelector(selectProducts, []);
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).not.toBeNull();
  });

  it('renders a card per product', () => {
    store.overrideSelector(selectCatalogLoaded, true);
    store.overrideSelector(selectProducts, [product]);
    createComponent();

    const cards = fixture.nativeElement.querySelectorAll('.product-card');
    expect(cards.length).toBe(1);
    expect(cards[0].textContent).toContain('Zellige Tile Set');
  });

  it('shows inline validation errors and does not dispatch when required fields are blank', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(dispatchSpy).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelectorAll('mat-error').length).toBeGreaterThan(0);
  });

  it('dispatches createProduct with the form value in add mode', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.form.setValue({
      name: 'Zellige Tile Set',
      description: 'Handmade blue zellige',
      priceAmount: 450,
      priceCurrency: 'MAD',
      craftType: 'Pottery',
    });
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(dispatchSpy).toHaveBeenCalledWith(
      CatalogActions.createProduct({
        name: 'Zellige Tile Set',
        description: 'Handmade blue zellige',
        priceAmount: 450,
        priceCurrency: 'MAD',
        craftType: 'Pottery',
      }),
    );
  });

  it('startEdit populates the form and submit dispatches updateProduct', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.startEdit(product);
    fixture.detectChanges();
    expect(component.form.getRawValue().name).toBe('Zellige Tile Set');

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(dispatchSpy).toHaveBeenCalledWith(
      CatalogActions.updateProduct({
        id: product.id,
        name: 'Zellige Tile Set',
        description: 'Handmade blue zellige',
        priceAmount: 450,
        priceCurrency: 'MAD',
        craftType: 'Pottery',
      }),
    );
  });

  it('deleteProduct dispatches deleteProduct after confirmation', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    vi.spyOn(window, 'confirm').mockReturnValue(true);

    component.deleteProduct(product);

    expect(dispatchSpy).toHaveBeenCalledWith(CatalogActions.deleteProduct({ id: product.id }));
  });

  it('deleteProduct does not dispatch when the user cancels the confirmation', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    vi.spyOn(window, 'confirm').mockReturnValue(false);

    component.deleteProduct(product);

    expect(dispatchSpy).not.toHaveBeenCalled();
  });

  it('shows a success snackbar when createProductSuccess fires', () => {
    createComponent();

    actions$.next(CatalogActions.createProductSuccess({ product }));
    fixture.detectChanges();

    expect(document.body.textContent).toContain('Product added');
  });

  it('shows "Issue Certificate" when none exists yet, and dispatches issueCertificate on click', () => {
    store.overrideSelector(selectCatalogLoaded, true);
    store.overrideSelector(selectProducts, [product]);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    const buttons = Array.from(fixture.nativeElement.querySelectorAll('button')) as HTMLButtonElement[];
    const issueButton = buttons.find((button) => button.textContent?.includes('Issue Certificate'));
    expect(issueButton).toBeTruthy();
    issueButton?.click();

    expect(dispatchSpy).toHaveBeenCalledWith(
      CertificateActions.issueCertificate({ productId: product.id }),
    );
  });

  it('shows the verification code and switches the button label once a certificate exists', () => {
    store.overrideSelector(selectCatalogLoaded, true);
    store.overrideSelector(selectProducts, [product]);
    store.overrideSelector(selectByProductId, {
      [product.id]: { id: 'cert-1', productId: product.id, issuedAt: '2026-01-01T00:00:00Z' },
    });
    createComponent();

    expect(fixture.nativeElement.querySelector('.verification-code')?.textContent).toContain('cert-1');
    const buttons = Array.from(fixture.nativeElement.querySelectorAll('button')) as HTMLButtonElement[];
    expect(buttons.some((button) => button.textContent?.includes('View Certificate'))).toBe(true);
  });

  it('renders a QR code and shows a snackbar when issueCertificateSuccess fires', async () => {
    store.overrideSelector(selectCatalogLoaded, true);
    store.overrideSelector(selectProducts, [product]);
    // The reducer itself is unit-tested separately (certificate.reducer.spec.ts) — this MockStore
    // doesn't run it, so the certificate-panel's "do we have one" check needs its own override to
    // render at all, same pattern the "switches the button label" test above already uses.
    store.overrideSelector(selectByProductId, {
      [product.id]: { id: 'cert-1', productId: product.id, issuedAt: '2026-01-01T00:00:00Z' },
    });
    createComponent();

    actions$.next(
      CertificateActions.issueCertificateSuccess({
        certificate: { id: 'cert-1', productId: product.id, issuedAt: '2026-01-01T00:00:00Z' },
      }),
    );
    fixture.detectChanges();
    // QR rendering is async (qrcode's toString resolves on a microtask).
    await fixture.whenStable();
    fixture.detectChanges();

    expect(document.body.textContent).toContain('Certificate issued');
    expect(fixture.nativeElement.querySelector('.qr-code svg')).not.toBeNull();
  });

  it('shows a snackbar with the backend message when issueCertificateFailure fires', () => {
    createComponent();

    actions$.next(CertificateActions.issueCertificateFailure({ message: "Couldn't issue a certificate" }));
    fixture.detectChanges();

    expect(document.body.textContent).toContain("Couldn't issue a certificate");
  });
});
