import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Action } from '@ngrx/store';
import { Observable, firstValueFrom, of, throwError } from 'rxjs';

import { CertificateService } from '../../core/certificate/certificate.service';
import { CertificateActions } from './certificate.actions';
import { CertificateEffects } from './certificate.effects';

describe('CertificateEffects', () => {
  const certificate = { id: 'cert-1', productId: 'product-1', issuedAt: '2026-01-01T00:00:00Z' };

  let actions$: Observable<Action>;
  let certificateService: { issue: ReturnType<typeof vi.fn> };
  let effects: CertificateEffects;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [
        CertificateEffects,
        provideMockActions(() => actions$),
        { provide: CertificateService, useValue: certificateService },
      ],
    });
    effects = TestBed.inject(CertificateEffects);
  }

  beforeEach(() => {
    certificateService = { issue: vi.fn() };
  });

  it('issueCertificate$ maps a successful issue to issueCertificateSuccess', async () => {
    certificateService.issue.mockReturnValue(of(certificate));
    actions$ = of(CertificateActions.issueCertificate({ productId: 'product-1' }));
    setup();

    const result = await firstValueFrom(effects.issueCertificate$);

    expect(certificateService.issue).toHaveBeenCalledWith('product-1');
    expect(result).toEqual(CertificateActions.issueCertificateSuccess({ certificate }));
  });

  it('issueCertificate$ falls back to a generic message on failure', async () => {
    certificateService.issue.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(CertificateActions.issueCertificate({ productId: 'product-1' }));
    setup();

    const result = await firstValueFrom(effects.issueCertificate$);

    expect(result).toEqual(
      CertificateActions.issueCertificateFailure({
        message: "Couldn't issue a certificate. Please try again.",
      }),
    );
  });

  it('issueCertificate$ maps a backend error to issueCertificateFailure with its message', async () => {
    certificateService.issue.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
            error: { error: { code: 'PRODUCT_NOT_FOUND', message: 'No product found', details: [] } },
          }),
      ),
    );
    actions$ = of(CertificateActions.issueCertificate({ productId: 'product-1' }));
    setup();

    const result = await firstValueFrom(effects.issueCertificate$);

    expect(result).toEqual(CertificateActions.issueCertificateFailure({ message: 'No product found' }));
  });
});
