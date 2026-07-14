import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { CertificateResponse, CertificateVerificationResponse } from '../../core/certificate/certificate.models';

export const CertificateActions = createActionGroup({
  source: 'Certificate',
  events: {
    'Issue Certificate': props<{ productId: string }>(),
    'Issue Certificate Success': props<{ certificate: CertificateResponse }>(),
    'Issue Certificate Failure': props<{ message: string }>(),

    'Verify Certificate': props<{ code: string }>(),
    'Verify Certificate Success': props<{ result: CertificateVerificationResponse }>(),
    // An unknown/malformed code is a normal empty state (like a missing product), not an error.
    'Verify Certificate Not Found': emptyProps(),
    'Verify Certificate Failure': props<{ message: string }>(),
  },
});
