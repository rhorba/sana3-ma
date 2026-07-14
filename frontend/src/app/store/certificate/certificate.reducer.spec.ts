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

  const verificationResult = {
    artisanDisplayName: 'Atlas Coop',
    productName: 'Rug',
    craftType: 'Weaving',
    issuedAt: '2026-01-01T00:00:00Z',
  };

  it('sets verifying and clears prior results on verifyCertificate', () => {
    const state = certificateReducer(
      { ...initialCertificateState, verificationResult, verificationNotFound: true },
      CertificateActions.verifyCertificate({ code: 'some-code' }),
    );
    expect(state.verifying).toBe(true);
    expect(state.verificationResult).toBeNull();
    expect(state.verificationNotFound).toBe(false);
  });

  it('stores the result and clears verifying on verifyCertificateSuccess', () => {
    const state = certificateReducer(
      { ...initialCertificateState, verifying: true },
      CertificateActions.verifyCertificateSuccess({ result: verificationResult }),
    );
    expect(state.verificationResult).toEqual(verificationResult);
    expect(state.verifying).toBe(false);
  });

  it('sets verificationNotFound on verifyCertificateNotFound', () => {
    const state = certificateReducer(
      { ...initialCertificateState, verifying: true },
      CertificateActions.verifyCertificateNotFound(),
    );
    expect(state.verificationNotFound).toBe(true);
    expect(state.verifying).toBe(false);
  });

  it('records a verification error message on verifyCertificateFailure', () => {
    const state = certificateReducer(
      { ...initialCertificateState, verifying: true },
      CertificateActions.verifyCertificateFailure({ message: 'boom' }),
    );
    expect(state.verificationError).toBe('boom');
    expect(state.verifying).toBe(false);
  });
});
