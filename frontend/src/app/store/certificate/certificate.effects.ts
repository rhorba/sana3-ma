import { HttpErrorResponse } from '@angular/common/http';
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

  verifyCertificate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CertificateActions.verifyCertificate),
      switchMap(({ code }) =>
        this.certificateService.verify(code).pipe(
          map((result) => CertificateActions.verifyCertificateSuccess({ result })),
          catchError((error) => {
            if (error instanceof HttpErrorResponse && error.status === 404) {
              return of(CertificateActions.verifyCertificateNotFound());
            }
            return of(
              CertificateActions.verifyCertificateFailure({
                message: extractErrorMessage(error, "Couldn't verify this certificate. Please try again."),
              }),
            );
          }),
        ),
      ),
    ),
  );
}
