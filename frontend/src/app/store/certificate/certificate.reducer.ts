import { createFeature, createReducer, on } from '@ngrx/store';

import { CertificateActions } from './certificate.actions';
import { CertificateState, initialCertificateState } from './certificate.state';

export const certificateFeature = createFeature({
  name: 'certificate',
  reducer: createReducer(
    initialCertificateState,
    on(CertificateActions.issueCertificate, (state, { productId }): CertificateState => ({
      ...state,
      issuingProductId: productId,
      error: null,
    })),
    on(
      CertificateActions.issueCertificateSuccess,
      (state, { certificate }): CertificateState => ({
        ...state,
        byProductId: { ...state.byProductId, [certificate.productId]: certificate },
        issuingProductId: null,
      }),
    ),
    on(CertificateActions.issueCertificateFailure, (state, { message }): CertificateState => ({
      ...state,
      issuingProductId: null,
      error: message,
    })),

    on(CertificateActions.verifyCertificate, (state): CertificateState => ({
      ...state,
      verifying: true,
      verificationResult: null,
      verificationNotFound: false,
      verificationError: null,
    })),
    on(CertificateActions.verifyCertificateSuccess, (state, { result }): CertificateState => ({
      ...state,
      verifying: false,
      verificationResult: result,
    })),
    on(CertificateActions.verifyCertificateNotFound, (state): CertificateState => ({
      ...state,
      verifying: false,
      verificationNotFound: true,
    })),
    on(CertificateActions.verifyCertificateFailure, (state, { message }): CertificateState => ({
      ...state,
      verifying: false,
      verificationError: message,
    })),
  ),
});

export const {
  name: certificateFeatureKey,
  reducer: certificateReducer,
  selectByProductId,
  selectIssuingProductId,
  selectError,
  selectVerificationResult,
  selectVerifying,
  selectVerificationNotFound,
  selectVerificationError,
} = certificateFeature;
