import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

import { CertificateService } from '../../core/certificate/certificate.service';
import { extractErrorMessage } from '../../core/http-error.util';
import { CertificateActions } from './certificate.actions';

@Injectable()
export class CertificateEffects {
  private readonly actions$ = inject(Actions);
  private readonly certificateService = inject(CertificateService);

  issueCertificate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CertificateActions.issueCertificate),
      switchMap(({ productId }) =>
        this.certificateService.issue(productId).pipe(
          map((certificate) => CertificateActions.issueCertificateSuccess({ certificate })),
          catchError((error) =>
            of(
              CertificateActions.issueCertificateFailure({
                message: extractErrorMessage(error, "Couldn't issue a certificate. Please try again."),
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
