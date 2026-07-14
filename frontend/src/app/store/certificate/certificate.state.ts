import { CertificateResponse, CertificateVerificationResponse } from '../../core/certificate/certificate.models';

export interface CertificateState {
  byProductId: Record<string, CertificateResponse>;
  issuingProductId: string | null;
  error: string | null;

  verificationResult: CertificateVerificationResponse | null;
  verifying: boolean;
  verificationNotFound: boolean;
  verificationError: string | null;
}

export const initialCertificateState: CertificateState = {
  byProductId: {},
  issuingProductId: null,
  error: null,

  verificationResult: null,
  verifying: false,
  verificationNotFound: false,
  verificationError: null,
};
