import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { CertificateVerificationResponse } from '../../core/certificate/certificate.models';
import { CertificateActions } from '../../store/certificate/certificate.actions';
import {
  selectVerificationError,
  selectVerificationNotFound,
  selectVerificationResult,
  selectVerifying,
} from '../../store/certificate/certificate.selectors';
import { CertificateVerify } from './certificate-verify';

describe('CertificateVerify', () => {
  let component: CertificateVerify;
  let fixture: ComponentFixture<CertificateVerify>;
  let store: MockStore;

  const result: CertificateVerificationResponse = {
    artisanDisplayName: 'Atlas Coop',
    productName: 'Rug',
    craftType: 'Weaving',
    issuedAt: '2026-01-01T00:00:00Z',
  };

  async function configure(code: string): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [CertificateVerify],
      providers: [
        provideRouter([]),
        provideMockStore({
          selectors: [
            { selector: selectVerificationResult, value: null },
            { selector: selectVerifying, value: false },
            { selector: selectVerificationNotFound, value: false },
            { selector: selectVerificationError, value: null },
          ],
        }),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ code }) } },
        },
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  }

  function createComponent(): void {
    fixture = TestBed.createComponent(CertificateVerify);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('dispatches verifyCertificate with the route code on construction', async () => {
    await configure('some-code');
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(CertificateActions.verifyCertificate({ code: 'some-code' }));
  });

  it('shows a loading message while verifying', async () => {
    await configure('some-code');
    store.overrideSelector(selectVerifying, true);

    createComponent();

    expect(fixture.nativeElement.textContent).toContain('Verifying');
  });

  it('shows the certificate details for a valid code', async () => {
    await configure('some-code');
    store.overrideSelector(selectVerificationResult, result);

    createComponent();

    expect(fixture.nativeElement.querySelector('.verify-result.valid')).not.toBeNull();
    expect(fixture.nativeElement.textContent).toContain('Atlas Coop');
    expect(fixture.nativeElement.textContent).toContain('Rug');
  });

  it('shows an invalid-certificate message for an unknown code', async () => {
    await configure('unknown-code');
    store.overrideSelector(selectVerificationNotFound, true);

    createComponent();

    expect(fixture.nativeElement.querySelector('.verify-result.invalid')).not.toBeNull();
    expect(fixture.nativeElement.textContent).toContain('Not a valid certificate');
  });

  it('shows an error message on a real failure', async () => {
    await configure('some-code');
    store.overrideSelector(selectVerificationError, "Couldn't verify this certificate");

    createComponent();

    expect(fixture.nativeElement.querySelector('.error-prompt')?.textContent).toContain(
      "Couldn't verify this certificate",
    );
  });
});
