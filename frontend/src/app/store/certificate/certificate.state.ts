import { CertificateResponse } from '../../core/certificate/certificate.models';

export interface CertificateState {
  byProductId: Record<string, CertificateResponse>;
  issuingProductId: string | null;
  error: string | null;
}

export const initialCertificateState: CertificateState = {
  byProductId: {},
  issuingProductId: null,
  error: null,
};
