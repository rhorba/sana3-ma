import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Store } from '@ngrx/store';

import { selectIsAuthenticated, selectRole } from '../../store/auth/auth.selectors';

// /checkout requires being logged in but not any specific role (Sprint 3 Assumed Default #3 —
// an artisan can buy another artisan's product) — plain authentication, no role check.
export const authGuard: CanActivateFn = (_route, state) => {
  const store = inject(Store);
  const router = inject(Router);

  if (!store.selectSignal(selectIsAuthenticated)()) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }

  return true;
};

// /profile is ARTISAN only (docs/ux-sana3-ma.md site map) — buyers are redirected home rather
// than reaching a form that would 403 on save.
export const artisanGuard: CanActivateFn = (_route, state) => {
  const store = inject(Store);
  const router = inject(Router);

  if (!store.selectSignal(selectIsAuthenticated)()) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }

  if (store.selectSignal(selectRole)() !== 'ARTISAN') {
    return router.createUrlTree(['/']);
  }

  return true;
};
