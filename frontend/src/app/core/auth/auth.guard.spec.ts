import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { selectIsAuthenticated, selectRole } from '../../store/auth/auth.selectors';
import { artisanGuard } from './auth.guard';

describe('artisanGuard', () => {
  let store: MockStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideMockStore()],
    });
    store = TestBed.inject(MockStore);
  });

  function runGuard(url: string) {
    return TestBed.runInInjectionContext(() => artisanGuard({} as never, { url } as never));
  }

  it('allows navigation when authenticated as an artisan', () => {
    store.overrideSelector(selectIsAuthenticated, true);
    store.overrideSelector(selectRole, 'ARTISAN');
    expect(runGuard('/profile')).toBe(true);
  });

  it('redirects to /login with a returnUrl when not authenticated', () => {
    store.overrideSelector(selectIsAuthenticated, false);
    store.overrideSelector(selectRole, null);
    const router = TestBed.inject(Router);
    const result = runGuard('/profile');

    expect(result).not.toBe(true);
    const tree = router.serializeUrl(result as ReturnType<Router['createUrlTree']>);
    expect(tree).toBe('/login?returnUrl=%2Fprofile');
  });

  it('redirects to / when authenticated as a non-artisan (buyer)', () => {
    store.overrideSelector(selectIsAuthenticated, true);
    store.overrideSelector(selectRole, 'BUYER');
    const router = TestBed.inject(Router);
    const result = runGuard('/profile');

    expect(result).not.toBe(true);
    const tree = router.serializeUrl(result as ReturnType<Router['createUrlTree']>);
    expect(tree).toBe('/');
  });
});
