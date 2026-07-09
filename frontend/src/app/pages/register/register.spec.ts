import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

import { AuthActions } from '../../store/auth/auth.actions';
import { selectAuthError, selectAuthLoading, selectIsAuthenticated } from '../../store/auth/auth.selectors';
import { Register } from './register';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let store: MockStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        provideMockStore({
          selectors: [
            { selector: selectAuthLoading, value: false },
            { selector: selectAuthError, value: null },
            { selector: selectIsAuthenticated, value: false },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('defaults the role to BUYER', () => {
    expect(component.form.controls.role.value).toBe('BUYER');
  });

  it('shows inline validation errors and does not dispatch when the form is invalid', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    component.form.controls.password.setValue('short');
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(dispatchSpy).not.toHaveBeenCalled();
    const errors = fixture.nativeElement.querySelectorAll('mat-error');
    expect(errors.length).toBeGreaterThan(0);
  });

  it('dispatches AuthActions.register with the form value when valid', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    component.form.setValue({ email: 'a@b.com', password: 'password123', role: 'ARTISAN' });
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(dispatchSpy).toHaveBeenCalledWith(
      AuthActions.register({ email: 'a@b.com', password: 'password123', role: 'ARTISAN' }),
    );
  });
});
