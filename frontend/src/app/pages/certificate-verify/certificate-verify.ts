import { DatePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { CertificateActions } from '../../store/certificate/certificate.actions';
import {
  selectVerificationError,
  selectVerificationNotFound,
  selectVerificationResult,
  selectVerifying,
} from '../../store/certificate/certificate.selectors';

@Component({
  selector: 'app-certificate-verify',
  imports: [RouterLink, DatePipe],
  templateUrl: './certificate-verify.html',
  styleUrl: './certificate-verify.scss',
})
export class CertificateVerify {
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(Store);

  protected readonly result = this.store.selectSignal(selectVerificationResult);
  protected readonly verifying = this.store.selectSignal(selectVerifying);
  protected readonly notFound = this.store.selectSignal(selectVerificationNotFound);
  protected readonly error = this.store.selectSignal(selectVerificationError);

  constructor() {
    const code = this.route.snapshot.paramMap.get('code');
    if (code) {
      this.store.dispatch(CertificateActions.verifyCertificate({ code }));
    }
  }
}
