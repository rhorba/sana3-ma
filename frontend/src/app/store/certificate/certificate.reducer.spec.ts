import { CertificateActions } from './certificate.actions';
import { certificateReducer } from './certificate.reducer';
import { initialCertificateState } from './certificate.state';

describe('certificateReducer', () => {
  const certificate = { id: 'cert-1', productId: 'product-1', issuedAt: '2026-01-01T00:00:00Z' };

  it('sets issuingProductId on issueCertificate', () => {
    const state = certificateReducer(
      initialCertificateState,
      CertificateActions.issueCertificate({ productId: 'product-1' }),
    );
    expect(state.issuingProductId).toBe('product-1');
  });

  it('stores the certificate keyed by productId and clears issuingProductId on success', () => {
    const state = certificateReducer(
      { ...initialCertificateState, issuingProductId: 'product-1' },
      CertificateActions.issueCertificateSuccess({ certificate }),
    );
    expect(state.byProductId['product-1']).toEqual(certificate);
    expect(state.issuingProductId).toBeNull();
  });

  it('records an error message and clears issuingProductId on failure', () => {
    const state = certificateReducer(
      { ...initialCertificateState, issuingProductId: 'product-1' },
      CertificateActions.issueCertificateFailure({ message: 'boom' }),
    );
    expect(state.error).toBe('boom');
    expect(state.issuingProductId).toBeNull();
  });
});
