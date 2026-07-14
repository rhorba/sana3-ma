import { createSelector } from '@ngrx/store';

import { certificateFeature } from './certificate.reducer';

export const {
  selectByProductId,
  selectIssuingProductId,
  selectError: selectCertificateError,
  selectVerificationResult,
  selectVerifying,
  selectVerificationNotFound,
  selectVerificationError,
} = certificateFeature;

export function selectCertificateForProduct(productId: string) {
  return createSelector(selectByProductId, (byProductId) => byProductId[productId] ?? null);
}
