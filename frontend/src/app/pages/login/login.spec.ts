import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { AuthActions } from '../../store/auth/auth.actions';
import { selectAuthError, selectAuthLoading, selectIsAuthenticated, selectRole } from '../../store/auth/auth.selectors';
import { Login } from './login';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let store: MockStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { queryParamMap: { get: () => null } } } },
        provideMockStore({
          selectors: [
            { selector: selectAuthLoading, value: false },
            { selector: selectAuthError, value: null },
            { selector: selectIsAuthenticated, value: false },
            { selector: selectRole, value: null },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows inline validation errors and does not dispatch when the form is invalid', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(dispatchSpy).not.toHaveBeenCalled();
    const errors = fixture.nativeElement.querySelectorAll('mat-error');
    expect(errors.length).toBeGreaterThan(0);
  });

  it('dispatches AuthActions.login with the form value when valid', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    component.form.setValue({ email: 'a@b.com', password: 'password123' });
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(dispatchSpy).toHaveBeenCalledWith(
      AuthActions.login({ email: 'a@b.com', password: 'password123' }),
    );
  });

  it('redirects a buyer to / once authenticated', () => {
    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigateByUrl');

    store.overrideSelector(selectIsAuthenticated, true);
    store.overrideSelector(selectRole, 'BUYER');
    store.refreshState();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith('/');
  });

  it('redirects an artisan to /profile once authenticated', () => {
    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigateByUrl');

    store.overrideSelector(selectIsAuthenticated, true);
    store.overrideSelector(selectRole, 'ARTISAN');
    store.refreshState();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith('/profile');
  });
});
