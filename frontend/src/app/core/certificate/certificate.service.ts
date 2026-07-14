import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { CertificateResponse, CertificateVerificationResponse } from './certificate.models';

@Injectable({ providedIn: 'root' })
export class CertificateService {
  private readonly http = inject(HttpClient);

  issue(productId: string): Observable<CertificateResponse> {
    return this.http.post<CertificateResponse>(
      `${API_BASE_URL}/artisan-profiles/me/products/${productId}/certificate`,
      {},
    );
  }

  verify(code: string): Observable<CertificateVerificationResponse> {
    return this.http.get<CertificateVerificationResponse>(
      `${API_BASE_URL}/certificates/verify/${code}`,
    );
  }
}
