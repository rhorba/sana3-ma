import { createActionGroup, props } from '@ngrx/store';

import { CertificateResponse } from '../../core/certificate/certificate.models';

export const CertificateActions = createActionGroup({
  source: 'Certificate',
  events: {
    'Issue Certificate': props<{ productId: string }>(),
    'Issue Certificate Success': props<{ certificate: CertificateResponse }>(),
    'Issue Certificate Failure': props<{ message: string }>(),
  },
});
