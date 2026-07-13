import { DatePipe } from '@angular/common';
import { Component, computed, effect, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Actions, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';

import { CooperativeMemberResponse } from '../../core/cooperative/cooperative.models';
import { selectUserId } from '../../store/auth/auth.selectors';
import { CooperativeActions } from '../../store/cooperative/cooperative.actions';
import {
  selectInviteSending,
  selectMembers,
  selectMembersError,
  selectMembersLoaded,
  selectMembersLoading,
} from '../../store/cooperative/cooperative.selectors';

@Component({
  selector: 'app-cooperative-members',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, DatePipe],
  templateUrl: './cooperative-members.html',
  styleUrl: './cooperative-members.scss',
})
export class CooperativeMembers {
  private readonly formBuilder = inject(FormBuilder);
  private readonly store = inject(Store);
  private readonly actions$ = inject(Actions);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly members = this.store.selectSignal(selectMembers);
  protected readonly loading = this.store.selectSignal(selectMembersLoading);
  protected readonly loaded = this.store.selectSignal(selectMembersLoaded);
  protected readonly error = this.store.selectSignal(selectMembersError);
  protected readonly inviteSending = this.store.selectSignal(selectInviteSending);
  private readonly currentUserId = this.store.selectSignal(selectUserId);

  protected readonly isOwner = computed(
    () => this.members().find((member) => member.userId === this.currentUserId())?.role === 'OWNER',
  );

  readonly inviteForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
  });

  constructor() {
    this.store.dispatch(CooperativeActions.loadMembers());

    effect(() => {
      const message = this.error();
      if (message) {
        this.snackBar.open(message, 'Dismiss', { duration: 5000 });
      }
    });

    this.actions$
      .pipe(ofType(CooperativeActions.inviteMemberSuccess), takeUntilDestroyed())
      .subscribe(() => {
        this.snackBar.open('Invite sent', 'Dismiss', { duration: 3000 });
        this.inviteForm.reset({ email: '' });
      });

    this.actions$
      .pipe(ofType(CooperativeActions.inviteMemberFailure), takeUntilDestroyed())
      .subscribe(({ message }) => this.snackBar.open(message, 'Dismiss', { duration: 5000 }));

    this.actions$
      .pipe(ofType(CooperativeActions.removeMemberFailure), takeUntilDestroyed())
      .subscribe(({ message }) => this.snackBar.open(message, 'Dismiss', { duration: 5000 }));
  }

  invite(): void {
    if (this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }
    this.store.dispatch(CooperativeActions.inviteMember(this.inviteForm.getRawValue()));
  }

  canRemove(member: CooperativeMemberResponse): boolean {
    if (member.role === 'OWNER') {
      return false;
    }
    return this.isOwner() || this.isSelf(member);
  }

  isSelf(member: CooperativeMemberResponse): boolean {
    return member.userId === this.currentUserId();
  }

  remove(member: CooperativeMemberResponse): void {
    const confirmMessage = this.isSelf(member)
      ? 'Leave this cooperative?'
      : `Remove ${member.email} from the cooperative?`;
    if (confirm(confirmMessage)) {
      this.store.dispatch(CooperativeActions.removeMember({ targetUserId: member.userId }));
    }
  }
}
