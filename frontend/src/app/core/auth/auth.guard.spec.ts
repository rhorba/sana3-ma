import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { selectIsAuthenticated } from '../../store/auth/auth.selectors';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let store: MockStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideMockStore()],
    });
    store = TestBed.inject(MockStore);
  });

  function runGuard(url: string) {
    return TestBed.runInInjectionContext(() =>
      authGuard({} as never, { url } as never),
    );
  }

  it('allows navigation when authenticated', () => {
    store.overrideSelector(selectIsAuthenticated, true);
    expect(runGuard('/profile')).toBe(true);
  });

  it('redirects to /login with a returnUrl when not authenticated', () => {
    store.overrideSelector(selectIsAuthenticated, false);
    const router = TestBed.inject(Router);
    const result = runGuard('/profile');

    expect(result).not.toBe(true);
    const tree = router.serializeUrl(result as ReturnType<Router['createUrlTree']>);
    expect(tree).toBe('/login?returnUrl=%2Fprofile');
  });
});
