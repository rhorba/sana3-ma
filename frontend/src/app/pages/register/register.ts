import { Component, effect, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';

import { RegistrableRole } from '../../core/auth/auth.models';
import { AuthActions } from '../../store/auth/auth.actions';
import { selectAuthError, selectAuthLoading, selectIsAuthenticated } from '../../store/auth/auth.selectors';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatRadioModule,
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private readonly formBuilder = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly loading = this.store.selectSignal(selectAuthLoading);
  protected readonly error = this.store.selectSignal(selectAuthError);
  private readonly isAuthenticated = this.store.selectSignal(selectIsAuthenticated);

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(10)]],
    role: this.formBuilder.nonNullable.control<RegistrableRole>('BUYER', Validators.required),
  });

  constructor() {
    effect(() => {
      if (this.isAuthenticated()) {
        this.router.navigateByUrl('/profile');
      }
    });

    effect(() => {
      const message = this.error();
      if (message) {
        this.snackBar.open(message, 'Dismiss', { duration: 5000 });
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.store.dispatch(AuthActions.register(this.form.getRawValue()));
  }
}
