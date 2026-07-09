import { Component, effect, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';

import { ArtisanProfileActions } from '../../store/artisan-profile/artisan-profile.actions';
import {
  selectHasProfile,
  selectProfileError,
  selectProfileForm,
  selectProfileLoaded,
  selectProfileLoading,
  selectProfileSaving,
} from '../../store/artisan-profile/artisan-profile.selectors';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatProgressSpinnerModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  private readonly formBuilder = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly actions$ = inject(Actions);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly loading = this.store.selectSignal(selectProfileLoading);
  protected readonly loaded = this.store.selectSignal(selectProfileLoaded);
  protected readonly saving = this.store.selectSignal(selectProfileSaving);
  protected readonly error = this.store.selectSignal(selectProfileError);
  protected readonly hasProfile = this.store.selectSignal(selectHasProfile);
  private readonly profileForm = this.store.selectSignal(selectProfileForm);

  readonly form = this.formBuilder.nonNullable.group({
    displayName: ['', [Validators.required, Validators.maxLength(150)]],
    craftType: ['', [Validators.required, Validators.maxLength(100)]],
    region: ['', [Validators.maxLength(100)]],
    bio: [''],
    contactPhone: ['', [Validators.maxLength(30)]],
  });

  constructor() {
    this.store.dispatch(ArtisanProfileActions.loadProfile());

    effect(() => {
      this.form.patchValue(this.profileForm());
    });

    effect(() => {
      const message = this.error();
      if (message) {
        this.snackBar.open(message, 'Dismiss', { duration: 5000 });
      }
    });

    this.actions$
      .pipe(ofType(ArtisanProfileActions.saveProfileSuccess), takeUntilDestroyed())
      .subscribe(() => this.snackBar.open('Profile updated', 'Dismiss', { duration: 3000 }));
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.store.dispatch(ArtisanProfileActions.saveProfile(this.form.getRawValue()));
  }
}
